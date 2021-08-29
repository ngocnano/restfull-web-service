package com.ngoctm.app.ws.security;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864000000; //10 day
    public static final long EXPIRATION_TIME_RESET_PASSWORD = 3600000; //1h
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String VERIFICATION_EMAIL_URL = "/users/email";
    public static final String REQUEST_RESET_PASSWORD_URL = "/users/request-reset-password";
    public static final String RESET_PASSWORD_URL = "/users/reset-password";
    public static final String TOKEN_SECRET = "shevalong";
}
