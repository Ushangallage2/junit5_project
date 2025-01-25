package com.octopus.orm.api.vi.service.impl;

public class OctopusDAOException extends Exception {
    public static final String COUNTRY_SAVING_FAILED = "Error saving country";
    public static final String COUNTRY_NAME_NOT_FOUND = "Country name not found";
    public static final String COUNTRY_CODE_NOT_FOUND = "Country code not found";

    public OctopusDAOException(String message) {
        super(message);
    }
}