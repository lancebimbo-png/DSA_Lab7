import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * AUDIT evidence for the Lab 7 complexity table.
 *
 * <p><b>What this program measures, and what it deliberately does not.</b>
 * <ul>
 *   <li><b>setup</b> — work prepared BEFORE the timer starts. It is never part of
 *       the reported elapsed time.</li>
 *   <li><b>timed work</b> — the only region the timer covers. Each row names
 *       exactly what is inside it.</li>
 *   <li><b>apiCalls</b> — the number of {@code Map} method calls issued
 *       <em>inside the timed region</em>. Deterministic and machine-independent.</li>
 *   <li><b>internalOps</b> — <b>not counted</b>. Hash recomputations, bucket
 *       probes, chain walks and tree traversals happen inside {@code HashMap}
 *       and are not observable from outside. Nothing in this file pretends to
 *       count them.</li>
 *   <li><b>elapsedNanos</b> — wall-clock time of the timed region only, taken
 *       with {@code System.nanoTime()}. It includes JVM and runtime overhead, so
 *       it measures how long the work took on this machine, <b>not</b> how many
 *       internal operations were performed.</li>
 *   <li><b>spaceEntries</b> — the number of entries a structure holds. This is
 *       the auxiliary-space evidence, reported separately from time.</li>
 * </ul>
 *
 * <p><b>Measured runtime is an observation, never a complexity guarantee.</b> The
 * "predicted" column states theory; the "measured" column states what happened
 * here, on this machine, at this moment. The two are deliberately printed side
 * by side and never merged.
 *
 * <p>Protocol: two discarded warm-up runs, then the median of five timed runs.
 */
public class ComplexityAudit {

    private static final int[] SIZES = { 1000, 10000, 100000 };
    private static final int SHAPE_N = 50000;
    private static final int WARMUP = 2;
    private static final int REPEATS = 5;

    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== ComplexityAudit (Lab 7 - HashMap and Key-Value Structures) ===");
        System.out.println();
        System.out.println("Counter definitions:");
        System.out.println("  setup        = work prepared BEFORE the timer starts; never inside elapsedNs");
        System.out.println("  timed work   = the only region the timer covers; named in every row");
        System.out.println("  apiCalls     = Map method calls issued INSIDE the timed region (deterministic)");
        System.out.println("  internalOps  = NOT counted - HashMap bucket probes, chain walks and tree");
        System.out.println("                 traversals are internal to HashMap and are not observable");
        System.out.println("                 from outside, so no counter here attempts to count them");
        System.out.println("  elapsedNanos = wall-clock time of the TIMED REGION ONLY, from");
        System.out.println("                 System.nanoTime(); includes JVM and runtime overhead");
        System.out.println("  spaceEntries = entries held by the structure (the auxiliary-space evidence)");
        System.out.println();
        System.out.println("Predictions below are theory. Measurements below are observations on this");
        System.out.println("machine. A measured time is NOT a complexity guarantee.");
        System.out.println("Protocol: " + WARMUP + " discarded warm-up runs, median of " + REPEATS + " timed runs.");
        System.out.println();

        warmUpJit();

        directorySizeSweep();
        directoryShapeSweep();
        snapshotCost();
        frequencySweep();
        collidingKeyCheck();

        System.out.println();
        if (failed > 0) {
            throw new AssertionError(failed + " audit check(s) failed");
        }
        System.out.println("ComplexityAudit: all self-checks passed.");
    }

    /**
     * Runs a large, discarded workload before any measurement is taken.
     *
     * <p>The JVM compiles methods on an as-needed basis, so whichever measurement
     * happens to run first pays for the compilation. Lab 3's REFLECTION.md records
     * exactly this artifact: the first timed loop was skewed high and the later
     * ones were not. Running the whole workload once at the largest size and
     * throwing the result away puts every section in the same warmed state, so
     * the numbers in the different sections can be compared with one another.
     *
     * <p>Nothing produced here is reported.
     */
    private static void warmUpJit() {
        System.out.println("Warming up the JVM (discarded, not reported)...");
        List<String> ids = distributedIds(100000);
        List<Student> records = buildRecords(ids);
        StudentDirectory dir = new StudentDirectory();
        for (int i = 0; i < ids.size(); i++) {
            dir.addStudent(ids.get(i), records.get(i));
        }
        for (int i = 0; i < ids.size(); i++) {
            dir.getStudent(ids.get(i));
        }
        dir.snapshot();
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        counter.count(alphabetText(1000000));
        List<String> colliding = collidingKeys(50000);
        List<Student> collidingRecords = buildRecords(colliding);
        StudentDirectory collided = new StudentDirectory();
        for (int i = 0; i < colliding.size(); i++) {
            collided.addStudent(colliding.get(i), collidingRecords.get(i));
        }
        for (int i = 0; i < colliding.size(); i++) {
            collided.getStudent(colliding.get(i));
        }
        System.out.println("Warm-up done.");
        System.out.println();
    }

    // ------------------------------------------------------------------
    // 1. StudentDirectory - size sweep (average case)
    // ------------------------------------------------------------------

    private static void directorySizeSweep() {
        header("1. StudentDirectory - size sweep, shape S1 (distributed String IDs)");
        System.out.println("  This is the AVERAGE case: the IDs are ordinary sequential strings whose");
        System.out.println("  hash codes spread across the table.");
        System.out.println();
        System.out.println("  setup for every row below (all of it is OUTSIDE the timer):");
        System.out.println("    put    - the n Student records are built once, before any timing, and");
        System.out.println("             reused; the timed region allocates one StudentDirectory");
        System.out.println("             and issues n addStudent calls.");
        System.out.println("    get    - one populated directory is built before any timing and reused");
        System.out.println("             (a get never changes it); the timed region issues");
        System.out.println("             n getStudent calls and nothing else.");
        System.out.println("    remove - a FRESH populated directory is rebuilt before every single");
        System.out.println("             repetition (untimed), because the timed region empties it;");
        System.out.println("             the timed region issues n removeStudent calls and nothing");
        System.out.println("             else - no setup, no allocation, no construction is inside it.");
        System.out.println();
        System.out.printf("  %-8s %-9s %-30s %-9s %-13s %-12s %-10s %s%n",
                "n", "operation", "timed work (inside the timer)", "apiCalls", "elapsedNs",
                "ns/apiCall", "space", "predicted (theory)");

        for (int n : SIZES) {
            final List<String> ids = distributedIds(n);
            final List<Student> records = buildRecords(ids);

            long putNs = medianNanos(null, new Runnable() {
                public void run() {
                    StudentDirectory dir = new StudentDirectory();
                    for (int i = 0; i < ids.size(); i++) {
                        dir.addStudent(ids.get(i), records.get(i));
                    }
                }
            });
            System.out.printf("  %,8d %-9s %-30s %,9d %-13d %-12.2f %-10s %s%n",
                    n, "put", "1 directory + n addStudent", n, putNs, (double) putNs / n,
                    n + " rec", "O(1) avg / O(n) worst");

            final StudentDirectory built = new StudentDirectory();
            for (int i = 0; i < ids.size(); i++) {
                built.addStudent(ids.get(i), records.get(i));
            }

            long getNs = medianNanos(null, new Runnable() {
                public void run() {
                    for (int i = 0; i < ids.size(); i++) {
                        built.getStudent(ids.get(i));
                    }
                }
            });
            System.out.printf("  %,8d %-9s %-30s %,9d %-13d %-12.2f %-10s %s%n",
                    n, "get", "n getStudent", n, getNs, (double) getNs / n,
                    n + " rec", "O(1) avg / O(n) worst");

            final List<String> removeIds = ids;
            final List<Student> removeRecords = records;
            // The setup phase builds a FRESH populated directory before every
            // repetition and hands it to the timed phase through this holder.
            // The timed phase then only issues the removals.
            final StudentDirectory[] fixture = new StudentDirectory[1];
            long removeNs = medianNanos(new Runnable() {
                public void run() {
                    StudentDirectory fresh = new StudentDirectory();
                    for (int i = 0; i < removeIds.size(); i++) {
                        fresh.addStudent(removeIds.get(i), removeRecords.get(i));
                    }
                    fixture[0] = fresh;
                }
            }, new Runnable() {
                public void run() {
                    StudentDirectory fresh = fixture[0];
                    for (int i = 0; i < removeIds.size(); i++) {
                        fresh.removeStudent(removeIds.get(i));
                    }
                }
            });
            System.out.printf("  %,8d %-9s %-30s %,9d %-13d %-12.2f %-10s %s%n",
                    n, "remove", "n removeStudent", n, removeNs, (double) removeNs / n,
                    n + " rec", "O(1) avg / O(n) worst");
            System.out.println();
        }
    }

    // ------------------------------------------------------------------
    // 2. StudentDirectory - shape sweep (average vs pathological)
    // ------------------------------------------------------------------

    private static void directoryShapeSweep() {
        header("2. StudentDirectory - shape sweep at n = " + SHAPE_N
                + " (average case vs pathological case)");
        System.out.println("  S1 distributed   - ordinary sequential IDs; natural hash spread (AVERAGE)");
        System.out.println("  S2 shared prefix - long common prefix, varying suffix; realistic, clustered");
        System.out.println("  S3 colliding     - real String IDs that ALL share ONE hash code (WORST)");
        System.out.println();
        System.out.println("  Textbook worst case for a fully-colliding key set is O(n) per operation.");
        System.out.println("  On Java 8+ a bucket holding 8 or more entries is TREEIFIED into a red-black");
        System.out.println("  tree - but only when the table's capacity is at least 64. Below that");
        System.out.println("  capacity the map resizes instead of treeifying. A treeified bucket bounds");
        System.out.println("  the per-operation cost at O(log n) instead of O(n). The measurement below");
        System.out.println("  shows what actually happened here; the prediction was recorded before it.");
        System.out.println();
        System.out.println("  setup (outside the timer): the Student records are built once and reused;");
        System.out.println("  a populated directory is built once and reused for the get row.");
        System.out.println("  timed work: n addStudent calls, or n getStudent calls, and nothing else.");
        System.out.println();
        System.out.printf("  %-16s %-9s %-22s %-9s %-13s %-12s %s%n",
                "shape", "operation", "timed work", "apiCalls", "elapsedNs", "ns/apiCall",
                "predicted (theory)");

        List<String> s1 = distributedIds(SHAPE_N);
        List<String> s2 = sharedPrefixIds(SHAPE_N);
        List<String> s3 = collidingKeys(SHAPE_N);

        reportShape("S1 distributed", s1, "O(1) avg / O(n) worst");
        reportShape("S2 shared prefix", s2, "O(1) avg / O(n) worst");
        reportShape("S3 colliding", s3, "O(1) avg / O(n) worst (treeified: O(log n))");
    }

    private static void reportShape(String name, final List<String> ids, String prediction) {
        final List<Student> records = buildRecords(ids);

        long putNs = medianNanos(null, new Runnable() {
            public void run() {
                StudentDirectory dir = new StudentDirectory();
                for (int i = 0; i < ids.size(); i++) {
                    dir.addStudent(ids.get(i), records.get(i));
                }
            }
        });
        System.out.printf("  %-16s %-9s %-22s %,9d %-13d %-12.2f %s%n",
                name, "put", "n addStudent", ids.size(), putNs, (double) putNs / ids.size(), prediction);

        final StudentDirectory built = new StudentDirectory();
        for (int i = 0; i < ids.size(); i++) {
            built.addStudent(ids.get(i), records.get(i));
        }
        long getNs = medianNanos(null, new Runnable() {
            public void run() {
                for (int i = 0; i < ids.size(); i++) {
                    built.getStudent(ids.get(i));
                }
            }
        });
        System.out.printf("  %-16s %-9s %-22s %,9d %-13d %-12.2f %s%n",
                name, "get", "n getStudent", ids.size(), getNs, (double) getNs / ids.size(), prediction);
    }

    // ------------------------------------------------------------------
    // 3. Snapshot copying - time and auxiliary space together
    // ------------------------------------------------------------------

    private static void snapshotCost() {
        header("3. Snapshot copying - time AND auxiliary space (they are both linear)");
        System.out.println("  A snapshot copies every entry, so it costs O(n) time and O(n) space.");
        System.out.println("  Both grow with n; neither is amortised away.");
        System.out.println();
        System.out.println("  setup (outside the timer): a populated directory with n records.");
        System.out.println("  timed work: ONE snapshot() call, which copies all n entries.");
        System.out.println("  apiCalls = 1 (one snapshot call); entries = n (the size of the copy).");
        System.out.println();
        System.out.printf("  %-8s %-9s %-9s %-14s %-13s %-14s %s%n",
                "n", "apiCalls", "entries", "elapsedNs", "ns/entry", "space", "predicted (theory)");

        for (int n : SIZES) {
            final List<String> ids = distributedIds(n);
            final List<Student> records = buildRecords(ids);
            final StudentDirectory dir = new StudentDirectory();
            for (int i = 0; i < ids.size(); i++) {
                dir.addStudent(ids.get(i), records.get(i));
            }

            long snapshotNs = medianNanos(null, new Runnable() {
                public void run() {
                    Map<String, Student> copy = dir.snapshot();
                    if (copy.size() != ids.size()) {
                        throw new IllegalStateException("snapshot lost entries");
                    }
                }
            });
            System.out.printf("  %,8d %-9d %,9d %-14d %-13.2f %-14s %s%n",
                    n, 1, n, snapshotNs, (double) snapshotNs / n, n + " entries",
                    "O(n) time / O(n) space");
        }
    }

    // ------------------------------------------------------------------
    // 4. Character-frequency counting
    // ------------------------------------------------------------------

    private static void frequencySweep() {
        header("4. Character-frequency counting - time is linear in length, space in distinct keys");
        System.out.println("  charsRead   = number of characters read");
        System.out.println("  mapGets     = number of Map.get calls issued (one per character)");
        System.out.println("  mapPuts     = number of Map.put calls issued (one per character)");
        System.out.println("  mapApiCalls = mapGets + mapPuts; the counter performs BOTH a get and a");
        System.out.println("                put for every character, so this is 2 x charsRead.");
        System.out.println("                mapPuts on its own is NOT the whole Map traffic.");
        System.out.println("  distinctKeys = size of the resulting map (the auxiliary space)");
        System.out.println();
        System.out.println("  setup (outside the timer): the text is built once, before any timing.");
        System.out.println("  timed work: ONE count() call over the whole text.");
        System.out.println();
        System.out.println("  NO collision measurement is attempted for Character keys: Character.hashCode()");
        System.out.println("  IS the code point, so distinct characters of a realistic alphabet have");
        System.out.println("  distinct hash codes and a fully-colliding character set cannot be built.");
        System.out.println("  The O(n^2) pathological bound is therefore theoretical and is NOT claimed");
        System.out.println("  as measured here.");
        System.out.println();
        System.out.printf("  %-10s %-11s %-9s %-10s %-8s %-8s %-11s %-12s %-10s %s%n",
                "shape", "length", "distinct", "charsRead", "mapGets", "mapPuts", "mapApiCalls",
                "elapsedNs", "ns/char", "predicted (theory)");

        int[] lengths = { 1000, 100000, 1000000 };
        for (int length : lengths) {
            reportFrequency("alphabet26", alphabetText(length), length, "O(n) time / O(k) space");
            reportFrequency("one char", singleCharText(length), length, "O(n) time / O(1) space");
            reportFrequency("mixed", mixedText(length), length, "O(n) time / O(k) space");
        }
    }

    private static void reportFrequency(String shape, final String text, int length, String prediction) {
        final CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        final Map<Character, Integer> result = counter.count(text);
        final int distinctKeys = result.size();

        long elapsedNs = medianNanos(null, new Runnable() {
            public void run() {
                counter.count(text);
            }
        });

        System.out.printf("  %-10s %,11d %-9d %-10d %-8d %-8d %-11d %-12d %-10.2f %s%n",
                shape, length, distinctKeys, length, length, length, length * 2, elapsedNs,
                (double) elapsedNs / length, prediction);
    }

    // ------------------------------------------------------------------
    // 5. Self-checks on the colliding key construction
    // ------------------------------------------------------------------

    private static void collidingKeyCheck() {
        header("5. Self-checks on the colliding key set (S3)");
        List<String> keys = collidingKeys(1000);
        boolean allSame = true;
        for (String key : keys) {
            if (key.hashCode() != 65535) {
                allSame = false;
                break;
            }
        }
        expect("the generated keys are all distinct", distinct(keys));
        expect("all 1000 generated keys share one hash code", allSame);
        expect("the shared hash code is 65535", keys.get(0).hashCode() == 65535);
        System.out.println("  (the S3 shape uses real java.lang.String keys - the same key type the");
        System.out.println("   directory really stores - not a wrapper class, so nothing about the");
        System.out.println("   measurement depends on instrumenting the implementation.)");
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Runs the timed body {@code WARMUP + REPEATS} times, discards the warm-up
     * runs, and returns the median elapsed nanoseconds.
     *
     * <p>{@code setup} runs before every repetition and is never timed. Use it to
     * prepare the state the timed body needs - for example a fresh populated
     * directory, because the timed body may destroy that state.
     */
    private static long medianNanos(Runnable setup, Runnable timed) {
        for (int i = 0; i < WARMUP; i++) {
            if (setup != null) {
                setup.run();
            }
            timed.run();
        }
        long[] samples = new long[REPEATS];
        for (int i = 0; i < REPEATS; i++) {
            if (setup != null) {
                setup.run();
            }
            long start = System.nanoTime();
            timed.run();
            samples[i] = System.nanoTime() - start;
        }
        Arrays.sort(samples);
        return samples[samples.length / 2];
    }

    /**
     * Builds the Student records for a list of IDs.
     *
     * <p>The records are built ONCE, outside every timed region, and reused.
     * {@code Student} is an immutable record, so sharing it is safe, and it keeps
     * object allocation out of the measured code.
     */
    private static List<Student> buildRecords(List<String> ids) {
        List<Student> records = new ArrayList<>(ids.size());
        for (String id : ids) {
            records.add(new Student(id, "student", 90.0));
        }
        return records;
    }

    /** Shape S1: ordinary sequential IDs whose hash codes spread across the table. */
    private static List<String> distributedIds(int count) {
        List<String> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(String.format("S%06d", i));
        }
        return ids;
    }

    /** Shape S2: long common prefix, varying suffix. */
    private static List<String> sharedPrefixIds(int count) {
        List<String> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(String.format("2025-BSCPE-%06d", i));
        }
        return ids;
    }

    /**
     * Shape S3: {@code count} distinct three-character Strings that all share
     * ONE hash code.
     *
     * <p>A String's hash is {@code s[0]*31^(n-1) + ... + s[n-1]}, so for three
     * characters it is {@code 961*s[0] + 31*s[1] + s[2]}. Fixing the target hash
     * at 65535 and choosing {@code s[0]} and {@code s[1]} freely determines
     * {@code s[2]}, which yields tens of thousands of distinct strings that all
     * hash to the same value.
     *
     * <p>These are genuine {@code java.lang.String} keys - the same key type the
     * directory stores - so the pathological shape is measured with the real key
     * type rather than with an instrumented stand-in.
     */
    private static List<String> collidingKeys(int count) {
        final int target = 65535;
        List<String> keys = new ArrayList<>(count);
        for (int x = 0; keys.size() < count; x++) {
            for (int y = 0; keys.size() < count; y++) {
                int z = target - 961 * x - 31 * y;
                if (z < 0) {
                    break;
                }
                keys.add(new String(new char[] { (char) x, (char) y, (char) z }));
            }
        }
        return keys;
    }

    /** 26-letter alphabet, repeated. */
    private static String alphabetText(int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + (i % 26)));
        }
        return text.toString();
    }

    /** A single character repeated. */
    private static String singleCharText(int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append('a');
        }
        return text.toString();
    }

    /** Mixed sentence-like text with spaces and punctuation. */
    private static String mixedText(int length) {
        StringBuilder text = new StringBuilder(length);
        String alphabet = "the quick brown fox jumps over the lazy dog, 0123456789!";
        for (int i = 0; i < length; i++) {
            text.append(alphabet.charAt(i % alphabet.length()));
        }
        return text.toString();
    }

    private static boolean distinct(List<String> keys) {
        java.util.Set<String> seen = new java.util.HashSet<>(keys.size() * 2);
        for (String key : keys) {
            if (!seen.add(key)) {
                return false;
            }
        }
        return seen.size() == keys.size();
    }

    private static void header(String title) {
        System.out.println("--------------------------------------------------------------------");
        System.out.println(title);
        System.out.println("--------------------------------------------------------------------");
    }

    private static void expect(String what, boolean ok) {
        System.out.println((ok ? "  [PASS] " : "  [FAIL] ") + what);
        if (!ok) {
            failed++;
        }
    }
}
