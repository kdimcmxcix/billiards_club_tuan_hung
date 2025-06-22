package com.billiards_club_tuan_hung.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billiards_club_tuan_hung.entity.BilliardsTable;
import com.billiards_club_tuan_hung.repository.BilliardsTableRepository;

import javax.persistence.EntityNotFoundException;

@Service
public class BilliardsTableService {

    @Autowired
    private BilliardsTableRepository billiardsTableRepository;

    public List<BilliardsTable> getAllBilliardsTables() {
        return billiardsTableRepository.findAll();
    }

    public BilliardsTable getBilliardsTableById(long id) {
        return billiardsTableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bàn với id: " + id));
    }

    public BilliardsTable createBilliardsTable(BilliardsTable billiardsTable) {
        billiardsTable.setInUse(false); // bàn mới mặc định chưa được sử dụng
        return billiardsTableRepository.save(billiardsTable);
    }

    public BilliardsTable updateBilliardsTable(long id, BilliardsTable updatedTable) {
        BilliardsTable existingTable = getBilliardsTableById(id);
        existingTable.setTableName(updatedTable.getTableName());
        existingTable.setInUse(updatedTable.isInUse());
        existingTable.setPricePerHour(updatedTable.getPricePerHour()); // cập nhật giá
        return billiardsTableRepository.save(existingTable);
    }

    public void deleteBilliardsTable(long id) {
        BilliardsTable table = getBilliardsTableById(id);
        billiardsTableRepository.delete(table);
    }

    public BilliardsTable markTableAsInUse(long id) {
        BilliardsTable table = getBilliardsTableById(id);
        table.setInUse(true);
        return billiardsTableRepository.save(table);
    }

    public BilliardsTable markTableAsAvailable(long id) {
        BilliardsTable table = getBilliardsTableById(id);
        table.setInUse(false);
        return billiardsTableRepository.save(table);
    }

    public List<BilliardsTable> getAvailableTables() {
        return billiardsTableRepository.findByInUseFalse();
    }
}
