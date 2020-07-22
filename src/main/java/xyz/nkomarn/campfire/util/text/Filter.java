package xyz.nkomarn.campfire.util.text;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A small utility to check a string against filters for words that
 * are forbidden from use on the server.
 */
public class Filter {

    private static final List<Pattern> FILTERS = Arrays.asList(
            Pattern.compile("([n5]+(\\W|\\d|_)*[i1!l¡]+(\\W|\\d|_)*[g96qbß]+(\\W|\\d|_)*[eë3äa]+(\\W|\\d|_)*[r®]+)"),
            Pattern.compile("([n5]+(\\W|\\d|_)*[i1!l¡]+(\\W|\\d|_)*[g96qbß]+(\\W|\\d|_)*[aåä4]+)")
    );

    /**
     * Checks a string across the regex filters above.
     *
     * @param text The string to check against the filters.
     * @return True if the string matches any of the filters.
     */
    public static boolean checkString(@NotNull String text) {
        text = text.toLowerCase().replace(" ", "").replace("_", "");
        for (Pattern filter : FILTERS) {
            if (filter.matcher(text).find()) {
                return true;
            }
        }
        return false;
    }
}
