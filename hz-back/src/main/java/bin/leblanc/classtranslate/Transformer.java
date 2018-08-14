package bin.leblanc.classtranslate;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

public class Transformer {

    public static <T, K> K transform(T from, K to) {
        try {
            for (Field f : from.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Field targetField = to.getClass().getDeclaredField(f.getName());
                if (targetField == null) continue;
                if (f.get(from) == null) continue;
                targetField.setAccessible(true);
                targetField.set(to, f.get(from));
            }
        } catch (Exception e) {
        }
        return to;
    }

    public static <T, K> K transform(T from, Class<K> to) {
        try {
            return transform(from, to.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
