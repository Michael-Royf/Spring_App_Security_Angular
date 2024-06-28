package com.michael.document.utils.validations;

import com.google.common.base.Joiner;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        final PasswordValidator validator = new PasswordValidator(
                // length between 8 and 16 characters
                new LengthRule(8, 16),
                // at least one upper-case character
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                // at least one lower-case character
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                // at least one digit character
                new CharacterRule(EnglishCharacterData.Digit, 1),
                // at least one symbol (special character)
                new CharacterRule(EnglishCharacterData.Special, 1),
                // define some illegal sequences that will fail when >= 5 chars long
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                // alphabetical is of the form 'abcde', numerical is '34567', qwery is 'asdfg'
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                // the false parameter indicates that wrapped sequences are allowed; e.g. 'xyzabc'
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                // no whitespace
                new WhitespaceRule()
        );

        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(Joiner.on(",")
                .join(validator.getMessages(result))).addConstraintViolation();
        return false;
    }
}
