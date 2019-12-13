package raven.mongodb.repository.conventions;

import org.bson.codecs.pojo.Convention;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;
import static org.bson.codecs.pojo.Conventions.CLASS_AND_PROPERTY_CONVENTION;

/**
 *
 */
public final class CustomConventions {

    /**
     *
     */
    public static final Convention FORMAT_PROPERTY_CONVENTION = new ConventioinFormatPropertyImpl();

    /**
     *
     */
    public static final List<Convention> DEFAULT_CONVENTIONS =
            unmodifiableList(asList(CLASS_AND_PROPERTY_CONVENTION, ANNOTATION_CONVENTION, FORMAT_PROPERTY_CONVENTION));


    /**
     *
     */
    private CustomConventions() {
    }
}
