package com.billiards_club_tuan_hung.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.billiards_club_tuan_hung.entity.OrderItem;
import com.billiards_club_tuan_hung.service.OrderItemService;

@RestController
@CrossOrigin
@RequestMapping("/api/order-item")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // Lấy tất cả order items
    @GetMapping
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        List<OrderItem> items = orderItemService.getAllOrderItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    // Lấy order item theo ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable long id) {
        try {
            OrderItem item = orderItemService.getOrderItemById(id);
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Tạo mới order item
    @PostMapping
    public ResponseEntity<OrderItem> createOrderItem(@Valid @RequestBody OrderItem orderItem) {
        try {
            OrderItem created = orderItemService.createOrderItem(orderItem);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Cập nhật order item
    @PutMapping("/{id}")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable long id, @Valid @RequestBody OrderItem orderItem) {
        try {
            OrderItem updated = orderItemService.updateOrderItem(id, orderItem);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Xoá order item
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable long id) {
        try {
            orderItemService.deleteOrderItem(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lấy tất cả order items theo orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable long orderId) {
        try {
            List<OrderItem> items = orderItemService.getOrderItemsByOrderId(orderId);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Lấy tất cả order items theo menuItemId
    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<OrderItem>> getOrderItemsByMenuItemId(@PathVariable long menuItemId) {
        try {
            List<OrderItem> items = orderItemService.getOrderItemsByMenuItemId(menuItemId);
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
