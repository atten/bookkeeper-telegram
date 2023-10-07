package bookkeeper.resolverAnnotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Expenditure {
    bookkeeper.enums.Expenditure value();
}
