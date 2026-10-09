package com.sameer.sameermart.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TopSellingProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    private int productId;
    private String productName;
    private int unitsSold;
    private BigDecimal totalRevenue;

    public TopSellingProduct() {
    }

    public TopSellingProduct(int productId, String productName, int unitsSold, BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.unitsSold = unitsSold;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(int unitsSold) {
        this.unitsSold = unitsSold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
