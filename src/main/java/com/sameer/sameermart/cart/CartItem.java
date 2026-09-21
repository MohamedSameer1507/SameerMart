package com.sameer.sameermart.cart;

public record CartItem(long productId, String name, String category, String imageUrl,
                       long unitPriceMinor, int quantity, int availableStock) {
    public long lineTotalMinor() {
        return unitPriceMinor * quantity;
    }
    public long getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public long getUnitPriceMinor() { return unitPriceMinor; }
    public int getQuantity() { return quantity; }
    public int getAvailableStock() { return availableStock; }
    public long getLineTotalMinor() { return lineTotalMinor(); }
}
