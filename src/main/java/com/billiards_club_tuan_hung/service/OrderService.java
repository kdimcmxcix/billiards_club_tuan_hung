package com.billiards_club_tuan_hung.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billiards_club_tuan_hung.entity.BilliardsTable;
import com.billiards_club_tuan_hung.entity.MenuItem;
import com.billiards_club_tuan_hung.entity.Order;
import com.billiards_club_tuan_hung.entity.OrderItem;
import com.billiards_club_tuan_hung.entity.User;
import com.billiards_club_tuan_hung.repository.BilliardsTableRepository;
import com.billiards_club_tuan_hung.repository.OrderRepository;
import com.billiards_club_tuan_hung.repository.UserRepository;

@Service
public class OrderService {

    @Autowired
    private WorkingSessionService workingSessionService;

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BilliardsTableRepository tableRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy order với id: " + id));
    }

    public Order createOrder(Order order) {
        if (order.getStaff() == null || order.getStaff().getId() == null) {
            throw new IllegalArgumentException("Thiếu staff id");
        }
        if (order.getBilliardsTable() == null || order.getBilliardsTable().getId() == 0) {
            throw new IllegalArgumentException("Thiếu bàn id");
        }

        User staff = userRepository.findById(order.getStaff().getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        BilliardsTable table = tableRepository.findById(order.getBilliardsTable().getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn"));

        order.setStaff(staff);
        order.setBilliardsTable(table);
        order.setCreatedAt(order.getCreatedAt() != null ? order.getCreatedAt() : java.time.LocalDateTime.now());

        // Tính tiền
        double foodTotal = order.getOrderItems() != null
                ? order.getOrderItems().stream().mapToDouble(i -> i.getQuantity() * i.getUnitPrice()).sum()
                : 0;

        double playingFee = 0;
        if (order.getStartTime() != null && order.getEndTime() != null
                && table.getPricePerHour() != null) {
            Duration duration = Duration.between(order.getStartTime(), order.getEndTime());
            double hours = duration.toMinutes() / 60.0;
            playingFee = hours * table.getPricePerHour();
        }

        double total = foodTotal + playingFee;

        Order savedOrder = orderRepository.save(order);

        // ✅ Đánh dấu bàn đang được sử dụng
        table.setInUse(true);
        tableRepository.save(table);

        // ✅ Cộng tiền nếu đã thanh toán
        if (savedOrder.getIsPaid() != null && savedOrder.getIsPaid()
                && "cash".equalsIgnoreCase(savedOrder.getPaymentMethod())) {
            workingSessionService.addCashToCurrentSession(savedOrder.getStaff(), total);
        }

        return savedOrder;
    }

    public Map<String, Double> calculateOrderTotals(Order order) {
        double foodTotal = order.getOrderItems().stream()
                .mapToDouble(i -> i.getUnitPrice() * i.getQuantity())
                .sum();

        double playingFee = 0.0;

        if (order.getStartTime() != null && order.getBilliardsTable() != null) {
            LocalDateTime endTime = order.getEndTime() != null ? order.getEndTime() : LocalDateTime.now();
            Duration duration = Duration.between(order.getStartTime(), endTime);

            double minutes = duration.toMinutes();
            double hours = minutes / 60.0;
            double roundedHours = Math.ceil(hours); // ✅ làm tròn lên
            double pricePerHour = order.getBilliardsTable().getPricePerHour();

            playingFee = roundedHours * pricePerHour;
        }

        double totalAmount = foodTotal + playingFee;

        Map<String, Double> result = new HashMap<>();
        result.put("foodTotal", foodTotal);
        result.put("playingFee", playingFee);
        result.put("totalAmount", totalAmount);
        return result;
    }

    public Order updateOrder(long id, Order updatedOrder) {
        Order existingOrder = getOrderById(id);

        existingOrder.setCreatedAt(updatedOrder.getCreatedAt());
        existingOrder.setStartTime(updatedOrder.getStartTime());
        existingOrder.setEndTime(updatedOrder.getEndTime());
        existingOrder.setIsPaid(updatedOrder.getIsPaid());

        if (updatedOrder.getStaff() != null && updatedOrder.getStaff().getId() != null) {
            User staff = userRepository.findById(updatedOrder.getStaff().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
            existingOrder.setStaff(staff);
        }

        if (updatedOrder.getBilliardsTable() != null && updatedOrder.getBilliardsTable().getId() != 0) {
            BilliardsTable table = tableRepository.findById(updatedOrder.getBilliardsTable().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn"));
            existingOrder.setBilliardsTable(table);
        }

        if (updatedOrder.getOrderItems() != null) {
            for (OrderItem item : updatedOrder.getOrderItems()) {
                item.setOrder(existingOrder);
            }
            existingOrder.setOrderItems(updatedOrder.getOrderItems());
        }

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(long id) {
        Order existingOrder = getOrderById(id);
        orderRepository.delete(existingOrder);
    }

    public List<Order> getOrdersByTableId(long tableId) {
        return orderRepository.findByBilliardsTableId(tableId);
    }

    public Order getUnpaidOrderByTableId(long tableId) {
        return orderRepository.findByBilliardsTableIdAndIsPaidFalse(tableId)
                .orElse(null);
    }

    public void finishOrder(long orderId, LocalDateTime endTime, String paymentMethod, Long staffId) {
        Order order = getOrderById(orderId);
        BilliardsTable table = order.getBilliardsTable();

        order.setEndTime(endTime);
        order.setIsPaid(true);
        order.setPaymentMethod(paymentMethod);

        // ✅ Nếu chưa có staff thì gán từ staffId
        if (order.getStaff() == null && staffId != null) {
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + staffId));
            order.setStaff(staff);
        }

        // ✅ Trừ kho khi kết thúc đơn
        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                MenuItem menuItem = item.getMenuItem();
                if (menuItem.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalArgumentException("Không đủ hàng tồn cho món: " + menuItem.getName());
                }
                menuItem.setStockQuantity(menuItem.getStockQuantity() - item.getQuantity());
                menuItemService.save(menuItem);
            }
        }

        // ✅ Nếu thanh toán bằng cash thì cộng tiền
        if ("cash".equalsIgnoreCase(paymentMethod)) {
            if (order.getStaff() == null || order.getStaff().getId() == null) {
                if (staffId == null) {
                    throw new IllegalArgumentException("Thiếu nhân viên để cộng tiền vào phiên làm việc.");
                }

                User staff = userRepository.findById(staffId)
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên với ID: " + staffId));
                order.setStaff(staff);
            }

            Map<String, Double> totals = calculateOrderTotals(order);
            double totalAmount = totals.getOrDefault("totalAmount", 0.0);
            workingSessionService.addCashToCurrentSession(order.getStaff(), totalAmount);
        }

        // ✅ Bàn không còn in use
        table.setInUse(false);
        tableRepository.save(table);

        orderRepository.save(order);

    }

}
