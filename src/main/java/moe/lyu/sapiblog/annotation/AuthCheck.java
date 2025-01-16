package moe.lyu.sapiblog.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.METHOD,
        ElementType.TYPE
})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    boolean jwtDbCheck() default false;
    boolean skipCheck() default false;
}
