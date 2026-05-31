package com.source.bundleboard.config.logging;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class RegexMaskerHelper {

    private final List<String> patterns = new ArrayList<>();

    private Pattern pattern;

    public final void addMaskPattern(String maskPattern) {
        if (maskPattern == null || maskPattern.isEmpty())
            return;
        patterns.add(maskPattern);
        pattern = Pattern.compile(String.join("|", patterns), Pattern.MULTILINE);
    }

    protected final String maskMessage(String message) {
        if (message == null || message.isEmpty() || pattern == null)
            return message;

        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = pattern.matcher(sb);

        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, '*'));
                }
            });
        }
        return sb.toString();
    }

}