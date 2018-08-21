package com.kiwifisher.mobstacker.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Utility for comparing collection contents.
 *
 * @author Jikoo
 */
public class CollectionUtils {

    private CollectionUtils() {}

    public static <T> boolean equal(Collection<T> collection1, Collection<T> collection2) {
        if (collection1 == collection2) {
            return true;
        }

        if (collection1 == null || collection2 == null || collection1.size() != collection2.size()) {
            return false;
        }

        if (collection1.isEmpty()) {
            // No need to check both, size already must be identical
            return true;
        }

        /*
         * Unfortunately, our elements are not capable of being sorted. We must iterate over the
         * entire contents to ensure each is contained. Since there's no guarantee that each
         * Collection contains only one of each object, we create a copy of one to modify.
         */
        List<T> clonedElements = new ArrayList<>(collection2);

        for (Iterator<T> iterator = collection1.iterator(); iterator.hasNext();) {
            if (!clonedElements.remove(iterator.next())) {
                return false;
            }
        }

        return true;
    }

}
