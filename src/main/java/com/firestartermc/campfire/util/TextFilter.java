package com.firestartermc.campfire.util;

import com.firestartermc.campfire.Campfire;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextFilter {

    private final Set<Pattern> filters;

    public TextFilter(@NotNull Campfire campfire) {
        this.filters = campfire.getConfig().getStringList("filters").stream()
                .map(Pattern::compile)
                .collect(Collectors.toSet());
    }

    @NotNull
    public Set<Pattern> getFilters() {
        return filters;
    }

    public boolean checkText(@NotNull String text) {
        var clean = cleanText(text);
        return getFilters().stream().anyMatch(filter -> filter.matcher(clean).find());
    }

    @NotNull
    private String cleanText(@NotNull String text) {
        return text.toLowerCase().replace(" ", "").replace("_", "");
    }
}
