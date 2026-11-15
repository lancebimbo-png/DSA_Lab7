import java.util.HashMap;
import java.util.Map;

/**
 * A keyed student directory backed by a {@link HashMap}.
 *
 * <p>Workbook Part B. Students are stored under their unique student ID:
 *
 * <pre>{@code
 * StudentDirectory directory = new StudentDirectory();
 * directory.addStudent("2025-001", new Student("2025-001", "Alice", 89.5));
 * directory.getStudent("2025-001");   // -> the Student record
 * }</pre>
 *
 * <p>Behaviour notes, following the approved design decisions:
 * <ul>
 *   <li><b>D1</b> — a duplicate ID is <em>rejected</em>, not overwritten. The
 *       existing record is kept and {@code false} is returned.</li>
 *   <li><b>D2</b> — a missing ID retrieves as {@code null}.</li>
 *   <li><b>D5</b> — {@link #snapshot()} returns a defensive copy, so callers can
 *       never modify the backing map.</li>
 *   <li><b>D7/D8</b> — a null ID, a null record, and a record whose own ID does
 *       not match the key are all rejected with {@code false}.</li>
 * </ul>
 *
 * <p>The workbook's plain {@code HashMap} replaces a value when the same key is
 * put twice. This class deliberately does not: the Part B contract asks for
 * duplicate student IDs to be rejected. The plain behaviour is demonstrated
 * separately in {@link HashMapBasics}.
 */
public class StudentDirectory {

    /** The backing store. Never handed out directly — only copies of it. */
    private final Map<String, Student> students = new HashMap<>();

    /**
     * Stores a student under the given ID.
     *
     * @return {@code true} if the student was stored; {@code false} if the ID or
     *         the record was null, if the record's own ID does not match the key,
     *         or if that ID is already taken (the existing student is kept).
     */
    public boolean addStudent(String id, Student student) {
        if (!isValidRecord(id, student)) {
            return false;
        }
        // putIfAbsent keeps the existing record when the ID is already present.
        // It returns the previous value, or null when the ID was not taken yet.
        // The previous value is never null here, because a null record was
        // rejected above, so "previous value == null" means "the ID was free".
        return students.putIfAbsent(id, student) == null;
    }

    /**
     * Retrieves a student by ID.
     *
     * @return the stored record, or {@code null} when the ID is unknown or null.
     */
    public Student getStudent(String id) {
        if (id == null) {
            return null;
        }
        return students.get(id);
    }

    /**
     * Replaces the record of an existing student.
     *
     * <p>This never inserts: an update against an unknown ID is rejected.
     *
     * @return {@code true} if the record was replaced; {@code false} otherwise.
     */
    public boolean updateStudent(String id, Student student) {
        if (!isValidRecord(id, student)) {
            return false;
        }
        // replace() only swaps an EXISTING mapping, so an update can never insert.
        // It returns the old value (never null, because null records are rejected),
        // so a non-null result means the ID existed. One lookup instead of two.
        return students.replace(id, student) != null;
    }

    /**
     * Removes a student by ID.
     *
     * @return {@code true} if a record was removed; {@code false} if the ID was
     *         unknown or null.
     */
    public boolean removeStudent(String id) {
        if (id == null) {
            return false;
        }
        // remove returns the value that was mapped, or null when there was no
        // mapping at all. Values are never null, so null means "nothing removed".
        return students.remove(id) != null;
    }

    /**
     * Returns a defensive copy of the directory.
     *
     * <p>The caller may modify the returned map freely — adding to it, removing
     * from it or clearing it — without affecting the directory, and later changes
     * to the directory do not affect a snapshot that was already taken.
     *
     * <p>{@code Student} is an immutable record, so the copy shares no mutable
     * state with the directory either.
     */
    public Map<String, Student> snapshot() {
        return new HashMap<>(students);
    }

    /** @return the number of stored records. */
    public int size() {
        return students.size();
    }

    /** @return {@code true} if the ID is present. */
    public boolean containsStudent(String id) {
        if (id == null) {
            return false;
        }
        return students.containsKey(id);
    }

    /**
     * Shared validation for add and update: the ID and record must be non-null
     * and the record's own ID must equal the key (decisions D7/D8).
     */
    private static boolean isValidRecord(String id, Student student) {
        return id != null
                && student != null
                && student.id() != null
                && student.id().equals(id);
    }
}
