package com.billiards_club_tuan_hung.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.billiards_club_tuan_hung.entity.User;
import com.billiards_club_tuan_hung.entity.WorkingSession;
import com.billiards_club_tuan_hung.repository.UserRepository;
import com.billiards_club_tuan_hung.repository.WorkingSessionRepository;

@Service
public class WorkingSessionService {

    @Autowired
    private WorkingSessionRepository workingSessionRepository;

    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả phiên làm việc
    public List<WorkingSession> getAllWorkingSessions() {
        return workingSessionRepository.findAll();
    }

    // Lấy phiên theo id
    public WorkingSession getWorkingSessionById(long id) {
        return workingSessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên làm việc với id: " + id));
    }

    // Tạo phiên mới
    public WorkingSession createWorkingSession(WorkingSession session) {
        return workingSessionRepository.save(session);
    }

    // Cập nhật phiên
    public WorkingSession updateWorkingSession(long id, WorkingSession session) {
        WorkingSession existing = getWorkingSessionById(id);
        existing.setStartTime(session.getStartTime());
        existing.setEndTime(session.getEndTime());
        existing.setCashInSession(session.getCashInSession());
        existing.setStaff(session.getStaff());
        return workingSessionRepository.save(existing);
    }

    // Xoá phiên
    public void deleteWorkingSession(long id) {
        WorkingSession existing = getWorkingSessionById(id);
        workingSessionRepository.delete(existing);
    }

    // Lấy danh sách theo staff
    public List<WorkingSession> getWorkingSessionsByStaffId(long staffId) {
        return workingSessionRepository.findByStaffId(staffId);
    }

    // Lấy phiên hiện tại (chưa kết thúc) của nhân viên
    public Optional<WorkingSession> findCurrentSessionByStaff(Long staffId) {
        return workingSessionRepository
                .findByStaffIdAndEndTimeIsNull(staffId); // Giả sử end_time null tức là ca đang mở
    }

    // Cộng tiền mặt vào phiên làm việc hiện tại
    public void addCashToCurrentSession(User staff, double amount) {
        WorkingSession session = findCurrentSessionByStaff(staff.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên làm việc hiện tại cho nhân viên"));

        session.setCashInSession(session.getCashInSession() + amount);
        workingSessionRepository.save(session);
    }

    public void addCashToCurrentSessionByStaffId(Long staffId, double amount) {
        findCurrentSessionByStaff(staffId).ifPresentOrElse(
                session -> {
                    session.setCashInSession(session.getCashInSession() + amount);
                    workingSessionRepository.save(session);
                },
                () -> {
                    throw new RuntimeException("Không tìm thấy phiên làm việc hiện tại cho nhân viên id = " + staffId);
                });
    }

    public void startSessionIfNotExists(Long staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        boolean exists = workingSessionRepository.existsByStaffIdAndEndTimeIsNull(staff.getId());

        if (!exists) {
            WorkingSession session = new WorkingSession();
            session.setStartTime(LocalDateTime.now());
            session.setStaff(staff);
            workingSessionRepository.save(session);
        }
    }

    public Page<WorkingSession> getPagedSessions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        return workingSessionRepository.findAll(pageable);
    }

    public ByteArrayInputStream exportSessionsToExcel(List<WorkingSession> sessions) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Working Sessions");

            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Nhân viên", "Bắt đầu", "Kết thúc", "Tổng tiền" };
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (WorkingSession session : sessions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(session.getId());
                row.createCell(1).setCellValue(session.getStaff().getUsername());
                row.createCell(2).setCellValue(session.getStartTime().toString());
                row.createCell(3).setCellValue(session.getEndTime() != null ? session.getEndTime().toString() : "");
                row.createCell(4).setCellValue(session.getCashInSession());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

}
