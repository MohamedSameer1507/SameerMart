package com.sameer.sameermart.cart;

import java.util.List;

public record Cart(List<CartItem> items) {
    public long totalMinor() {
        return items.stream().mapToLong(CartItem::lineTotalMinor).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
