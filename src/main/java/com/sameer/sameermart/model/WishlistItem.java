package com.sameer.sameermart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class WishlistItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int buyerId;
    private int productId;
    private Timestamp createdAt;

    // Associated Product information for rich rendering
    private String productName;
    private BigDecimal productPrice;
    private String productDescription;
    private String productCategory;
    private String productImageUrl;
    private int productQuantity;

    public WishlistItem() {
    }

    public WishlistItem(int id, int buyerId, int productId, Timestamp createdAt) {
        this.id = id;
        this.buyerId = buyerId;
        this.productId = productId;
        this.createdAt = createdAt != null ? new Timestamp(createdAt.getTime()) : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(int buyerId) {
        this.buyerId = buyerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Timestamp getCreatedAt() {
        return createdAt != null ? new Timestamp(createdAt.getTime()) : null;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt != null ? new Timestamp(createdAt.getTime()) : null;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public boolean isInStock() {
        return productQuantity > 0;
    }
}
