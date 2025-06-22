package com.billiards_club_tuan_hung.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;

import com.billiards_club_tuan_hung.entity.WorkingSession;
import com.billiards_club_tuan_hung.service.WorkingSessionService;

@RestController
@CrossOrigin
@RequestMapping("/api/working-session")
public class WorkingSessionController {

    @Autowired
    private WorkingSessionService workingSessionService;

    // Lấy tất cả phiên làm việc
    @GetMapping
    public ResponseEntity<List<WorkingSession>> getAllWorkingSessions() {
        try {
            List<WorkingSession> sessions = workingSessionService.getAllWorkingSessions();
            return new ResponseEntity<>(sessions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Lấy 1 phiên làm việc theo id
    @GetMapping("/{id}")
    public ResponseEntity<WorkingSession> getWorkingSessionById(@PathVariable long id) {
        try {
            WorkingSession session = workingSessionService.getWorkingSessionById(id);
            return new ResponseEntity<>(session, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Tạo phiên làm việc mới
    @PostMapping
    public ResponseEntity<WorkingSession> createWorkingSession(@RequestBody WorkingSession session) {
        try {
            WorkingSession created = workingSessionService.createWorkingSession(session);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Cập nhật phiên làm việc
    @PutMapping("/{id}")
    public ResponseEntity<WorkingSession> updateWorkingSession(@PathVariable long id,
            @RequestBody WorkingSession session) {
        try {
            WorkingSession updated = workingSessionService.updateWorkingSession(id, session);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Xoá phiên làm việc
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkingSession(@PathVariable long id) {
        try {
            workingSessionService.deleteWorkingSession(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Lấy danh sách phiên làm việc theo staffId
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<WorkingSession>> getWorkingSessionsByStaffId(@PathVariable long staffId) {
        try {
            List<WorkingSession> sessions = workingSessionService.getWorkingSessionsByStaffId(staffId);
            return new ResponseEntity<>(sessions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Tạo phiên làm việc mới nếu chưa có (dành cho đăng nhập)
    @PostMapping("/start")
    public ResponseEntity<?> startWorkingSessionIfNotExists(
            @RequestBody(required = false) java.util.Map<String, Long> body) {
        try {
            if (body == null || !body.containsKey("staffId")) {
                return ResponseEntity.badRequest().body("Thiếu staffId");
            }

            Long staffId = body.get("staffId");
            workingSessionService.startSessionIfNotExists(staffId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi khởi tạo phiên làm việc: " + e.getMessage());
        }
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<WorkingSession>> getPagedSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return new ResponseEntity<>(workingSessionService.getPagedSessions(page, size), HttpStatus.OK);
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportWorkingSessionsToExcel() {
        try {
            List<WorkingSession> sessions = workingSessionService.getAllWorkingSessions();
            ByteArrayInputStream in = workingSessionService.exportSessionsToExcel(sessions);
            InputStreamResource file = new InputStreamResource(in);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=working_sessions.xlsx")
                    .contentType(MediaType
                            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

}
