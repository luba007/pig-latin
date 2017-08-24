package cz.lubos.piglatin;

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Simple implementation of pig-latin converter:<br/>
 * Write some Java code that translates a string (word, sentence, or paragraph) into “pig-latin” using the following rules.
 * <ol>
 * <li>Words that start with a consonant have their first letter moved to the end of the word and the letters “ay” added to the end.</li>
 * <ul>
 *     <li>Hello becomes Ellohay</li>
 * </ul>
 * <li>Words that start with a vowel have the letters “way” added to the end.</li>
 * <ul>
 *     <li>apple becomes appleway</li>
 * </ul>
 * <li>Words that end in “way” are not modified.</li>
 * <ul>
 *     <li>stairway stays as stairway</li>
 * </ul>
 * <li>Punctuation must remain in the same relative place from the end of the word.</li>
 * <ul>
 *     <li>can’t becomes antca’y</li>
 *     <li>end. becomes endway.</li>
 * </ul>
 * <li>Hyphens are treated as two words</li>
 * <ul>
 *     <li>this-thing becomes histay-hingtay</li>
 * </ul>
 * <li>Capitalization must remain in the same place.</li>
 * <ul>
 *     <li>Beach becomes Eachbay</li>
 *     <li>McCloud becomes CcLoudmay</li>
 * </ul>
 * </ol>
 */
public class PigLatinizerMain {

    private final static String VOWELS = "aeiou";
    private final static String CONSONANTS = "bcdfghjklmnpqrstvxwyz";
    private final static String IGNORABLE_WORD_SUFFIX = "way";
    private final static String HYPHEN = "-";
    private final static String WORD_SEPARATOR = " ";
    private final static String PUNCTUATION_PATTERN = "[\\.\\!\\?';’,\"]";
    private final static String SUFFIX_AY = "ay";
    private final static String SUFFIX_WAY = "way";

    private static WordPredicate startWithVowel = word -> VOWELS.indexOf(toLowerCase(word.charAt(0))) >= 0;
    private static WordPredicate startWithConsonant = word -> CONSONANTS.indexOf(toLowerCase(word.charAt(0))) >= 0;
    private static WordPredicate isIgnorableWord = word -> word.toLowerCase().endsWith(IGNORABLE_WORD_SUFFIX);
    private static WordPredicate isContainingHyphen = word -> word.contains(HYPHEN);

    public static void main(String[] args) {
        String sampleInput = "Hi! Hello apple starway can’t end this-thing Beach McCloud. Test, with a comma?!";
        String expectedResult = "Ihay! Ellohay appleway starway antca’y endway histay-hingtay Eachbay CcLoudmay. Esttay, ithway away ommacay?!";

        String result = Arrays.stream(sampleInput.split(WORD_SEPARATOR))
                .map(PigLatinizerMain::convertToPigLatin)
                .collect(Collectors.joining(WORD_SEPARATOR));

        System.out.println("INPUT:");
        System.out.println(sampleInput);
        System.out.println("\nRESULT:");
        System.out.println(result);
        System.out.println("\nEXPECTED:");
        System.out.println(expectedResult);
        System.out.println("\nVERIFICATION: " + (expectedResult.equals(result) ? "**SUCCESS**" : "**FAIL**"));
    }

    private static String convertToPigLatin(String word) {
        String resultWord;

        if (isContainingHyphen.check(word)) {
            resultWord = Arrays.stream(word.split(HYPHEN))
                    .map(PigLatinizerMain::convertToPigLatin)
                    .collect(Collectors.joining(HYPHEN));
        } else if (isIgnorableWord.check(word)) {
            return word;
        } else if (startWithConsonant.check(word)) {
            resultWord = word.substring(1) + toLowerCase(word.charAt(0)) + SUFFIX_AY;
        } else if (startWithVowel.check(word)) {
            resultWord = word + SUFFIX_WAY;
        } else {
            resultWord = word;
        }

        resultWord = capitalizeByPattern(resultWord, word);
        resultWord = punctuateByPattern(resultWord, word);

        return resultWord;
    }

    private static String capitalizeByPattern(String word, String pattern) {
        StringBuilder wordStringBuilder = new StringBuilder(word);
        for (int i = 0; i < pattern.length(); i++) {
            Character patternChar = pattern.charAt(i);
            if (isAlphabetic(patternChar)) {
                char wordChar = word.charAt(i);
                char capitalizedChar = isLowerCase(patternChar) ? toLowerCase(wordChar) : toUpperCase(wordChar);
                wordStringBuilder.setCharAt(i, capitalizedChar);
            }
        }
        return wordStringBuilder.toString();
    }

    private static String punctuateByPattern(String word, String pattern) {
        word = word.replaceAll(PUNCTUATION_PATTERN, "");
        StringBuilder wordStringBuilder = new StringBuilder(word);
        for (int i = pattern.length() - 1, indexFromEnd = 0; i >= 0; i--, indexFromEnd++) {
            char patternChar = pattern.charAt(i);
            if (Character.toString(patternChar).matches(PUNCTUATION_PATTERN)) {
                wordStringBuilder.insert(wordStringBuilder.length() - indexFromEnd, patternChar);
            }
        }
        return wordStringBuilder.toString();
    }

    @FunctionalInterface
    interface WordPredicate {
        Boolean check(String word);
    }
}