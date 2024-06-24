package com.michael.document.controllers;

import com.michael.document.domain.User;
import com.michael.document.enumerations.TokenType;
import com.michael.document.handler.ApiLogoutHandler;
import com.michael.document.payload.request.*;
import com.michael.document.payload.response.Response;
import com.michael.document.service.JwtService;
import com.michael.document.service.ProfileImageService;
import com.michael.document.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static com.michael.document.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ProfileImageService profileImageService;
    private final JwtService jwtService;
    private final ApiLogoutHandler apiLogoutHandler;

    @PostMapping("/register")
    public ResponseEntity<Response> saveUser(@RequestBody @Valid RegistrationRequest registrationRequest,
                                             HttpServletRequest request) throws IOException {
        userService.createUser(registrationRequest);
        return ResponseEntity.created(URI.create(""))
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account created. Check your email to enable your account",
                        HttpStatus.CREATED));
    }


    @GetMapping("/verify/account")
    public ResponseEntity<Response> verify(@RequestParam("key") String key,
                                           HttpServletRequest request) {
        userService.verifyAccountKey(key);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account verified.",
                        HttpStatus.OK));
    }

    @PatchMapping("/mfa/setup")
    public ResponseEntity<Response> setUpMfa(@AuthenticationPrincipal User userPrincipal,
                                             HttpServletRequest request) {
        var user = userService.setUpMfa(userPrincipal.getId());
        return ResponseEntity.ok().body(getResponse(request, Map.of("user", user), "MFA set up successfully", HttpStatus.OK));
    }

    @PatchMapping("/mfa/cancel")
    public ResponseEntity<Response> cancelMfa(@AuthenticationPrincipal User userPrincipal,
                                              HttpServletRequest request) {
        var user = userService.cancelMfa(userPrincipal.getId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "MFA canceled up successfully",
                        HttpStatus.OK));
    }


    @PostMapping("/verify/qrcode")
    public ResponseEntity<Response> verifyQrCode(@RequestBody QrCodeRequest qrCodeRequest,
                                                 HttpServletResponse response,
                                                 HttpServletRequest request) {
        var user = userService.verifyQrCode(qrCodeRequest.getUserId(), qrCodeRequest.getQrCode());
        jwtService.addCookie(response, user, TokenType.ACCESS);
        jwtService.addCookie(response, user, TokenType.REFRESH);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "QR code verifies.",
                        HttpStatus.OK));
    }

    //reset password when user not logged
    @PostMapping("/reset_password")
    public ResponseEntity<Response> resetPassword(@RequestBody @Valid EmailRequest emailRequest,
                                                  HttpServletRequest request) {
        userService.resetPassword(emailRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "We sent you an email to reset you password.",
                        HttpStatus.OK));
    }

    @GetMapping("/verify/password")
    public ResponseEntity<Response> verifyPassword(@RequestParam("key") String key,
                                                   HttpServletRequest request) {
        var user = userService.verifyPasswordKey(key);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "Enter new password",
                        HttpStatus.OK));
    }

    @PostMapping("/reset_password/reset")
    public ResponseEntity<Response> doResetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest,
                                                    HttpServletRequest request) {
        userService.updatePassword(resetPasswordRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Password reset successfully.",
                        HttpStatus.OK));
    }


    //reset password when user is logged
    @PatchMapping("/update_password")
    public ResponseEntity<Response> updatePassword(@AuthenticationPrincipal User user,
                                                   @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                   HttpServletRequest request) {
        userService.updatePassword(user.getUserId(), updatePasswordRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Password updated successfully.",
                        HttpStatus.OK));
    }


    //profile
    @GetMapping("/profile")
    //  @PreAuthorize("hasAnyAuthority('user:read') or hasAnyRole('USER', 'ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Response> profile(@AuthenticationPrincipal User userPrincipal,
                                            HttpServletRequest request) {
        var user = userService.getUserByUserId(userPrincipal.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "Profile retrieved.",
                        HttpStatus.OK));
    }

    @PatchMapping("/update")
    public ResponseEntity<Response> updateUserProfile(@AuthenticationPrincipal User userPrincipal,
                                                      @RequestBody RegistrationRequest registrationRequest,
                                                      HttpServletRequest request) {
        var user = userService.updateUser(userPrincipal.getUserId(), registrationRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("user", user),
                        "User update successfully.",
                        HttpStatus.OK));
    }

    @PatchMapping("/update_role")
    public ResponseEntity<Response> updateUserRole(@AuthenticationPrincipal User userPrincipal,
                                                   @RequestBody RoleRequest roleRequest,
                                                   HttpServletRequest request) {
        userService.updateRole(userPrincipal.getUserId(), roleRequest);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Role update successfully.",
                        HttpStatus.OK));
    }

    @GetMapping(value = "/image/{filename}", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public ResponseEntity<?> getProfileImage(@PathVariable("filename") String filename) throws IOException {
        byte[] profileImage = profileImageService.getProfileImage(filename);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_JPEG_VALUE))
                .body(new ByteArrayResource(profileImage));
    }

    @PatchMapping("/image")
    public ResponseEntity<Response> updateImageProfile(@AuthenticationPrincipal User user,
                                                @RequestParam("file") MultipartFile file,
                                                HttpServletRequest request) throws IOException {
        log.info("user {}", user );
        var imageUrl = profileImageService.updateProfileImage(user, file);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("imageUrl", imageUrl),
                        "Photo update successfully.",
                        HttpStatus.OK));
    }



    @PatchMapping("/image/delete")
    public ResponseEntity<Response> deleteProfileImage(@AuthenticationPrincipal User user,
                                                HttpServletRequest request) throws IOException {
        var imageUrl = profileImageService.deleteProfileImageAndSetDefaultImage(user);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        Map.of("imageUrl", imageUrl),
                        "Photo delete successfully.",
                        HttpStatus.OK));
    }




    @PostMapping("/logout")
    public ResponseEntity<Response> logout(HttpServletResponse response,
                                           HttpServletRequest request,
                                           Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        apiLogoutHandler.logout(request, response, authentication);
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "You logged put successfully.",
                        HttpStatus.OK));
    }


    @PatchMapping("/toggle_account_expired")
    public ResponseEntity<Response> toggleAccountExpired(@AuthenticationPrincipal User user,
                                                         HttpServletRequest request) {
        userService.toggleAccountExpired(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        HttpStatus.OK));
    }

    @PatchMapping("/toggle_account_locked")
    public ResponseEntity<Response> toggleAccountLocked(@AuthenticationPrincipal User user,
                                                        HttpServletRequest request) {
        userService.toggleAccountLocked(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        HttpStatus.OK));
    }

    @PatchMapping("/toggle_account_enabled")
    public ResponseEntity<Response> toggleAccountEnabled(@AuthenticationPrincipal User user,
                                                         HttpServletRequest request) {
        userService.toggleAccountEnabled(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        HttpStatus.OK));
    }

    @PatchMapping("/toggle_credentials_expired")
    public ResponseEntity<Response> toggleCredentialsExpired(@AuthenticationPrincipal User user,
                                                             HttpServletRequest request) {
        userService.toggleCredentialsExpired(user.getUserId());
        return ResponseEntity.ok()
                .body(getResponse(
                        request,
                        emptyMap(),
                        "Account updated successfully.",
                        HttpStatus.OK));
    }
}
