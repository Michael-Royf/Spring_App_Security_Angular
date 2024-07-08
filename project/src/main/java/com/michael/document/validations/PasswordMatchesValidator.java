package com.michael.document.validations;

import com.michael.document.payload.request.RegistrationRequest;
import com.michael.document.payload.request.ResetPasswordRequest;
import com.michael.document.payload.request.UpdatePasswordRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {
    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RegistrationRequest) {
            RegistrationRequest registrationRequest = (RegistrationRequest) obj;
            return registrationRequest.getPassword().equals(registrationRequest.getConfirmationPassword());
        } else if (obj instanceof UpdatePasswordRequest) {
            UpdatePasswordRequest updatePasswordRequest = (UpdatePasswordRequest) obj;
            return updatePasswordRequest.getNewPassword().equals(updatePasswordRequest.getConfirmationPassword());
        } else if (obj instanceof ResetPasswordRequest) {
            ResetPasswordRequest resetPasswordRequest = (ResetPasswordRequest) obj;
            return resetPasswordRequest.getNewPassword().equals(resetPasswordRequest.getConfirmationPassword());
        } else
            return false;
    }
}