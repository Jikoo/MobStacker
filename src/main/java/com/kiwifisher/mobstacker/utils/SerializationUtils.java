package com.kiwifisher.mobstacker.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author Jikoo
 */
public class SerializationUtils {

    private SerializationUtils() {}

    // TODO: simplified complex list setup
    public static <T> void loadList(Object serialized, Class<T> paramClazz, String key, Map<String, Object> serialization) {
        String setterName = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
        Method setter;
        try {
            setter = serialized.getClass().getMethod(setterName, List.class);
        } catch (NoSuchMethodException | SecurityException e) {
            // Invalid setup.
            e.printStackTrace();
            return;
        }

        if (!serialization.containsKey(key)) {
            return;
        }

        Object savedListObject = serialization.get(key);
        if (!(savedListObject instanceof List)) {
            return;
        }

        List<T> loadedList = new ArrayList<>();
        List<?> savedList = (List<?>) savedListObject;

        for (Object object : savedList) {
            if (paramClazz.isInstance(object)) {
                loadedList.add(paramClazz.cast(object));
            }
        }

        if (!loadedList.isEmpty()) {
            try {
                setter.invoke(serialized, loadedList);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public static void load(Object serialized, Class<?> paramClazz, String key, Map<String, Object> serialization) {
        String setterName = "set" + Character.toUpperCase(key.charAt(0)) + key.substring(1);
        Method setter;
        try {
            setter = serialized.getClass().getMethod(setterName, paramClazz);
        } catch (NoSuchMethodException | SecurityException e) {
            // Invalid setup.
            e.printStackTrace();
            return;
        }

        // Ensure key is set.
        if (!serialization.containsKey(key)) {
            return;
        }

        Object value = serialization.get(key);

        if (paramClazz.isInstance(value)) {
            try {
                setter.invoke(serialized, paramClazz.cast(value));
            } catch (IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                // I've done something horrid.
                e.printStackTrace();
            }
        } else {
            System.out.println(String.format("Attempted to load %s for %s with key %s but found %s instead", paramClazz.getName(), serialized.getClass().getName(), key, value.getClass()));
        }
    }

}
