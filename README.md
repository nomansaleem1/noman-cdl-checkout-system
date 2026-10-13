# Supermarket Checkout

A command-line supermarket checkout built with Java 21, Maven and JUnit 5.

## Prices

Each item has a single-letter code (SKU). These are the sample prices from the task:

| Item | Unit price (pence) | Special offer (pence) |
| --- | --- | --- |
| A | 50 | 3 for 130 |
| B | 30 | 2 for 45 |
| C | 20 | None |
| D | 15 | None |

For example, scanning B, A and B gives a total of 95p. The two Bs cost 45p
because of the offer, and the A costs 50p.

## Design choices

The code stores money as integer pence, so 50p is stored as `50` and £1.30 as
`130`. This keeps calculations exact and avoids floating-point rounding errors.
Totals are formatted as pounds only when displayed.

Pricing rules are separate from the checkout's item counts. Each rule describes
an item's unit price and optional offer. A new checkout can receive a different
set of rules without changing the calculation logic. It copies the rules so a
change to the original map does not affect an existing transaction.

The checkout counts how many times each SKU has been scanned. This makes offers
work regardless of scan order: B, A, B still counts as two Bs and one A. The total
is calculated from those counts, with each complete offer group charged at the
special price and remaining items charged at the unit price. Four As therefore
cost 130p + 50p = 180p.

## Build and test

You need JDK 21 or later and Maven installed. Run these commands from the project folder:

```sh
mvn test
mvn package
```

The tests cover individual prices, different items, repeated offers, leftover
items, scanning in different orders, custom pricing rules and invalid input.
They also check the running and final totals printed by the command-line program.

## Run the checkout

After building, start the program with:

```sh
java -cp target/classes com.cdl.checkout.Main
```

Enter an item such as `A`, or several items separated by spaces, such as `B A B`.
Lowercase letters work too. Type `done` to finish and see the final total.
The program also finishes if it reaches the end of input. If an item is not
recognised, it prints a message and lets you continue.

You can also pass the items directly in the command:

```sh
java -cp target/classes com.cdl.checkout.Main B A B
```

This prints:

```text
Scanned B. Running total: £0.30
Scanned A. Running total: £0.80
Scanned B. Running total: £0.95
Final total: £0.95
```
### AI assistance and personal review

I used Codex to help develop the initial implementation. I then reviewed the code and made the following changes:

- Added named variables in PricingRule.calculatePrice() to make the offer groups and remaining items easier to follow.
- Replaced the stream in Checkout.getTotal() with a loop because I found it clearer for this small calculation.
- Changed Checkout.scan() to return void. The returned map was unused, so I removed it to keep the interface focused on scanning items and reading the total.
- Replaced the parameterized test using @CsvSource with individual tests with descriptive names, which I found easier to read and understand.