package com.cdl.checkout;

public class PricingRule {
    private final int unitPrice;
    private final SpecialOffer specialOffer;

    public PricingRule(int unitPrice) {
        this(unitPrice, null);
    }

    public PricingRule(int unitPrice, SpecialOffer specialOffer) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.unitPrice = unitPrice;
        this.specialOffer = specialOffer;
    }

    public int calculatePrice(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (specialOffer == null) {
            return quantity * unitPrice;
        }

        int offerGroups = quantity / specialOffer.quantity();
        int remainingItems = quantity % specialOffer.quantity();

        return offerGroups * specialOffer.price()
                + remainingItems * unitPrice;
    }
}
