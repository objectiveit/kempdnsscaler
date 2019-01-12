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

    /**
     * Checks equality of lists in terms of elements, i.e. [a, b, c] and [c, b, a, b] are equals.
     *
     * @param l1 list 1
     * @param l2 list 2
     * @return true if lists are equal
     */
    public static boolean equals(List<String> l1, List<String> l2) {
        boolean emptyCase = (l1 == null || l1.isEmpty()) && (l2 == null || l2.isEmpty());
        boolean nonEmptyCase = l1 != null && l2 != null && l1.containsAll(l2) && l2.containsAll(l1);
        return emptyCase || nonEmptyCase;
    }

}
