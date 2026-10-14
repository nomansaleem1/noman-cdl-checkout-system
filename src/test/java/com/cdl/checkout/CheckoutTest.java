package com.cdl.checkout;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckoutTest {

    @Test
    void shouldReturnZeroForEmptyCheckout() {
        Checkout checkout = new Checkout();
        assertEquals(0, checkout.getTotal());
    }

    @Test
    void shouldCalculatePriceForOneA() {
        Checkout checkout = new Checkout();
        checkout.scan("A");
        assertEquals(50, checkout.getTotal());
    }

    @Test
    void shouldCalculatePriceForOneB() {
        Checkout checkout = new Checkout();
        checkout.scan("B");
        assertEquals(30, checkout.getTotal());
    }

    @Test
    void shouldCalculatePriceForOneC() {
        Checkout checkout = new Checkout();
        checkout.scan("C");
        assertEquals(20, checkout.getTotal());
    }

    @Test
    void shouldCalculatePriceForOneD() {
        Checkout checkout = new Checkout();
        checkout.scan("D");
        assertEquals(15, checkout.getTotal());
    }

    @Test
    void shouldCalculatePriceForMultipleDifferentItems() {
        Checkout checkout = new Checkout();
        checkout.scan("D");
        checkout.scan("B");
        checkout.scan("A");
        checkout.scan("C");
        assertEquals(115, checkout.getTotal());
    }

    @Test
    void shouldApplySpecialOfferForThreeAs() {
        Checkout checkout = new Checkout();
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        assertEquals(130, checkout.getTotal());
    }

    @Test
    void shouldApplyOfferForBsScannedApart() {
        Checkout checkout = new Checkout();
        checkout.scan("B");
        checkout.scan("A");
        checkout.scan("B");
        assertEquals(95, checkout.getTotal());
    }

    @Test
    void shouldChargeForAnExtraAAfterAnOffer() {
        Checkout checkout = new Checkout();
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        assertEquals(180, checkout.getTotal());
    }

    @Test
    void shouldApplyTheAOfferTwice() {
        Checkout checkout = new Checkout();
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        checkout.scan("A");
        assertEquals(260, checkout.getTotal());
    }

    @Test
    void shouldApplyTwoBOffersAndChargeForTheRemainingB() {
        Checkout checkout = new Checkout();
        checkout.scan("B");
        checkout.scan("B");
        checkout.scan("B");
        checkout.scan("B");
        checkout.scan("B");
        assertEquals(120, checkout.getTotal());
    }

    @Test
    void shouldApplyBothOffersWhenItemsAreMixed() {
        Checkout checkout = new Checkout();
        checkout.scan("D");
        checkout.scan("A");
        checkout.scan("B");
        checkout.scan("A");
        checkout.scan("B");
        checkout.scan("A");
        assertEquals(190, checkout.getTotal());
    }

    @Test
    void shouldUpdateRunningTotalAfterEachScan() {
        Checkout checkout = new Checkout();
        assertEquals(0, checkout.getTotal());
        checkout.scan("A");
        assertEquals(50, checkout.getTotal());
        checkout.scan("B");
        assertEquals(80, checkout.getTotal());
        checkout.scan("A");
        assertEquals(130, checkout.getTotal());
        checkout.scan("A");
        assertEquals(160, checkout.getTotal());
        checkout.scan("B");
        assertEquals(175, checkout.getTotal());
        checkout.scan("A");
        assertEquals(225, checkout.getTotal());
        checkout.scan("A");
        assertEquals(275, checkout.getTotal());
        checkout.scan("A");
        assertEquals(305, checkout.getTotal());
    }

    @Test
    void shouldUsePricingRulesProvidedForEachTransaction() {
        Checkout first = new Checkout(Map.of("A", new PricingRule(50, new SpecialOffer(3, 130))));
        Checkout second = new Checkout(Map.of("A", new PricingRule(60, new SpecialOffer(2, 100))));
        for (int i = 0; i < 3; i++) {
            first.scan("A");
            second.scan("A");
        }
        assertEquals(130, first.getTotal());
        assertEquals(160, second.getTotal());
    }

    @Test
    void shouldKeepTransactionPricingWhenOriginalRulesChange() {
        Map<String, PricingRule> rules = new HashMap<>();
        rules.put("A", new PricingRule(50));
        Checkout checkout = new Checkout(rules);
        rules.put("A", new PricingRule(100));
        checkout.scan("A");
        assertEquals(50, checkout.getTotal());
    }

    @Test
    void shouldRejectUnknownItemsWithoutChangingTotal() {
        Checkout checkout = new Checkout();
        checkout.scan("A");
        assertThrows(IllegalArgumentException.class, () -> checkout.scan("Z"));
        assertThrows(IllegalArgumentException.class, () -> checkout.scan(null));
        assertEquals(50, checkout.getTotal());
    }

    @Test
    void shouldValidatePricingRules() {
        assertThrows(IllegalArgumentException.class, () -> new Checkout(null));
        assertThrows(IllegalArgumentException.class, () -> new Checkout(Map.of("AA", new PricingRule(50))));
        assertThrows(IllegalArgumentException.class, () -> new PricingRule(-1));
        assertThrows(IllegalArgumentException.class, () -> new SpecialOffer(0, 130));
        assertThrows(IllegalArgumentException.class, () -> new SpecialOffer(3, -1));
        assertThrows(IllegalArgumentException.class, () -> new PricingRule(50).calculatePrice(-1));
    }

    @Test
    void shouldPrintRunningAndFinalTotalsForCommandLineArguments() {
        assertEquals("Scanned B. Price: £0.30. Running total: £0.30\n"
                        + "Scanned A. Price: £0.50. Running total: £0.80\n"
                        + "Scanned B. Price: £0.15 (Offer applied). Running total: £0.95\n"
                        + "Final total: £0.95\n",
                runCommandLine(new String[]{"B", "A", "B"}, ""));
    }

    @Test
    void shouldKeepScanPricesAndReportRepeatedOffers() {
        assertEquals("Scanned B. Price: £0.30. Running total: £0.30\n"
                        + "Scanned B. Price: £0.15 (Offer applied). Running total: £0.45\n"
                        + "Scanned B. Price: £0.30. Running total: £0.75\n"
                        + "Scanned B. Price: £0.15 (Offer applied). Running total: £0.90\n"
                        + "Scanned B. Price: £0.30. Running total: £1.20\n"
                        + "Final total: £1.20\n",
                runCommandLine(new String[]{"B", "B", "B", "B", "B"}, ""));
    }

    @Test
    void shouldAcceptInteractiveInputAndRecoverFromInvalidItems() {
        String output = runCommandLine(new String[0], "b A\nZ\nb\ndone\nA\n");
        assertTrue(output.contains("Unknown item: Z"));
        assertTrue(output.contains("Scanned B. Price: £0.15 (Offer applied). Running total: £0.95"));
        assertTrue(output.endsWith("Final total: £0.95\n"));
    }

    @Test
    void shouldPrintFinalTotalAtEndOfInput() {
        assertTrue(runCommandLine(new String[0], "A\n").endsWith("Final total: £0.50\n"));
        assertTrue(runCommandLine(new String[0], "").endsWith("Final total: £0.00\n"));
    }

    private String runCommandLine(String[] args, String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Main.run(args, new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
