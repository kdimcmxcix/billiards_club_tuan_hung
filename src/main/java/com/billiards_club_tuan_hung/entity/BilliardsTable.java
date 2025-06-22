package com.billiards_club_tuan_hung.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "billiards_tables")
public class BilliardsTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "in_use")
    private boolean inUse = false;

    @Column(name = "price_per_hour")
    private Double pricePerHour;

    @OneToMany(mappedBy = "billiardsTable")
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    public BilliardsTable() {}

    public BilliardsTable(long id, String tableName, boolean inUse, Double pricePerHour, List<Order> orders) {
        this.id = id;
        this.tableName = tableName;
        this.inUse = inUse;
        this.pricePerHour = pricePerHour;
        this.orders = orders;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isInUse() {
        return inUse;
    }

    public void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
