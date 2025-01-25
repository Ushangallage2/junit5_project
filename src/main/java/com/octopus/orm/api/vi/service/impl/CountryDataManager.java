package com.octopus.orm.api.vi.service.impl;

import com.octopus.orm.api.vi.pojo.ApiV1Country;

import java.util.ArrayList;

public class CountryDataManager {
    private CountryDAO countryDAO;

    public CountryDataManager() {
        this.countryDAO = new CountryDAO();
    }

    public ApiV1Country addCountry(ApiV1Country country) {
        return countryDAO.addCountry(country);
    }

    public ArrayList<ApiV1Country> getAllCountries() {
        return countryDAO.getAllCountries();
    }

    public ApiV1Country updateCountry(ApiV1Country country) {
        return countryDAO.updateCountry(country);
    }

    public ApiV1Country getCountryByName(String name) {
        return countryDAO.getCountryByName(name);
    }

    public ApiV1Country getCountryByCode(String code) {
        return countryDAO.getCountryByCode(code);
    }
}