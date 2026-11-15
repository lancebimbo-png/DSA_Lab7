import java.util.Map;

/**
 * Student-authored edge-case suite for StudentDirectory (Part B).
 *
 * Each test names ONE behaviour and the single reason it would fail. These
 * assertions were written BEFORE any implementation existed, so they are the
 * student-authored evidence the workbook requires at the Test checkpoint.
 *
 * At the Test checkpoint every behaviour below is expected to FAIL, because
 * StudentDirectory is a stub whose bodies throw UnsupportedOperationException.
 */
public class StudentDirectoryTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== StudentDirectoryTest (student-authored edge cases) ===");
        System.out.println();

        testNormalInsertionAndRetrieval();
        testMultipleRecordsCoexist();
        testSnapshotIsolatedFromMutatingTheCopy();
        testSnapshotIsolatedFromLaterDirectoryChanges();
        testSnapshotOfEmptyDirectoryIsUsable();
        testUpdateTouchesOnlyTheTargetedId();
        testUpdateOfMissingIdDoesNotInsert();
        testRemoveOfMissingIdIsANoOp();
        testNullIdIsRejected();
        testNullStudentIsRejected();
        testMismatchedStudentIdIsRejected();
        testNullNameIsAccepted();
        testNullInputsAreSafeForReads();
        testSnapshotContentMatchesTheDirectory();
        testContainsStudentAgreesWithGetStudent();
        testUpdateWithNullStudentIsRejected();
        testUpdateWithMismatchedIdIsRejected();
        testAddStudentWithNullRecordIdIsRejected();
        testRemovingTheLastStudentEmptiesTheDirectory();

        System.out.println();
        System.out.println("StudentDirectoryTest results: " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            throw new AssertionError(failed + " edge-case test(s) failed");
        }
    }

    private static void testNormalInsertionAndRetrieval() {
        section("A1 - normal insertion and retrieval");
        StudentDirectory dir = new StudentDirectory();
        try {
            Student s = new Student("2025-001", "Alice", 89.5);
            check("adding one student returns true", dir.addStudent("2025-001", s));
            check("size becomes 1", dir.size() == 1);
            Student got = dir.getStudent("2025-001");
            check("the student can be retrieved", got != null);
            check("the retrieved record keeps its id", got != null && "2025-001".equals(got.id()));
            check("the retrieved record keeps its name", got != null && "Alice".equals(got.name()));
            check("the retrieved record keeps its grade", got != null && got.grade() == 89.5);
        } catch (UnsupportedOperationException e) {
            check("normal insertion and retrieval implemented", false);
            blocked(e);
        }
    }

    private static void testMultipleRecordsCoexist() {
        section("A2 - multiple records coexist");
        StudentDirectory dir = new StudentDirectory();
        try {
            check("adding Alice returns true", dir.addStudent("A", new Student("A", "Alice", 89.5)));
            check("adding Ben returns true", dir.addStudent("B", new Student("B", "Ben", 91.0)));
            check("adding Carla returns true", dir.addStudent("C", new Student("C", "Carla", 85.5)));
            check("adding Dan returns true", dir.addStudent("D", new Student("D", "Dan", 78.0)));
            check("adding Eva returns true", dir.addStudent("E", new Student("E", "Eva", 95.0)));
            check("size is 5 after five distinct students", dir.size() == 5);
            check("Alice is retrievable", "Alice".equals(dir.getStudent("A").name()));
            check("Ben is retrievable", "Ben".equals(dir.getStudent("B").name()));
            check("Carla is retrievable", "Carla".equals(dir.getStudent("C").name()));
            check("Dan is retrievable", "Dan".equals(dir.getStudent("D").name()));
            check("Eva is retrievable", "Eva".equals(dir.getStudent("E").name()));
            check("each record kept its own grade",
                    dir.getStudent("A").grade() == 89.5 && dir.getStudent("E").grade() == 95.0);
        } catch (UnsupportedOperationException e) {
            check("multiple-record storage implemented", false);
            blocked(e);
        }
    }

    private static void testSnapshotIsolatedFromMutatingTheCopy() {
        section("A3 - snapshot is isolated when the copy is mutated");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            Map<String, Student> snap = dir.snapshot();
            snap.put("X", new Student("X", "Xavier", 80.0));
            snap.remove("A");
            check("adding to the snapshot does not change the directory", dir.size() == 2);
            check("the snapshot does not leak a new key into the directory",
                    dir.getStudent("X") == null);
            check("removing from the snapshot does not delete from the directory",
                    dir.getStudent("A") != null);
        } catch (UnsupportedOperationException e) {
            check("snapshot isolation implemented", false);
            blocked(e);
        }
    }

    private static void testSnapshotIsolatedFromLaterDirectoryChanges() {
        section("A4 - snapshot is isolated from later directory changes");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            Map<String, Student> snap = dir.snapshot();
            dir.removeStudent("A");
            check("the earlier snapshot still holds the removed record",
                    snap.get("A") != null);
            check("the earlier snapshot still has its original size", snap.size() == 2);
        } catch (UnsupportedOperationException e) {
            check("snapshot independence implemented", false);
            blocked(e);
        }
    }

    private static void testSnapshotOfEmptyDirectoryIsUsable() {
        section("A5 - snapshot of an empty directory is empty and mutable");
        StudentDirectory dir = new StudentDirectory();
        try {
            Map<String, Student> snap = dir.snapshot();
            check("the snapshot of an empty directory is not null", snap != null);
            check("the snapshot of an empty directory is empty", snap != null && snap.isEmpty());
            if (snap != null) {
                snap.put("A", new Student("A", "Alice", 89.5));
                check("the returned snapshot is mutable", snap.size() == 1);
            }
            check("mutating the snapshot did not change the directory", dir.size() == 0);
        } catch (UnsupportedOperationException e) {
            check("empty-directory snapshot implemented", false);
            blocked(e);
        }
    }

    private static void testUpdateTouchesOnlyTheTargetedId() {
        section("A6 - update touches only the targeted ID");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            check("update returns true", dir.updateStudent("A", new Student("A", "Alicia", 92.0)));
            check("the updated record shows the new name",
                    "Alicia".equals(dir.getStudent("A").name()));
            check("the updated record shows the new grade",
                    dir.getStudent("A").grade() == 92.0);
            check("the untouched record keeps its name",
                    "Ben".equals(dir.getStudent("B").name()));
            check("the untouched record keeps its grade",
                    dir.getStudent("B").grade() == 91.0);
        } catch (UnsupportedOperationException e) {
            check("targeted update implemented", false);
            blocked(e);
        }
    }

    private static void testUpdateOfMissingIdDoesNotInsert() {
        section("A7 - update of a missing ID does not insert");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            check("updating a missing ID returns false",
                    !dir.updateStudent("ZZZ", new Student("ZZZ", "Zed", 70.0)));
            check("the missing ID was not inserted", dir.getStudent("ZZZ") == null);
            check("size is unchanged", dir.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("update-without-insert implemented", false);
            blocked(e);
        }
    }

    private static void testRemoveOfMissingIdIsANoOp() {
        section("A8 - remove of a missing ID is a no-op");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            check("removing a missing ID returns false", !dir.removeStudent("ZZZ"));
            check("size is unchanged", dir.size() == 1);
            check("the existing record is untouched",
                    "Alice".equals(dir.getStudent("A").name()));
        } catch (UnsupportedOperationException e) {
            check("remove-no-op implemented", false);
            blocked(e);
        }
    }

    private static void testNullIdIsRejected() {
        section("A9 - a null student ID is rejected");
        StudentDirectory dir = new StudentDirectory();
        try {
            check("addStudent with a null ID returns false",
                    !dir.addStudent(null, new Student("A", "Alice", 89.5)));
            check("nothing was stored", dir.size() == 0);
        } catch (UnsupportedOperationException e) {
            check("null-ID rejection implemented", false);
            blocked(e);
        }
    }

    private static void testNullStudentIsRejected() {
        section("A10 - a null Student value is rejected");
        StudentDirectory dir = new StudentDirectory();
        try {
            check("addStudent with a null Student returns false",
                    !dir.addStudent("A", null));
            check("nothing was stored", dir.size() == 0);
        } catch (UnsupportedOperationException e) {
            check("null-Student rejection implemented", false);
            blocked(e);
        }
    }

    private static void testMismatchedStudentIdIsRejected() {
        section("A11 - a Student whose ID differs from the key is rejected");
        StudentDirectory dir = new StudentDirectory();
        try {
            check("addStudent with a mismatched ID returns false",
                    !dir.addStudent("A", new Student("B", "Bob", 91.0)));
            check("nothing was stored", dir.size() == 0);
            check("the mismatched key is absent", dir.getStudent("A") == null);
        } catch (UnsupportedOperationException e) {
            check("ID-consistency rejection implemented", false);
            blocked(e);
        }
    }

    private static void testNullNameIsAccepted() {
        section("A12 - a Student with a null name is accepted (documented rule)");
        StudentDirectory dir = new StudentDirectory();
        try {
            check("addStudent with a null name returns true",
                    dir.addStudent("A", new Student("A", null, 90.0)));
            check("the record was stored", dir.size() == 1);
            check("the stored name is null", dir.getStudent("A").name() == null);
            check("the stored grade is intact", dir.getStudent("A").grade() == 90.0);
        } catch (UnsupportedOperationException e) {
            check("null-name acceptance implemented", false);
            blocked(e);
        }
    }

    private static void testNullInputsAreSafeForReads() {
        section("A13 - null inputs are safe for read and delete operations");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            check("getStudent(null) returns null", dir.getStudent(null) == null);
            check("updateStudent(null, ...) returns false",
                    !dir.updateStudent(null, new Student("A", "Alice", 89.5)));
            check("removeStudent(null) returns false", !dir.removeStudent(null));
            check("containsStudent(null) returns false", !dir.containsStudent(null));
            check("size is unchanged", dir.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("null-safe reads implemented", false);
            blocked(e);
        }
    }

    private static void testSnapshotContentMatchesTheDirectory() {
        section("A14 - snapshot content matches the directory");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            dir.addStudent("C", new Student("C", "Carla", 85.5));
            Map<String, Student> snap = dir.snapshot();
            check("the snapshot has the same number of entries as the directory",
                    snap.size() == 3);
            check("the snapshot contains key A", snap.containsKey("A"));
            check("the snapshot contains key B", snap.containsKey("B"));
            check("the snapshot contains key C", snap.containsKey("C"));
            check("the snapshot does not contain an unknown key", !snap.containsKey("ZZZ"));
            check("the snapshot value for A equals the directory value",
                    snap.get("A").equals(dir.getStudent("A")));
        } catch (UnsupportedOperationException e) {
            check("snapshot content implemented", false);
            blocked(e);
        }
    }

    private static void testContainsStudentAgreesWithGetStudent() {
        section("A15 - containsStudent agrees with getStudent");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            dir.addStudent("B", new Student("B", "Ben", 91.0));
            dir.addStudent("C", new Student("C", "Carla", 85.5));
            check("containsStudent is true for A", dir.containsStudent("A"));
            check("containsStudent is true for B", dir.containsStudent("B"));
            check("containsStudent is true for C", dir.containsStudent("C"));
            check("containsStudent agrees for A",
                    dir.containsStudent("A") == (dir.getStudent("A") != null));
            check("containsStudent agrees for B",
                    dir.containsStudent("B") == (dir.getStudent("B") != null));
            check("containsStudent agrees for C",
                    dir.containsStudent("C") == (dir.getStudent("C") != null));
            check("containsStudent is false for a missing ID", !dir.containsStudent("ZZZ"));
            check("containsStudent is false for a null ID", !dir.containsStudent(null));
        } catch (UnsupportedOperationException e) {
            check("containsStudent agreement implemented", false);
            blocked(e);
        }
    }

    private static void testUpdateWithNullStudentIsRejected() {
        section("A16 - updateStudent with a null Student is rejected");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            check("updating with a null Student returns false", !dir.updateStudent("A", null));
            check("the stored record is unchanged", "Alice".equals(dir.getStudent("A").name()));
            check("size is unchanged", dir.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("null-Student update rejection implemented", false);
            blocked(e);
        }
    }

    private static void testUpdateWithMismatchedIdIsRejected() {
        section("A17 - updateStudent with a mismatched record ID is rejected");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            check("updating key A with a record for B returns false",
                    !dir.updateStudent("A", new Student("B", "Bob", 91.0)));
            check("the stored record is unchanged", "Alice".equals(dir.getStudent("A").name()));
            check("no record for B was stored", dir.getStudent("B") == null);
            check("size is unchanged", dir.size() == 1);
        } catch (UnsupportedOperationException e) {
            check("update ID-consistency rejection implemented", false);
            blocked(e);
        }
    }

    private static void testAddStudentWithNullRecordIdIsRejected() {
        section("A18 - a Student whose own ID is null is rejected");
        StudentDirectory dir = new StudentDirectory();
        try {
            check("addStudent with a null record ID returns false",
                    !dir.addStudent("A", new Student(null, "NoId", 90.0)));
            check("nothing was stored", dir.size() == 0);
        } catch (UnsupportedOperationException e) {
            check("null-record-ID rejection implemented", false);
            blocked(e);
        }
    }

    private static void testRemovingTheLastStudentEmptiesTheDirectory() {
        section("A19 - removing the last student empties the directory");
        StudentDirectory dir = new StudentDirectory();
        try {
            dir.addStudent("A", new Student("A", "Alice", 89.5));
            check("removing the only student returns true", dir.removeStudent("A"));
            check("size is 0", dir.size() == 0);
            check("the student is gone", dir.getStudent("A") == null);
            check("containsStudent is false", !dir.containsStudent("A"));
            check("the snapshot is empty", dir.snapshot().isEmpty());
        } catch (UnsupportedOperationException e) {
            check("remove-to-empty implemented", false);
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
