package cn.bctools.mongodb.core.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public final class ClassUtils {
    private ClassUtils() {
    }

    public static void consumerNotNullField(Object o, Consumer<Field> fieldConsumer) {
        if (o != null) {
            Field[] declaredFields = o.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                boolean accessible = declaredField.isAccessible();
                if (!accessible) {
                    declaredField.setAccessible(true);
                }
                try {
                    if (declaredField.get(o) != null) {
                        fieldConsumer.accept(declaredField);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("get error", e);
                } finally {
                    declaredField.setAccessible(accessible);
                }
            }
        }
    }

    /**
     * @param field             字段
     * @param presentAnnotation 字段的注解再次被此注解修饰
     * @return
     */
    public static Annotation getFieldAnnotationisAnnotationPresent(Field field, Class<? extends Annotation> presentAnnotation) {
        Annotation annotation = null;
        Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
        for (Annotation declaredAnnotation : declaredAnnotations) {
            if (getAnnotationClass(declaredAnnotation).isAnnotationPresent(presentAnnotation)) {
                annotation = declaredAnnotation;
                break;
            }
        }
        return annotation;
    }


    public static Class<? extends Annotation> getAnnotationClass(Annotation annotation) {
        return annotation == null ? null : annotation.annotationType();
    }
}
