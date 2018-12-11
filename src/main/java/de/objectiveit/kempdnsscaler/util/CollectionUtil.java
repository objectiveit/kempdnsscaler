package de.objectiveit.kempdnsscaler.util;

import java.util.List;
import java.util.stream.Collectors;

public class CollectionUtil {

    /**
     * Returns subtraction of lists: {@code from \ subtrahend}.
     *
     * @param from       minuend list
     * @param subtrahend subtrahend list
     * @return
     */
    public static List<String> subtract(List<String> from, List<String> subtrahend) {
        List<String> result = from.stream()
                .filter(next -> !subtrahend.contains(next))
                .collect(Collectors.toList());
        return result;
    }

}
