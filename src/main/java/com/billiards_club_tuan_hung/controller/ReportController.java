package com.billiards_club_tuan_hung.controller;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.billiards_club_tuan_hung.entity.Order;
import com.billiards_club_tuan_hung.service.OrderService;
import com.billiards_club_tuan_hung.repository.OrderRepository;

@RestController
@CrossOrigin
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @GetMapping("/revenue")
    public List<Map<String, Object>> getRevenueByTimeRange(
            @RequestParam String type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<Order> orders = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null &&
                        o.getIsPaid() != null && o.getIsPaid() &&
                        !o.getCreatedAt().toLocalDate().isBefore(from) &&
                        !o.getCreatedAt().toLocalDate().isAfter(to))
                .collect(Collectors.toList());

        Map<String, Double> revenueMap = new TreeMap<>();

        for (Order order : orders) {
            String key;
            switch (type.toLowerCase()) {
                case "hour":
                    key = order.getCreatedAt().getHour() + "h";
                    break;
                case "day":
                    key = order.getCreatedAt().getDayOfWeek().name();
                    break;
                case "date":
                    key = order.getCreatedAt().toLocalDate().toString();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid type: " + type);
            }

            double total = orderService.calculateOrderTotals(order).get("totalAmount");
            revenueMap.put(key, revenueMap.getOrDefault(key, 0.0) + total);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (var entry : revenueMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("label", entry.getKey());
            item.put("revenue", entry.getValue());
            result.add(item);
        }

        return result;
    }

    @GetMapping("/orders/by-date")
    public List<Map<String, Object>> getOrdersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt() != null &&
                        o.getIsPaid() != null && o.getIsPaid() &&
                        o.getCreatedAt().toLocalDate().isEqual(date))
                .map(o -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", o.getId());
                    map.put("billiardsTableName", o.getBilliardsTable().getTableName());

                    // Chuyển thời gian về định dạng ISO để JS xử lý
                    map.put("startTime", o.getStartTime().toString());
                    map.put("endTime", o.getEndTime() != null ? o.getEndTime().toString() : "");
                    map.put("paymentMethod", o.getPaymentMethod());

                    Map<String, Double> totals = orderService.calculateOrderTotals(o);
                    map.putAll(totals);

                    // Thêm danh sách món gọi
                    List<Map<String, Object>> itemList = o.getOrderItems().stream().map(i -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("name", i.getMenuItem().getName()); // hoặc i.getName() nếu OrderItem chứa sẵn
                        itemMap.put("quantity", i.getQuantity());
                        itemMap.put("unitPrice", i.getUnitPrice());
                        return itemMap;
                    }).collect(Collectors.toList());
                    map.put("items", itemList);

                    return map;
                })
                .collect(Collectors.toList());
    }

}
