import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Student-authored suite for the Part A Task 4 mini-application
 * (Product Code -> Quantity).
 *
 * Approved rules: addProduct rejects a duplicate code; updateStock sets an
 * absolute quantity and requires an existing code; adjustStock applies a delta
 * (borrow/return) and may not drive the stock negative; a null code and a
 * negative quantity are rejected; getQuantity reports 0 for an absent code.
 *
 * At the Test checkpoint every behaviour is expected to FAIL, because
 * ProductInventory is a stub whose bodies throw UnsupportedOperationException.
 */
public class ProductInventoryTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== ProductInventoryTest (Part A mini-application) ===");
        System.out.println();

        testProductIsAdded();
        testDuplicateProductCodeIsRejected();
        testStockUpdateSetsAnAbsoluteQuantity();
        testUpdateOfAnAbsentCodeDoesNotInsert();
        testAdjustStockAppliesADelta();
        testBorrowBelowZeroIsRejected();
        testMissingProductReportsZero();
        testNegativeQuantityIsRejected();
        testDisplayStockCoversEveryEntry();
        testNullProductCodeIsRejected();
        testAdjustOfAbsentCodeDoesNotInsert();
        testBorrowToExactlyZeroIsAccepted();

        System.out.println();
        System.out.println("ProductInventoryTest results: " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            throw new AssertionError(failed + " Part A test(s) failed");
        }
    }

    private static void testProductIsAdded() {
        section("P1 - a product is added");
        ProductInventory inv = new ProductInventory();
        try {
            check("adding a product returns true", inv.addProduct("P1", 5));
            check("the quantity is readable", inv.getQuantity("P1") == 5);
            check("the inventory holds one product", inv.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("product insertion implemented", false);
            blocked(e);
        }
    }

    private static void testDuplicateProductCodeIsRejected() {
        section("P2 - a duplicate product code is rejected");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 5);
            check("adding the same code again returns false", !inv.addProduct("P1", 9));
            check("the stored quantity is unchanged", inv.getQuantity("P1") == 5);
            check("the inventory still holds one product", inv.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("duplicate-code rejection implemented", false);
            blocked(e);
        }
    }

    private static void testStockUpdateSetsAnAbsoluteQuantity() {
        section("P3 - updateStock sets an absolute quantity");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 5);
            check("updating the stock returns true", inv.updateStock("P1", 12));
            check("the quantity is now 12", inv.getQuantity("P1") == 12);
            check("the inventory still holds one product", inv.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("stock update implemented", false);
            blocked(e);
        }
    }

    private static void testUpdateOfAnAbsentCodeDoesNotInsert() {
        section("P4 - updateStock on an absent code does not insert");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 5);
            check("updating an absent code returns false", !inv.updateStock("P9", 3));
            check("the absent code was not inserted", inv.getQuantity("P9") == 0);
            check("the inventory still holds one product", inv.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("update-without-insert implemented", false);
            blocked(e);
        }
    }

    private static void testAdjustStockAppliesADelta() {
        section("P5 - adjustStock applies a delta (borrow / return)");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 12);
            check("borrowing two units returns true", inv.adjustStock("P1", -2));
            check("the quantity dropped to 10", inv.getQuantity("P1") == 10);
            check("returning five units returns true", inv.adjustStock("P1", 5));
            check("the quantity rose to 15", inv.getQuantity("P1") == 15);
        } catch (UnsupportedOperationException e) {
            check("delta adjustment implemented", false);
            blocked(e);
        }
    }

    private static void testBorrowBelowZeroIsRejected() {
        section("P6 - a borrow that would go negative is rejected");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 15);
            check("borrowing too many units returns false", !inv.adjustStock("P1", -999));
            check("the quantity is unchanged", inv.getQuantity("P1") == 15);
        } catch (UnsupportedOperationException e) {
            check("negative-stock guard implemented", false);
            blocked(e);
        }
    }

    private static void testMissingProductReportsZero() {
        section("P7 - an absent product reports a zero quantity");
        ProductInventory inv = new ProductInventory();
        try {
            check("an absent code reports 0", inv.getQuantity("NOPE") == 0);
        } catch (UnsupportedOperationException e) {
            check("absent-product lookup implemented", false);
            blocked(e);
        }
    }

    private static void testNegativeQuantityIsRejected() {
        section("P8 - a negative quantity is rejected");
        ProductInventory inv = new ProductInventory();
        try {
            // P1 is added FIRST so that updateStock("P1", -1) exercises the negative-quantity
            // guard on an EXISTING product, rather than the absent-code path (which would
            // also return false, but for the wrong reason and would prove nothing about
            // the negative-quantity rule).
            check("adding P2 with a negative quantity returns false", !inv.addProduct("P2", -1));
            check("adding P1 with quantity 5 returns true", inv.addProduct("P1", 5));
            check("updating the existing P1 to a negative quantity returns false",
                    !inv.updateStock("P1", -1));
            check("P1's quantity remains 5", inv.getQuantity("P1") == 5);
            check("P2 was not stored", inv.getQuantity("P2") == 0);
            check("inventory size is exactly 1", inv.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("negative-quantity rejection implemented", false);
            blocked(e);
        }
    }

    private static void testDisplayStockCoversEveryEntry() {
        section("P9 - displayStock prints one line per entry");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 5);
            inv.addProduct("P2", 9);
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            try {
                System.setOut(new PrintStream(captured, true));
                inv.displayStock();
            } finally {
                System.setOut(original);
            }
            String output = captured.toString();
            java.util.List<String> lines = new java.util.ArrayList<String>();
            for (String line : output.split("\\R")) {
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
            check("one output line per entry", lines.size() == inv.size());
            check("P1 is printed with its own quantity", lines.contains("P1 -> 5"));
            check("P2 is printed with its own quantity", lines.contains("P2 -> 9"));
        } catch (UnsupportedOperationException e) {
            check("displayStock implemented", false);
            blocked(e);
        }
    }

    private static void testNullProductCodeIsRejected() {
        section("P10 - a null product code is rejected");
        ProductInventory inv = new ProductInventory();
        try {
            check("adding a null code returns false", !inv.addProduct(null, 5));
            check("updating a null code returns false", !inv.updateStock(null, 5));
            check("adjusting a null code returns false", !inv.adjustStock(null, 1));
            check("a null code reports a zero quantity", inv.getQuantity(null) == 0);
            check("the inventory holds nothing", inv.size() == 0);
        } catch (UnsupportedOperationException e) {
            check("null-code rejection implemented", false);
            blocked(e);
        }
    }

    private static void testAdjustOfAbsentCodeDoesNotInsert() {
        section("P11 - adjustStock on an absent code does not insert");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 5);
            check("adjusting an absent code returns false", !inv.adjustStock("P9", 5));
            check("the absent code was not inserted", inv.getQuantity("P9") == 0);
            check("the inventory still holds one product", inv.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("adjust-without-insert implemented", false);
            blocked(e);
        }
    }

    private static void testBorrowToExactlyZeroIsAccepted() {
        section("P12 - borrowing exactly the stock on hand is accepted");
        ProductInventory inv = new ProductInventory();
        try {
            inv.addProduct("P1", 5);
            check("borrowing all five units returns true", inv.adjustStock("P1", -5));
            check("the quantity is exactly 0", inv.getQuantity("P1") == 0);
            check("the product is still listed", inv.size() == 1);
            check("borrowing one more unit is rejected", !inv.adjustStock("P1", -1));
        } catch (UnsupportedOperationException e) {
            check("zero-boundary guard implemented", false);
            blocked(e);
        }
    }

    // --- helpers ---

    private static void section(String name) {
        System.out.println("-- " + name + " --");
    }

    private static void blocked(UnsupportedOperationException e) {
        System.out.println("       blocked by: " + e.getMessage());
    }

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description);
        }
    }
}
