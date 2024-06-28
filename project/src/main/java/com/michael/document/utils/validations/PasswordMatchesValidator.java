package com.michael.document.utils.validations;

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
            RegistrationRequest reqistrationRequest = (RegistrationRequest) obj;
            return reqistrationRequest.getPassword().equals(reqistrationRequest.getConfirmationPassword());
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