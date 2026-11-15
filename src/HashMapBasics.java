import java.util.HashMap;
import java.util.Map;

/**
 * Workbook Part A, Tasks 1-3: plain {@link HashMap} fundamentals.
 *
 * <ul>
 *   <li><b>Task 1</b> — a map of student grades: put, get, containsKey, remove.</li>
 *   <li><b>Task 2</b> — iterating with for-each over {@code entrySet()}.</li>
 *   <li><b>Task 3</b> — a map of {@link Student} objects: add, update, retrieve,
 *       display all records.</li>
 * </ul>
 *
 * <p><b>Important contrast (workbook conflict C1).</b> Everything here uses a raw
 * {@code HashMap}, so putting a key that already exists <em>replaces</em> its
 * value — the workbook's Part A behaviour. {@link StudentDirectory} applies the
 * stricter Part B rule instead, where a duplicate student ID is rejected. Both
 * behaviours are shown so the difference is visible rather than hidden.
 */
public class HashMapBasics {

    /** Runs Tasks 1, 2 and 3 in order and prints the required output. */
    public static void runDemo() {
        demonstrateGradesMap();
        demonstrateEntrySetIteration();
        demonstrateStudentObjects();
    }

    /** Task 1 — put, get, containsKey and remove on a map of grades. */
    private static void demonstrateGradesMap() {
        System.out.println("-- Task 1: HashMap basics (student -> grade) --");

        Map<String, Double> grades = new HashMap<>();
        grades.put("Alice", 89.5);
        grades.put("Ben", 91.0);
        grades.put("Carla", 85.5);

        System.out.println("grades.get(\"Ben\") = " + grades.get("Ben"));
        System.out.println("grades.containsKey(\"Carla\") = " + grades.containsKey("Carla"));
        System.out.println("grades.containsKey(\"Zed\") = " + grades.containsKey("Zed"));
        System.out.println("grades.remove(\"Carla\") = " + grades.remove("Carla"));
        System.out.println("grades.containsKey(\"Carla\") after remove = " + grades.containsKey("Carla"));
        System.out.println("grades.size() = " + grades.size());
    }

    /** Task 2 — for-each over entrySet(). */
    private static void demonstrateEntrySetIteration() {
        System.out.println("-- Task 2: iterating with entrySet() --");

        Map<String, Double> grades = new HashMap<>();
        grades.put("Alice", 89.5);
        grades.put("Ben", 91.0);
        grades.put("Carla", 85.5);

        for (Map.Entry<String, Double> entry : grades.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("(a HashMap does not promise any iteration order)");
    }

    /** Task 3 — a map of Student objects: add, update, retrieve, display all. */
    private static void demonstrateStudentObjects() {
        System.out.println("-- Task 3: HashMap of Student objects --");

        Map<String, Student> students = new HashMap<>();
        students.put("2025-001", new Student("2025-001", "Alice", 89.5));
        students.put("2025-002", new Student("2025-002", "Ben", 91.0));
        students.put("2025-003", new Student("2025-003", "Carla", 85.5));

        // Update by replacement: the raw HashMap overwrites the existing record.
        students.put("2025-002", new Student("2025-002", "Ben Rosales", 93.0));

        System.out.println("students.get(\"2025-002\") = " + students.get("2025-002"));
        System.out.println("students.size() after the update = " + students.size());
        for (Map.Entry<String, Student> entry : students.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().name()
                    + " (" + entry.getValue().grade() + ")");
        }
        System.out.println("NOTE: a raw HashMap.put REPLACES the value of an existing key.");
        System.out.println("      StudentDirectory.addStudent REJECTS a duplicate ID instead.");
    }
}
