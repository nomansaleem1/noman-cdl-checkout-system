package com.cdl.checkout;

import java.util.HashMap;
import java.util.Map;

public class Checkout {

    private final Map<String, PricingRule> pricingRules;
    private final Map<String, Integer> scannedItems = new HashMap<>();

    public Checkout() {
        this(samplePricingRules());
    }

    public Checkout(Map<String, PricingRule> pricingRules) {
        if (pricingRules == null) {
            throw new IllegalArgumentException("Pricing rules are required");
        }
        pricingRules.forEach((sku, rule) -> {
            if (sku == null || !sku.matches("[A-Z]") || rule == null) {
                throw new IllegalArgumentException("Each rule requires a single uppercase SKU and a price");
            }
        });
        this.pricingRules = Map.copyOf(pricingRules);
    }

    public static Map<String, PricingRule> samplePricingRules() {
        return Map.of(
                "A", new PricingRule(50, new SpecialOffer(3, 130)),
                "B", new PricingRule(30, new SpecialOffer(2, 45)),
                "C", new PricingRule(20),
                "D", new PricingRule(15)
        );
    }

    public int getTotal() {
        int total = 0;
        for (var entry : scannedItems.entrySet()) {
            PricingRule rule = pricingRules.get(entry.getKey());
            total += rule.calculatePrice(entry.getValue());
        }
        return total;
    }

    public void scan(String item) {
        if (item == null || !pricingRules.containsKey(item)) {
            throw new IllegalArgumentException("Unknown item: " + item);
        }
        scannedItems.merge(item, 1, Integer::sum);
    }
}
