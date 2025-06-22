package com.billiards_club_tuan_hung.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billiards_club_tuan_hung.entity.MenuItem;
import com.billiards_club_tuan_hung.entity.OrderItem;
import com.billiards_club_tuan_hung.repository.MenuItemRepository;
import com.billiards_club_tuan_hung.repository.OrderItemRepository;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    // Lấy tất cả order item
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    // Lấy order item theo ID
    public OrderItem getOrderItemById(long id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy orderItem với id: " + id));
    }

    // Tạo order item (gán unitPrice từ menuItem)
    public OrderItem createOrderItem(OrderItem orderItem) {
        // Lấy thông tin menuItem từ DB
        MenuItem menuItem = menuItemRepository.findById(orderItem.getMenuItem().getId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy menuItem với id: " + orderItem.getMenuItem().getId()));

        // Gán lại menuItem và giá đơn vị
        orderItem.setMenuItem(menuItem);
        orderItem.setUnitPrice(menuItem.getPrice());

        return orderItemRepository.save(orderItem);
    }

    // Cập nhật order item
    public OrderItem updateOrderItem(long id, OrderItem updatedItem) {
        OrderItem existingItem = getOrderItemById(id);

        existingItem.setQuantity(updatedItem.getQuantity());

        // Gán lại menuItem và unitPrice nếu menuItemId thay đổi
        if (updatedItem.getMenuItem() != null && updatedItem.getMenuItem().getId() != existingItem.getMenuItem().getId()) {
            MenuItem menuItem = menuItemRepository.findById(updatedItem.getMenuItem().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy menuItem với id: " + updatedItem.getMenuItem().getId()));
            existingItem.setMenuItem(menuItem);
            existingItem.setUnitPrice(menuItem.getPrice());
        }

        return orderItemRepository.save(existingItem);
    }

    // Xoá order item
    public void deleteOrderItem(long id) {
        OrderItem existingItem = getOrderItemById(id);
        orderItemRepository.delete(existingItem);
    }

    // Lọc theo order
    public List<OrderItem> getOrderItemsByOrderId(long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    // Lọc theo menu item
    public List<OrderItem> getOrderItemsByMenuItemId(long menuItemId) {
        return orderItemRepository.findByMenuItemId(menuItemId);
    }
}
