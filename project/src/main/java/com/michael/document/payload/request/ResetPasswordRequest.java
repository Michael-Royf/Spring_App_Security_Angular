package com.michael.document.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.michael.document.validations.PasswordMatches;
import com.michael.document.validations.ValidPassword;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@PasswordMatches
public class ResetPasswordRequest {
    @NotEmpty(message = "User ID cannot be empty or null")
    private String userId;
    @ValidPassword
    @NotEmpty(message = "Password cannot be empty or null")
    private String newPassword;
    @NotEmpty(message = "Confirm password cannot be empty or null")
    private String confirmationPassword;
}
