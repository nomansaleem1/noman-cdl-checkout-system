package com.cdl.checkout;

public record SpecialOffer(int quantity, int price) {
    public SpecialOffer {
        if (quantity <= 0 || price < 0) {
            throw new IllegalArgumentException("Offer quantity must be positive and price non-negative");
        }
    }
}
