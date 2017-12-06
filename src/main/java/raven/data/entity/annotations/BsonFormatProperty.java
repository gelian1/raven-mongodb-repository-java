package raven.data.entity.annotations;


import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BsonFormatProperty {
    BsonFormatType value() default BsonFormatType.Camel;
}
