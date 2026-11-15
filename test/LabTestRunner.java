import java.util.HashMap;
import java.util.Map;

/**
 * Agent-authored acceptance test runner (NOT an instructor-provided file).
 *
 * The instructor starter kit for Lab 7 was never placed in this workspace, so
 * this runner is written from the acceptance-test matrix in
 * Lab_07_HashMap_KeyValue_Workbook.docx (DSA-WB-07), Part B:
 *   - Empty Map
 *   - Duplicate Key
 *   - Missing Key
 *   - Record Update
 *   - Removal
 *   - Repeated Characters
 * plus three Part A smoke checks that exercise plain java.util.HashMap and the
 * Student record directly (Workbook Tasks 1-3).
 *
 * Workflow: executed at the TEST checkpoint BEFORE BUILD. The classes under test
 * are stubs whose bodies throw UnsupportedOperationException, so the mandatory
 * cases are expected to FAIL here and to PASS after the approved Build.
 */
public class LabTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== LabTestRunner (agent-authored acceptance suite) ===");
        System.out.println("Source: acceptance-test matrix, Lab_07_HashMap_KeyValue_Workbook.docx (DSA-WB-07), Part B");
        System.out.println("DISCLOSURE: agent-authored, not instructor-supplied - the starter kit was never placed in the workspace.");
        System.out.println();

        testEmptyMap();
        testDuplicateKey();
        testMissingKey();
        testRecordUpdate();
        testRemoval();
        testRepeatedCharacters();
        testPartAHashMapReplacement();
        testPartAEntrySetIteration();
        testStudentRecordSemantics();

        System.out.println();
        System.out.println("LabTestRunner results: " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            throw new AssertionError(failed + " acceptance test(s) failed");
        }
    }

    // --- Matrix: Empty Map ---

    private static void testEmptyMap() {
        section("Empty Map");
        StudentDirectory dir = new StudentDirectory();
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            check("empty directory size is 0", dir.size() == 0);
            check("empty directory snapshot is empty", dir.snapshot().isEmpty());
            check("empty directory getStudent returns null", dir.getStudent("X") == null);
            check("empty directory removeStudent returns false", !dir.removeStudent("X"));
            check("empty directory updateStudent returns false",
                    !dir.updateStudent("X", new Student("X", "Xavier", 80.0)));
            check("empty directory containsStudent returns false", !dir.containsStudent("X"));
            check("empty directory size still 0 after failed operations", dir.size() == 0);
            check("counter on empty input returns a non-null map", counter.count("") != null);
            check("counter on empty input returns an empty map", counter.count("").isEmpty());
        } catch (UnsupportedOperationException e) {
            check("empty-map API implemented", false);
            blocked(e);
        }
    }

    // --- Matrix: Duplicate Key ---

    private static void testDuplicateKey() {
        section("Duplicate Key");
        StudentDirectory dir = new StudentDirectory();
        Student alice = new Student("A", "Alice", 89.5);
        Student bob = new Student("A", "Bob", 91.0);
        try {
            check("first add of ID A returns true", dir.addStudent("A", alice));
            check("duplicate add of ID A returns false", !dir.addStudent("A", bob));
            check("duplicate add leaves size at 1", dir.size() == 1);
            Student stored = dir.getStudent("A");
            check("duplicate add preserved the original name",
                    stored != null && "Alice".equals(stored.name()));
            check("duplicate add preserved the original grade",
                    stored != null && stored.grade() == 89.5);
            check("duplicate add did not store the second record",
                    stored == null || !"Bob".equals(stored.name()));
        } catch (UnsupportedOperationException e) {
            check("duplicate-key rejection implemented", false);
            blocked(e);
        }
    }

    // --- Matrix: Missing Key ---

    private static void testMissingKey() {
        section("Missing Key");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            dir.addStudent("C", new Student("C", "Carla", 85.5));
            check("missing key getStudent returns null", dir.getStudent("ZZZ") == null);
            check("missing key updateStudent returns false",
                    !dir.updateStudent("ZZZ", new Student("ZZZ", "Zed", 70.0)));
            check("missing key removeStudent returns false", !dir.removeStudent("ZZZ"));
            check("missing key leaves size unchanged", dir.size() == 3);
            check("missing key did not insert a record", dir.getStudent("ZZZ") == null);
        } catch (UnsupportedOperationException e) {
            check("missing-key behaviour implemented", false);
            blocked(e);
        }
    }

    // --- Matrix: Record Update ---

    private static void testRecordUpdate() {
        section("Record Update");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            dir.addStudent("C", new Student("C", "Carla", 85.5));
            check("updateStudent returns true",
                    dir.updateStudent("B", new Student("B", "Robert", 95.0)));
            check("updated record has the new name",
                    "Robert".equals(dir.getStudent("B").name()));
            check("updated record has the new grade",
                    dir.getStudent("B").grade() == 95.0);
            check("update leaves size unchanged", dir.size() == 3);
            check("update did not affect record A",
                    "Alice".equals(dir.getStudent("A").name()));
            check("update did not affect record C",
                    dir.getStudent("C").grade() == 85.5);
        } catch (UnsupportedOperationException e) {
            check("record-update behaviour implemented", false);
            blocked(e);
        }
    }

    // --- Matrix: Removal ---

    private static void testRemoval() {
        section("Removal");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            dir.addStudent("C", new Student("C", "Carla", 85.5));
            check("removeStudent returns true", dir.removeStudent("B"));
            check("removal decrements size to 2", dir.size() == 2);
            check("removed record is gone", dir.getStudent("B") == null);
            check("other records survive the removal",
                    dir.getStudent("A") != null && dir.getStudent("C") != null);
            check("removing an already removed ID returns false", !dir.removeStudent("B"));
            check("size unchanged after the failed second removal", dir.size() == 2);
        } catch (UnsupportedOperationException e) {
            check("removal behaviour implemented", false);
            blocked(e);
        }
    }

    // --- Matrix: Repeated Characters ---

    private static void testRepeatedCharacters() {
        section("Repeated Characters");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("abracadabra");
            check("frequency map has 5 distinct characters", freq.size() == 5);
            check("'a' counted 5 times", freq.get('a') == 5);
            check("'b' counted 2 times", freq.get('b') == 2);
            check("'r' counted 2 times", freq.get('r') == 2);
            check("'c' counted 1 time", freq.get('c') == 1);
            check("'d' counted 1 time", freq.get('d') == 1);
            int total = 0;
            for (int value : freq.values()) {
                total += value;
            }
            check("counts sum to the input length (11)", total == 11);
        } catch (UnsupportedOperationException e) {
            check("character-frequency counting implemented", false);
            blocked(e);
        }
    }

    // --- Part A smoke checks (Workbook Tasks 1-3, plain HashMap + Student record) ---

    private static void testPartAHashMapReplacement() {
        section("Part A smoke - raw HashMap replacement semantics (Task 1)");
        try {
            HashMap<String, Double> grades = new HashMap<String, Double>();
            grades.put("Alice", 89.5);
            grades.put("Ben", 91.0);
            grades.put("Carla", 85.5);
            grades.put("Alice", 95.0);
            check("raw HashMap replaces an existing key instead of adding one", grades.size() == 3);
            check("raw HashMap keeps the last value written", grades.get("Alice") == 95.0);
            check("raw HashMap get returns a stored value", grades.get("Ben") == 91.0);
            check("raw HashMap containsKey finds a stored key", grades.containsKey("Carla"));
            grades.remove("Carla");
            check("raw HashMap remove deletes the entry",
                    !grades.containsKey("Carla") && grades.size() == 2);
        } catch (RuntimeException e) {
            check("Part A HashMap basics behave as documented", false);
            blocked(e);
        }
    }

    private static void testPartAEntrySetIteration() {
        section("Part A smoke - entrySet iteration (Task 2) and Student storage (Task 3)");
        try {
            HashMap<String, Student> students = new HashMap<String, Student>();
            students.put("2025-001", new Student("2025-001", "Alice", 89.5));
            students.put("2025-002", new Student("2025-002", "Ben", 91.0));
            check("entrySet exposes every entry", students.entrySet().size() == 2);
            int visited = 0;
            for (Map.Entry<String, Student> entry : students.entrySet()) {
                visited++;
                check("entry key resolves to its stored record",
                        entry.getValue().equals(students.get(entry.getKey())));
                check("entry value carries the matching student ID",
                        entry.getKey().equals(entry.getValue().id()));
            }
            check("for-each over entrySet visited every entry", visited == 2);
        } catch (RuntimeException e) {
            check("Part A entrySet iteration behaves as documented", false);
            blocked(e);
        }
    }

    private static void testStudentRecordSemantics() {
        section("Part A smoke - Student record semantics (Task 3)");
        try {
            Student s = new Student("2025-001", "Alice", 89.5);
            check("record exposes its id", "2025-001".equals(s.id()));
            check("record exposes its name", "Alice".equals(s.name()));
            check("record exposes its grade", s.grade() == 89.5);
            check("records with equal components are equal",
                    s.equals(new Student("2025-001", "Alice", 89.5)));
            check("equal records share a hash code",
                    s.hashCode() == new Student("2025-001", "Alice", 89.5).hashCode());
        } catch (RuntimeException e) {
            check("Student record behaves as an immutable value type", false);
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

    private static void blocked(RuntimeException e) {
        System.out.println("       blocked by: " + e);
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
