package com.octopus.orm.api.vi.pojo;

import com.octopus.orm.api.vi.service.impl.CountryDataManager;
import com.octopus.orm.api.vi.service.impl.OctopusDAOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CountryServiceImplTest {

    private CountryServiceImpl countryService;

    @Mock
    private CountryDataManager countryDataManager;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        countryService = new CountryServiceImpl(countryDataManager);
    }

    @Test
    public void testAddCountryRecord_Success() {
        ApiV1Country country = new ApiV1Country();
        country.setCountryCode("US");
        country.setCountryName("United States");

        when(countryDataManager.addCountry(any())).thenReturn(country);

        Response response = countryService.addCountryRecord(country);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country added successfully"));
    }

    @Test
    public void testAddCountryRecord_Conflict() {
        ApiV1Country country = new ApiV1Country();
        country.setCountryCode("US");
        country.setCountryName("United States");

        when(countryDataManager.addCountry(any())).thenReturn(null); // Simulating conflict

        Response response = countryService.addCountryRecord(country);

        assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country with this code already exists."));
    }

    @Test
    public void testGetAllCountryRecords_Success() {
        ArrayList<ApiV1Country> countries = new ArrayList<>();
        countries.add(new ApiV1Country()); // Initialize with sample country
        countries.get(0).setCountryCode("US");
        countries.get(0).setCountryName("United States");

        when(countryDataManager.getAllCountries()).thenReturn(countries);

        Response response = countryService.getAllCountryRecords();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Countries retrieved successfully"));
    }

    @Test
    public void testGetAllCountryRecords_EmptyList() {
        when(countryDataManager.getAllCountries()).thenReturn(new ArrayList<>()); // Empty list

        Response response = countryService.getAllCountryRecords();

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country list is empty"));
    }

    @Test
    public void testEditCountryRecord_Success() {
        ApiV1Country country = new ApiV1Country();
        country.setCountryId(1);
        country.setCountryCode("US");
        country.setCountryName("United States");

        when(countryDataManager.updateCountry(any())).thenReturn(country);

        Response response = countryService.editCountryRecord(country);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country edited successfully"));
    }

    @Test
    public void testGetCountryByName_Success() {
        ApiV1Country country = new ApiV1Country();
        country.setCountryCode("US");
        country.setCountryName("United States");

        when(countryDataManager.getCountryByName(anyString())).thenReturn(country);

        Response response = countryService.getCountryByName("United States");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country retrieved successfully"));
    }



    @Test
    public void testGetCountryByCode_Success() throws OctopusDAOException {
        ApiV1Country country = new ApiV1Country();
        country.setCountryCode("US");
        country.setCountryName("United States");

        when(countryDataManager.getCountryByCode(anyString())).thenReturn(country);

        Response response = countryService.getCountryByCode("US");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country retrieved successfully"));
    }

    @Test
    public void testGetCountryByName_NotFound() {
        when(countryDataManager.getCountryByName(anyString())).thenReturn(null);

        Response response = countryService.getCountryByName("Unknown Country");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country name not found"));
    }

    @Test
    public void testGetCountryByCode_NotFound() throws OctopusDAOException {
        when(countryDataManager.getCountryByCode(anyString())).thenReturn(null);

        Response response = countryService.getCountryByCode("Unknown Code");

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Country code not found"));
    }
}