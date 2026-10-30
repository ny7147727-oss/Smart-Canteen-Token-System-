package com.smartcanteen.common.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");

    private Validator() {
        // Prevent instantiation
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isPhoneValid(String phone) {
        if (phone == null) {
            return false;
        }
        String cleanPhone = phone.replaceAll("[\\s\\-+()]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    public static boolean isPositiveNumber(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isStrongPassword(String password) {
        if (password == null) {
            return false;
        }
        return password.trim().length() >= 6;
    }
}
