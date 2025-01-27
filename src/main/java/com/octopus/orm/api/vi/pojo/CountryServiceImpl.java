package com.octopus.orm.api.vi.pojo;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.octopus.orm.api.vi.service.impl.CountryDataManager;
import com.octopus.orm.api.vi.service.impl.OctopusDAOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path("/1.0/country")
public class CountryServiceImpl {

    private static final Logger log = LogManager.getLogger(CountryServiceImpl.class);
    private static final String SUCCESS_STATUS = "success";
    private static final String FAILURE_STATUS = "failure";

    @Context
    private ServletContext servletContext;

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Context
    HttpHeaders headers;

    private final Gson gson = new GsonBuilder().create();
    private final CountryDataManager countryDataManager; // No initialization here

    // Constructor for dependency injection
    public CountryServiceImpl(CountryDataManager countryDataManager) {
        this.countryDataManager = countryDataManager != null ? countryDataManager : new CountryDataManager();
    }

    @POST
    @Path("/addCountry")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response addCountryRecord(ApiV1Country data) {
        log.info("COUNTRY ADD CALLED");
        try {
            ApiV1Country result = countryDataManager.addCountry(data);
            if (result == null) { // Conflict detected
                return Response.status(Response.Status.CONFLICT)
                        .entity(createResponse(FAILURE_STATUS, "Country with this code already exists.", null))
                        .build();
            } else {
                return Response.ok(createResponse(SUCCESS_STATUS, "Country added successfully", result)).build();
            }
        } catch (Exception e) {
            log.error("Exception", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(createResponse(FAILURE_STATUS, "Error occurred while adding the country.", null))
                    .build();
        }
    }

    @POST
    @Path("/list")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getAllCountryRecords() {
        log.info("COUNTRY GET ALL CALLED");
        try {
            ArrayList<ApiV1Country> result = countryDataManager.getAllCountries();
            if (result == null || result.isEmpty()) {
                throw new OctopusDAOException("Country list is empty");
            }
            return Response.ok(createResponse(SUCCESS_STATUS, "Countries retrieved successfully", result)).build();
        } catch (OctopusDAOException e) {
            log.error("DAO Exception", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(createResponse(FAILURE_STATUS, e.getMessage(), null)).build();
        } catch (Exception e) {
            log.error("Exception", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(createResponse(FAILURE_STATUS, "Error retrieving countries.", null)).build();
        }
    }

    @POST
    @Path("/editCountry")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response editCountryRecord(ApiV1Country data) {
        log.info("COUNTRY EDIT CALLED");
        try {
            ApiV1Country result = countryDataManager.updateCountry(data);
            return Response.ok(createResponse(SUCCESS_STATUS, "Country edited successfully", result)).build();
        } catch (Exception e) {
            log.error("Exception", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(createResponse(FAILURE_STATUS, "Error occurred while editing the country.", null)).build();
        }
    }

    @GET
    @Path("/countryName")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCountryByName(@QueryParam("name") String name) {
        log.info("GET COUNTRY BY NAME CALLED");
        try {
            ApiV1Country result = countryDataManager.getCountryByName(name);
            if (result == null) {
                throw new OctopusDAOException(OctopusDAOException.COUNTRY_NAME_NOT_FOUND);
            }
            return Response.ok(createResponse(SUCCESS_STATUS, "Country retrieved successfully", result)).build();
        } catch (OctopusDAOException e) {
            log.error("DAO Exception", e);
            return Response.status(Response.Status.NOT_FOUND).entity(createResponse(FAILURE_STATUS, e.getMessage(), null)).build();
        } catch (Exception e) {
            log.error("Exception", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(createResponse(FAILURE_STATUS, "Error occurred while retrieving country by name.", null)).build();
        }
    }

    @GET
    @Path("/countryCode")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCountryByCode(@QueryParam("code") String code) {
        log.info("GET COUNTRY BY CODE CALLED");
        try {
            ApiV1Country result = countryDataManager.getCountryByCode(code);
            log.info("Result from DAO: " + result);

            if (result == null) {
                throw new OctopusDAOException(OctopusDAOException.COUNTRY_CODE_NOT_FOUND);
            }

            return Response.ok(createResponse(SUCCESS_STATUS, "Country retrieved successfully", result)).build();
        } catch (OctopusDAOException e) {
            log.error("DAO Exception", e);
            return Response.status(Response.Status.NOT_FOUND).entity(createResponse(FAILURE_STATUS, e.getMessage(), null)).build();
        } catch (Exception e) {
            log.error("Exception", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(createResponse(FAILURE_STATUS, "Error occurred while retrieving country by code.", null)).build();
        }
    }

    private String createResponse(String status, String message, Object data) {
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status);
        responseMap.put("message", message);
        responseMap.put("data", data);
        return gson.toJson(responseMap);
    }
}