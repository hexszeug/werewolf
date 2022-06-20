package eu.hexsz.werewolf.update;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Converts a {@link Class} to a {@link String} using the simple name of the class.
 * The returned string is uppercase and has underscores between every word
 * (separated by camel case in the class name).
 * It confirms the enum serialization specified in the
 * <a href="https://google.github.io/styleguide/jsoncstyleguide.xml?showone=Property_Name_Format#Enum_Values">
 *     Google JSON Style Guide
 * </a>
 * @since 1.0-SNAPSHOT
 * @author hexszeug
 * */
public class ClassNameSerializer {
    private @Accessors(fluent = true) @Getter String value;

    public ClassNameSerializer(Object object) {
        if (object == null) {
            value = null;
            return;
        }
        Class clazz = object.getClass();
        if (object instanceof Class<?>) {
            clazz = (Class) object;
        }
        value = clazz
                .getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("([A-Z])([A-Z])([a-z])", "$1_$2$3")
                .toUpperCase();
    }
}
