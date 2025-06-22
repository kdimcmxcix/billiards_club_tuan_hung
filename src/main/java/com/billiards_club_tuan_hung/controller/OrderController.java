package com.billiards_club_tuan_hung.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.billiards_club_tuan_hung.entity.Order;
import com.billiards_club_tuan_hung.service.OrderService;

@RestController
@CrossOrigin
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            return ResponseEntity.ok(orderService.getAllOrders());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(orderService.getOrderById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable long id, @RequestBody Order order) {
        try {
            return ResponseEntity.ok(orderService.updateOrder(id, order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable long id) {
        try {
            orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<Order>> getOrdersByTableId(@PathVariable long tableId) {
        try {
            return ResponseEntity.ok(orderService.getOrdersByTableId(tableId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/table/{tableId}/unpaid")
    public ResponseEntity<Order> getUnpaidOrderByTableId(@PathVariable long tableId) {
        try {
            Order order = orderService.getUnpaidOrderByTableId(tableId);
            return order != null ? ResponseEntity.ok(order) : ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/table/{tableId}/totals")
    public ResponseEntity<List<Map<String, Object>>> getOrdersWithTotalsByTable(@PathVariable long tableId) {
        try {
            List<Order> orders = orderService.getOrdersByTableId(tableId);
            List<Map<String, Object>> result = orders.stream().map(order -> {
                Map<String, Double> totals = orderService.calculateOrderTotals(order);
                Map<String, Object> map = new HashMap<>();
                map.put("order", order);
                map.putAll(totals);
                return map;
            }).toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/table/{tableId}/unpaid/total")
    public ResponseEntity<Map<String, Object>> getUnpaidOrderWithTotal(@PathVariable long tableId) {
        try {
            Order unpaidOrder = orderService.getUnpaidOrderByTableId(tableId);
            if (unpaidOrder == null) {
                return ResponseEntity.noContent().build();
            }
            Map<String, Double> totals = orderService.calculateOrderTotals(unpaidOrder);
            Map<String, Object> response = new HashMap<>();
            response.put("order", unpaidOrder);
            response.putAll(totals);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<String> finishOrder(
            @PathVariable("id") long orderId,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("staffId") Long staffId) {
        try {
            orderService.finishOrder(orderId, endTime, paymentMethod, staffId);
            return ResponseEntity.ok("Đã tính tiền và kết thúc đơn hàng.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lỗi khi kết thúc đơn hàng: " + e.getMessage());
        }
    }

}
