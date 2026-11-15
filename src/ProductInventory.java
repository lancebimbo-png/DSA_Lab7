import java.util.HashMap;
import java.util.Map;

/**
 * Part A Task 4 mini-application: a product inventory keyed by product code.
 *
 * <pre>{@code
 * ProductInventory inventory = new ProductInventory();
 * inventory.addProduct("P1001", 25);      // true  - new product
 * inventory.addProduct("P1001", 40);      // false - duplicate code rejected
 * inventory.updateStock("P1001", 30);     // true  - absolute quantity
 * inventory.adjustStock("P1001", -3);     // true  - borrow three units
 * inventory.getQuantity("P1001");         // -> 27
 * }</pre>
 *
 * <p>Rules, following the approved design decisions:
 * <ul>
 *   <li>the product code is the key and must be unique;</li>
 *   <li>a quantity can never be negative — adding, updating or adjusting to a
 *       negative stock level is rejected;</li>
 *   <li>{@code updateStock} sets an <em>absolute</em> quantity, while
 *       {@code adjustStock} applies a signed delta (borrow / return);</li>
 *   <li>an unknown product reports a quantity of {@code 0}.</li>
 * </ul>
 */
public class ProductInventory {

    /** Product code -> quantity in stock. */
    private final Map<String, Integer> stock = new HashMap<>();

    /**
     * Adds a new product.
     *
     * @return {@code true} if the product was added; {@code false} if the code is
     *         null, the quantity is negative, or the code is already in use (the
     *         stored quantity is then left untouched).
     */
    public boolean addProduct(String code, int quantity) {
        if (code == null || quantity < 0) {
            return false;
        }
        // putIfAbsent keeps the existing quantity when the code is already known.
        return stock.putIfAbsent(code, quantity) == null;
    }

    /**
     * Sets the stock level of an existing product to an absolute quantity.
     *
     * @return {@code true} if the product was updated; {@code false} if the code
     *         is null, the quantity is negative, or the product is unknown.
     */
    public boolean updateStock(String code, int quantity) {
        if (code == null || quantity < 0) {
            return false;
        }
        if (!stock.containsKey(code)) {
            return false;
        }
        stock.put(code, quantity);
        return true;
    }

    /**
     * Changes the stock level of an existing product by a signed amount.
     *
     * <p>Use a negative delta to borrow units and a positive delta to return them.
     *
     * @return {@code true} if the stock was adjusted; {@code false} if the code is
     *         null, the product is unknown, or the change would make the stock
     *         negative.
     */
    public boolean adjustStock(String code, int delta) {
        if (code == null || !stock.containsKey(code)) {
            return false;
        }
        int current = stock.get(code);
        int updated = current + delta;
        if (updated < 0) {
            return false;
        }
        stock.put(code, updated);
        return true;
    }

    /** @return the quantity in stock, or {@code 0} when the product is unknown. */
    public int getQuantity(String code) {
        if (code == null) {
            return 0;
        }
        Integer quantity = stock.get(code);
        return (quantity == null) ? 0 : quantity;
    }

    /**
     * Prints every product as {@code code -> quantity}.
     *
     * <p>Uses for-each over {@code entrySet()} (Workbook Task 2). The iteration
     * order of a {@code HashMap} is not specified, so the rows may appear in any
     * order.
     */
    public void displayStock() {
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }

    /** @return the number of products in the inventory. */
    public int size() {
        return stock.size();
    }
}
