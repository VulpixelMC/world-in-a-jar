package gay.sylv.wij.impl.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <h1>DO NOT WRAP THIS METHOD CALL</h1>
 * <b>This annotation indicates that the annotated method is sensitive to the caller.</b>
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface CallerSensitive {
}
