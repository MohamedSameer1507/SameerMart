package com.sameer.sameermart.product;

/** Product amounts are integer minor units (paise). */
public record Product(long id, long sellerId, String name, String description,
                      long priceMinor, int stockQuantity, String category, String imageUrl) {
    public long getId() { return id; }
    public long getSellerId() { return sellerId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public long getPriceMinor() { return priceMinor; }
    public int getStockQuantity() { return stockQuantity; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
}
