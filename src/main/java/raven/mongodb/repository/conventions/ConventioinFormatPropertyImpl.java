package raven.mongodb.repository.conventions;

import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyModelBuilder;

import org.bson.codecs.pojo.annotations.BsonProperty;
import raven.data.entity.annotations.*;

import java.lang.annotation.Annotation;

public class ConventioinFormatPropertyImpl implements Convention {

    @Override
    public void apply(final ClassModelBuilder<?> classModelBuilder) {

        BsonFormatType formatType = BsonFormatType.Camel;
        for (final Annotation annotation : classModelBuilder.getAnnotations()) {
            if(annotation instanceof BsonFormatProperty){
                formatType = ((BsonFormatProperty)annotation).value();
                break;
            }
        }

        if(formatType == BsonFormatType.Camel)
            return;

        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            formatPropertyAnnotations(classModelBuilder, propertyModelBuilder);
        }
        //processCreatorAnnotation(classModelBuilder);
        //cleanPropertyBuilders(classModelBuilder);
    }


    private void formatPropertyAnnotations(final ClassModelBuilder<?> classModelBuilder,
                                            final PropertyModelBuilder<?> propertyModelBuilder) {

        for (Annotation annotation : propertyModelBuilder.getReadAnnotations()) {
            if (annotation instanceof BsonProperty) {
                return;
            }
        }

        for (Annotation annotation : propertyModelBuilder.getWriteAnnotations()) {
            if (annotation instanceof BsonProperty) {
                return;
            }
        }

        String name = propertyModelBuilder.getName();
        char[] _temp = name.toCharArray();
        _temp[0] = Character.toUpperCase(_temp[0]);
        name = new String(_temp);

        propertyModelBuilder.writeName(name);
        propertyModelBuilder.readName(name);
    }
}