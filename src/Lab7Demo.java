import java.util.Map;

/**
 * Runnable demonstration of Lab 7 — HashMap and Key-Value Structures.
 *
 * <p>Run it with:
 * <pre>
 * javac -d out src/*.java test/*.java
 * java -cp out Lab7Demo
 * </pre>
 *
 * <p>The output is the log for the lab report: it shows the Part A exercises
 * (HashMap basics, entrySet() iteration, Student objects, the Product Inventory
 * mini-application) and the Part B classes (the student directory and the
 * character-frequency counter).
 */
public class Lab7Demo {

    public static void main(String[] args) {
        System.out.println("=== Lab 7 - HashMap and Key-Value Structures ===");
        System.out.println("Workbook DSA-WB-07 | Part A: plain HashMap | Part B: keyed directory");
        System.out.println();

        System.out.println("########## PART A - HashMap basics, entrySet(), Student objects ##########");
        HashMapBasics.runDemo();
        System.out.println();

        System.out.println("########## PART A Task 4 - Product Inventory mini-application ##########");
        demoProductInventory();
        System.out.println();

        System.out.println("########## PART B - StudentDirectory ##########");
        demoStudentDirectory();
        System.out.println();

        System.out.println("########## PART B - character frequency ##########");
        demoCharacterFrequency();
    }

    /** Part A Task 4: add products, update stock, adjust stock, display all. */
    private static void demoProductInventory() {
        ProductInventory inventory = new ProductInventory();

        System.out.println("addProduct(\"P1001\", 25)  = " + inventory.addProduct("P1001", 25));
        System.out.println("addProduct(\"P1002\", 10)  = " + inventory.addProduct("P1002", 10));
        System.out.println("addProduct(\"P1003\", 60)  = " + inventory.addProduct("P1003", 60));
        System.out.println("addProduct(\"P1001\", 99)  = " + inventory.addProduct("P1001", 99)
                + "   (duplicate code rejected, stored quantity untouched)");

        System.out.println("updateStock(\"P1002\", 40) = " + inventory.updateStock("P1002", 40)
                + "   (absolute quantity)");
        System.out.println("adjustStock(\"P1002\", -5) = " + inventory.adjustStock("P1002", -5)
                + "   (borrow five units)");
        System.out.println("adjustStock(\"P1002\", -999) = " + inventory.adjustStock("P1002", -999)
                + "   (rejected: the stock would go negative)");

        System.out.println("getQuantity(\"P1002\")   = " + inventory.getQuantity("P1002"));
        System.out.println("getQuantity(\"NOPE\")    = " + inventory.getQuantity("NOPE")
                + "   (unknown product reports 0)");
        System.out.println("displayStock():");
        inventory.displayStock();
    }

    /** Part B: add, reject a duplicate, retrieve, update, snapshot, remove. */
    private static void demoStudentDirectory() {
        StudentDirectory directory = new StudentDirectory();

        System.out.println("addStudent(\"2025-001\", Alice)   = "
                + directory.addStudent("2025-001", new Student("2025-001", "Alice", 89.5)));
        System.out.println("addStudent(\"2025-002\", Ben)     = "
                + directory.addStudent("2025-002", new Student("2025-002", "Ben", 91.0)));
        System.out.println("addStudent(\"2025-003\", Carla)   = "
                + directory.addStudent("2025-003", new Student("2025-003", "Carla", 85.5)));
        System.out.println("addStudent(\"2025-001\", Robert)  = "
                + directory.addStudent("2025-001", new Student("2025-001", "Robert", 95.0))
                + "   (duplicate ID rejected; Alice is kept)");

        System.out.println("getStudent(\"2025-001\")          = " + directory.getStudent("2025-001"));
        System.out.println("getStudent(\"ZZZ\")              = " + directory.getStudent("ZZZ")
                + "   (unknown ID retrieves as null)");

        System.out.println("updateStudent(\"2025-002\", Ben)  = "
                + directory.updateStudent("2025-002", new Student("2025-002", "Ben Rosales", 93.0)));
        System.out.println("updateStudent(\"ZZZ\", Zed)       = "
                + directory.updateStudent("ZZZ", new Student("ZZZ", "Zed", 70.0))
                + "   (an update never inserts)");

        System.out.println("size()                            = " + directory.size());
        System.out.println("snapshot():");
        Map<String, Student> snapshot = directory.snapshot();
        for (Map.Entry<String, Student> entry : snapshot.entrySet()) {
            System.out.println("  " + entry.getKey() + " -> " + entry.getValue().name()
                    + " (" + entry.getValue().grade() + ")");
        }
        snapshot.clear();
        System.out.println("the snapshot was cleared; directory size is still "
                + directory.size() + " (a snapshot is a copy, not a view)");

        System.out.println("removeStudent(\"2025-003\")       = " + directory.removeStudent("2025-003"));
        System.out.println("removeStudent(\"2025-003\") again = " + directory.removeStudent("2025-003"));
        System.out.println("size() after removal             = " + directory.size());
    }

    /** Part B: count the characters of a sentence. */
    private static void demoCharacterFrequency() {
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();

        Map<Character, Integer> frequency = counter.count("the quick brown fox");
        System.out.println("count(\"the quick brown fox\") -> " + frequency.size() + " distinct characters");
        for (Map.Entry<Character, Integer> entry : frequency.entrySet()) {
            System.out.println("  '" + entry.getKey() + "' = " + entry.getValue());
        }

        int total = 0;
        for (int value : frequency.values()) {
            total += value;
        }
        System.out.println("all counts sum to " + total + ", which is the length of the text");
        System.out.println("uppercase and lowercase are separate keys, and spaces and");
        System.out.println("punctuation are counted just like letters (decision D6).");
    }
}
