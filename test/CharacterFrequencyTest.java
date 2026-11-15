import java.util.Map;

/**
 * Student-authored edge-case suite for the character-frequency counter (Part B).
 *
 * Approved counting rule (D6): count every character exactly as supplied.
 * Uppercase and lowercase are SEPARATE keys; spaces and punctuation ARE counted.
 * Empty input yields an empty map; null input is rejected with
 * IllegalArgumentException.
 *
 * Each test names ONE behaviour and the single reason it would fail. At the Test
 * checkpoint every behaviour is expected to FAIL, because the counter is a stub.
 */
public class CharacterFrequencyTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== CharacterFrequencyTest (student-authored edge cases) ===");
        System.out.println();

        testEmptyInput();
        testSingleCharacterInput();
        testRepeatedCharacterOnly();
        testMixedCharacterSet();
        testMidStringRepeat();
        testCaseIsCountedSeparately();
        testWhitespaceAndPunctuationAreCounted();
        testCountsSumToInputLength();
        testNullInputIsRejected();
        testNonAsciiCharacterIsOneKey();

        System.out.println();
        System.out.println("CharacterFrequencyTest results: " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            throw new AssertionError(failed + " edge-case test(s) failed");
        }
    }

    private static void testEmptyInput() {
        section("F1 - empty input yields an empty map");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("");
            check("the result is not null", freq != null);
            check("the result is empty", freq != null && freq.isEmpty());
            check("the result has no entries", freq != null && freq.size() == 0);
        } catch (UnsupportedOperationException e) {
            check("empty-input handling implemented", false);
            blocked(e);
        }
    }

    private static void testSingleCharacterInput() {
        section("F2 - single-character input");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("x");
            check("the map has exactly one entry", freq.size() == 1);
            check("the character is counted once", countOf(freq, 'x') == 1);
        } catch (UnsupportedOperationException e) {
            check("single-character handling implemented", false);
            blocked(e);
        }
    }

    private static void testRepeatedCharacterOnly() {
        section("F3 - one character repeated");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("aaa");
            check("the map has exactly one entry", freq.size() == 1);
            check("the character is counted three times", countOf(freq, 'a') == 3);
        } catch (UnsupportedOperationException e) {
            check("repeated-character counting implemented", false);
            blocked(e);
        }
    }

    private static void testMixedCharacterSet() {
        section("F4 - mixed character set");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("aabb");
            check("the map has exactly two entries", freq.size() == 2);
            check("'a' is counted twice", countOf(freq, 'a') == 2);
            check("'b' is counted twice", countOf(freq, 'b') == 2);
        } catch (UnsupportedOperationException e) {
            check("mixed-character counting implemented", false);
            blocked(e);
        }
    }

    private static void testMidStringRepeat() {
        section("F5 - a repeat in the middle of the string");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("xyzzy");
            check("the map has exactly three entries", freq.size() == 3);
            check("'x' is counted once", countOf(freq, 'x') == 1);
            check("'y' is counted twice", countOf(freq, 'y') == 2);
            check("'z' is counted twice", countOf(freq, 'z') == 2);
        } catch (UnsupportedOperationException e) {
            check("mid-string repeat counting implemented", false);
            blocked(e);
        }
    }

    private static void testCaseIsCountedSeparately() {
        section("F6 - uppercase and lowercase are separate keys (rule D6)");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("Aa");
            check("the map has exactly two entries", freq.size() == 2);
            check("'A' is counted once", countOf(freq, 'A') == 1);
            check("'a' is counted once", countOf(freq, 'a') == 1);
        } catch (UnsupportedOperationException e) {
            check("case-sensitive counting implemented", false);
            blocked(e);
        }
    }

    private static void testWhitespaceAndPunctuationAreCounted() {
        section("F7 - spaces and punctuation are counted (rule D6)");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("a, b");
            check("the map has exactly four entries", freq.size() == 4);
            check("'a' is counted once", countOf(freq, 'a') == 1);
            check("the space is counted once", countOf(freq, ' ') == 1);
            check("the comma is counted once", countOf(freq, ',') == 1);
            check("'b' is counted once", countOf(freq, 'b') == 1);
        } catch (UnsupportedOperationException e) {
            check("whitespace and punctuation counting implemented", false);
            blocked(e);
        }
    }

    private static void testCountsSumToInputLength() {
        section("F8 - the counts sum to the input length");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            // "the quick brown fox" is 19 characters:
            //   t h e _ q u i c k _ b r o w n _ f o x
            // 15 distinct letters (t,h,e,q,u,i,c,k,b,r,o,w,n,f,x) + 1 distinct space = 16 distinct.
            // The space occurs 3x (after "the", "quick", "brown"); the letter 'o' occurs 2x
            // (in "brown" and in "fox"); every other character occurs once.
            // 14x1 + 2 + 3 = 19 characters counted in total.
            String text = "the quick brown fox";
            Map<Character, Integer> freq = counter.count(text);
            int total = 0;
            for (int value : freq.values()) {
                total += value;
            }
            check("the counts sum to 19", total == 19);
            check("the string has 16 distinct characters (15 letters + 1 space)", freq.size() == 16);
            check("' ' occurs three times", countOf(freq, ' ') == 3);
            check("'o' occurs two times", countOf(freq, 'o') == 2);
        } catch (UnsupportedOperationException e) {
            check("count total implemented", false);
            blocked(e);
        }
    }

    private static void testNullInputIsRejected() {
        section("F9 - null input is rejected");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        String thrown = captureThrowable(new Runnable() {
            public void run() {
                counter.count(null);
            }
        });
        check("count(null) throws IllegalArgumentException",
                "java.lang.IllegalArgumentException".equals(thrown));
        if (thrown != null && !"java.lang.IllegalArgumentException".equals(thrown)) {
            System.out.println("       actually threw: " + thrown);
        }
    }

    private static void testNonAsciiCharacterIsOneKey() {
        section("F10 - a non-ASCII character is one distinct key");
        CharacterFrequencyCounter counter = new CharacterFrequencyCounter();
        try {
            Map<Character, Integer> freq = counter.count("a\u00e9");
            check("the map has exactly two entries", freq.size() == 2);
            check("'a' is counted once", countOf(freq, 'a') == 1);
            check("the accented character is counted once", countOf(freq, '\u00e9') == 1);
        } catch (UnsupportedOperationException e) {
            check("non-ASCII counting implemented", false);
            blocked(e);
        }
    }

    // --- helpers ---

    /** Count for a character, or -1 when absent, so a missing key is a failed check and not an NPE. */
    private static int countOf(Map<Character, Integer> freq, char c) {
        Integer v = freq.get(c);
        return v == null ? -1 : v;
    }

    /** Returns the fully-qualified class name of the thrown throwable, or null if none was thrown. */
    private static String captureThrowable(Runnable body) {
        try {
            body.run();
            return null;
        } catch (Throwable t) {
            return t.getClass().getName();
        }
    }

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
