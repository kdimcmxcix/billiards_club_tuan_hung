package com.billiards_club_tuan_hung.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.billiards_club_tuan_hung.entity.MenuItem;
import com.billiards_club_tuan_hung.service.MenuItemService;

@RestController
@CrossOrigin
@RequestMapping("/api/menu-item")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        try {
            return new ResponseEntity<>(menuItemService.getAllMenuItems(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable long id) {
        try {
            return new ResponseEntity<>(menuItemService.getMenuItemById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem item) {
        try {
            if (menuItemService.existsByName(item.getName())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(menuItemService.createMenuItem(item), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable long id, @RequestBody MenuItem item) {
        try {
            return new ResponseEntity<>(menuItemService.updateMenuItem(id, item), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable long id) {
        try {
            menuItemService.deleteMenuItem(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/category")
    public ResponseEntity<List<MenuItem>> getByCategory(@RequestParam String category) {
        try {
            return new ResponseEntity<>(menuItemService.getMenuItemsByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchByName(@RequestParam String keyword) {
        try {
            return new ResponseEntity<>(menuItemService.searchByName(keyword), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
        return new ResponseEntity<>(menuItemService.existsByName(name), HttpStatus.OK);
    }
}
