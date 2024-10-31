package it.finanze.sanita.fse2.ms.iniclient.validators.impl;

import it.finanze.sanita.fse2.ms.iniclient.dto.IssuerCreateRequestDTO;
import it.finanze.sanita.fse2.ms.iniclient.utility.StringUtility;
import it.finanze.sanita.fse2.ms.iniclient.validators.ValidMiddlewareIssuer;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class MiddlewareIssuerValidator implements ConstraintValidator<ValidMiddlewareIssuer, IssuerCreateRequestDTO> {
    /**
     * Initializes the validator in preparation for calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidMiddlewareIssuer constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(IssuerCreateRequestDTO req, ConstraintValidatorContext context) {
        if (req.isMiddleware()) {
            return StringUtility.isNullOrEmpty(req.getNomeDocumentRepository());
        } else {
            return !StringUtility.isNullOrEmpty(req.getNomeDocumentRepository());
        }
    }

}