package com.billiards_club_tuan_hung.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "working_sessions")
public class WorkingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "cash_in_session")
    private double cashInSession = 0.0;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    public WorkingSession() {

    }

    public WorkingSession(long id, LocalDateTime startTime, LocalDateTime endTime, double cashInSession, User staff) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cashInSession = cashInSession;
        this.staff = staff;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getCashInSession() {
        return cashInSession;
    }

    public void setCashInSession(double cashInSession) {
        this.cashInSession = cashInSession;
    }

    public User getStaff() {
        return staff;
    }

    public void setStaff(User staff) {
        this.staff = staff;
    }
    
}
