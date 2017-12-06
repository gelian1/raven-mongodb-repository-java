package raven.data.entity.annotations;


import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BsonPropertyFormat {
    BsonPropertyFormatType value() default BsonPropertyFormatType.CamelCase;
}
