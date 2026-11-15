import java.util.HashMap;
import java.util.Map;

/**
 * Counts how often each character occurs in a string.
 *
 * <p>Workbook Part B. The approved counting rule (design decision <b>D6</b>) is
 * deliberately simple and literal:
 * <ul>
 *   <li>every character of the supplied text is counted, exactly as it appears;</li>
 *   <li>uppercase and lowercase are <em>separate</em> keys — "A" and "a" are
 *       different characters;</li>
 *   <li>spaces and punctuation are counted like any other character;</li>
 *   <li>empty text yields an empty map;</li>
 *   <li>null text is rejected with an {@link IllegalArgumentException}.</li>
 * </ul>
 *
 * <p>No case folding, no whitespace skipping and no punctuation filtering is
 * applied, because the workbook states no such rule and inventing one would be a
 * silent assumption.
 */
public class CharacterFrequencyCounter {

    /**
     * Builds a character-frequency map for the supplied text.
     *
     * @param text the text to analyse; must not be null.
     * @return a map from character to how many times it occurs; never null.
     * @throws IllegalArgumentException if {@code text} is null.
     */
    public Map<Character, Integer> count(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }

        Map<Character, Integer> frequency = new HashMap<>();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            Integer previous = frequency.get(character);
            if (previous == null) {
                frequency.put(character, 1);
            } else {
                frequency.put(character, previous + 1);
            }
        }
        return frequency;
    }
}
