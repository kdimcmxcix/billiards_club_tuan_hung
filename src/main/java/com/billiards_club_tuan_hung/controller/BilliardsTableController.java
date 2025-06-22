package com.billiards_club_tuan_hung.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.billiards_club_tuan_hung.entity.BilliardsTable;
import com.billiards_club_tuan_hung.service.BilliardsTableService;

@RestController
@CrossOrigin
@RequestMapping("/api/billiards-table")
public class BilliardsTableController {

    @Autowired
    private BilliardsTableService billiardsTableService;

    // Lấy tất cả bàn
    @GetMapping
    public ResponseEntity<List<BilliardsTable>> getAllBilliardsTables() {
        try {
            List<BilliardsTable> tables = billiardsTableService.getAllBilliardsTables();
            return new ResponseEntity<>(tables, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy 1 bàn theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BilliardsTable> getBilliardsTableById(@PathVariable long id) {
        try {
            BilliardsTable table = billiardsTableService.getBilliardsTableById(id);
            return new ResponseEntity<>(table, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Tạo bàn mới
    @PostMapping
    public ResponseEntity<BilliardsTable> createBilliardsTable(@RequestBody BilliardsTable table) {
        try {
            BilliardsTable createdTable = billiardsTableService.createBilliardsTable(table);
            return new ResponseEntity<>(createdTable, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Cập nhật bàn
    @PutMapping("/{id}")
    public ResponseEntity<BilliardsTable> updateBilliardsTable(
            @PathVariable long id,
            @RequestBody BilliardsTable table) {
        try {
            BilliardsTable updated = billiardsTableService.updateBilliardsTable(id, table);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Xoá bàn
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBilliardsTable(@PathVariable long id) {
        try {
            billiardsTableService.deleteBilliardsTable(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Mở bàn (đánh dấu là đang sử dụng)
    @PutMapping("/{id}/in-use")
    public ResponseEntity<BilliardsTable> markTableAsInUse(@PathVariable long id) {
        try {
            BilliardsTable table = billiardsTableService.markTableAsInUse(id);
            return new ResponseEntity<>(table, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Đóng bàn (đánh dấu là rảnh)
    @PutMapping("/{id}/available")
    public ResponseEntity<BilliardsTable> markTableAsAvailable(@PathVariable long id) {
        try {
            BilliardsTable table = billiardsTableService.markTableAsAvailable(id);
            return new ResponseEntity<>(table, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lấy danh sách bàn còn trống
    @GetMapping("/available")
    public ResponseEntity<List<BilliardsTable>> getAvailableTables() {
        try {
            List<BilliardsTable> availableTables = billiardsTableService.getAvailableTables();
            return new ResponseEntity<>(availableTables, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
