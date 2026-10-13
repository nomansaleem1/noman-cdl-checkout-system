package com.cdl.checkout;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        run(args, System.in, System.out);
    }

    static void run(String[] args, InputStream input, PrintStream output) {
        Checkout checkout = new Checkout(Checkout.samplePricingRules());
        if (args.length > 0) {
            for (String item : args) {
                scan(checkout, item, output);
            }
        } else {
            output.println("Scan A, B, C or D (space-separated items allowed). Type done to finish.");
            Scanner scanner = new Scanner(input);
            boolean finished = false;
            while (!finished && scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                for (String item : line.split("\\s+")) {
                    if (item.equalsIgnoreCase("done")) {
                        finished = true;
                        break;
                    }
                    scan(checkout, item, output);
                }
            }
        }
        output.println("Final total: " + formatPrice(checkout.getTotal()));
    }

    private static void scan(Checkout checkout, String input, PrintStream output) {
        String item = input.toUpperCase(Locale.ROOT);
        try {
            checkout.scan(item);
            output.println("Scanned " + item + ". Running total: " + formatPrice(checkout.getTotal()));
        } catch (IllegalArgumentException exception) {
            output.println(exception.getMessage());
        }
    }

    private static String formatPrice(int pence) {
        return String.format(Locale.UK, "£%d.%02d", pence / 100, pence % 100);
    }
}
