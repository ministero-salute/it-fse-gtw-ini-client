package it.finanze.sanita.fse2.ms.iniclient.validators;


import it.finanze.sanita.fse2.ms.iniclient.validators.impl.MiddlewareIssuerValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MiddlewareIssuerValidator.class)
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMiddlewareIssuer {
    String message() default "The issuer is a middleware. The field 'nomeDocumentRepository' must be null or empty";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
