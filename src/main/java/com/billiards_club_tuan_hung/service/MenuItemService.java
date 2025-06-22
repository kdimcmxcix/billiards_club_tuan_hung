package com.billiards_club_tuan_hung.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billiards_club_tuan_hung.entity.MenuItem;
import com.billiards_club_tuan_hung.repository.MenuItemRepository;

@Service
public class MenuItemService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // Lấy tất cả menu items
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    // Lấy menu item theo ID
    public MenuItem getMenuItemById(long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy MenuItem với id: " + id));
    }

    // Tạo mới
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    // Cập nhật menu item
    public MenuItem updateMenuItem(long id, MenuItem updatedMenuItem) {
        MenuItem existing = getMenuItemById(id);
        existing.setName(updatedMenuItem.getName());
        existing.setPrice(updatedMenuItem.getPrice());
        existing.setCategory(updatedMenuItem.getCategory());
        existing.setStockQuantity(updatedMenuItem.getStockQuantity());
        return menuItemRepository.save(existing);
    }

    // Xoá
    public void deleteMenuItem(long id) {
        MenuItem existing = getMenuItemById(id);
        menuItemRepository.delete(existing);
    }

    // Tìm theo danh mục (nước / đồ ăn)
    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategoryIgnoreCase(category);
    }

    // Tìm theo tên gần đúng
    public List<MenuItem> searchByName(String keyword) {
        return menuItemRepository.findByNameContainingIgnoreCase(keyword);
    }

    // Kiểm tra trùng tên
    public boolean existsByName(String name) {
        return menuItemRepository.existsByNameIgnoreCase(name);
    }

    // Lưu lại MenuItem (dùng để cập nhật tồn kho)
    public MenuItem save(MenuItem item) {
        return menuItemRepository.save(item);
    }
}
