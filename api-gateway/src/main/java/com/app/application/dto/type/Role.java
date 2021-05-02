package com.app.application.dto.type;

public enum Role {
    ROLE_DOCTOR,
    ROLE_PATIENT;

    public static boolean isDoctor(String roleValue) {

        return ROLE_DOCTOR.toString().equalsIgnoreCase(roleValue);

    }
}
