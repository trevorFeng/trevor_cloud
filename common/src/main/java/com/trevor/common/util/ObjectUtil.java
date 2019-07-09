package com.trevor.common.util;

import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @author trevor
 * @date 06/27/19 18:20
 */
public class ObjectUtil {

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else {
            boolean empty = false;
            if (object instanceof CharSequence) {
                empty = !StringUtils.hasText((CharSequence)object);
            } else if (object instanceof Map) {
                empty = ((Map)object).isEmpty();
            } else if (object instanceof Iterable) {
                if (object instanceof Collection) {
                    empty = ((Collection)object).isEmpty();
                } else {
                    empty = !((Iterable)object).iterator().hasNext();
                }
            } else if (object.getClass().isArray()) {
                empty = Array.getLength(object) == 0;
            }

            return empty;
        }
    }
}
