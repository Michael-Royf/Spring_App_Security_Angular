package com.michael.document.utils.emailUtils;

public class EmailUtils {
    public static String getEmailMessage(String name, String host, String key) {
        return "Hello " + name + ",\n\nYou new account has bean created. Please click on the link below to verify you account. \n\n" +
                getVerificationUrl(host, key) + "\n\nThe Support Team";
    }

    private static String getVerificationUrl(String host, String key) {
        return host + "/user/verify/account?key=" + key;
    }
    public static String getResetPasswordMessage(String name, String host, String token) {
        return "Hello " + name + ",\n\nPlease use this link below to reset your password. \n\n" +
                getResetPasswordUrl(host, token) + "\n\nThe Support Team";

    }

    private static String getResetPasswordUrl(String host, String key) {
        return host + "/user/verify/password?key=" + key;
    }

}
