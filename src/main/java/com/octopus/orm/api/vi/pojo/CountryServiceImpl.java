package com.octopus.orm.api.vi.pojo;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.octopus.orm.api.vi.pojo.ApiSharedMethods;
import com.octopus.orm.api.vi.service.impl.CountryDataManager;
import com.octopus.orm.api.vi.service.impl.OctopusDAOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Path("/1.0/country")
public class CountryServiceImpl {

    public static Logger log = LogManager.getLogger(CountryServiceImpl.class);


    @Context
    private ServletContext servletContext;

    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    @Context
    HttpHeaders headers;

    private final Gson gson = new GsonBuilder().create();
    private final CountryDataManager countryDataManager = new CountryDataManager();

    @POST
    @Path("/addCountry")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response addCountryRecord(ApiV1Country data) {
        log.info("COUNTRY ADD CALLED");
        String output = "NOK";
        int responseCode = Response.Status.BAD_REQUEST.getStatusCode();

        try {
            ApiV1Country result = countryDataManager.addCountry(data);
            if (result != null) {
                output = convertToApiString(result);
                responseCode = Response.Status.OK.getStatusCode(); // 200 OK
            } else {
                throw new OctopusDAOException("Error occurred while saving the country");
            }
        } catch (OctopusDAOException e) {
            output = e.getMessage();
        } catch (Exception e) {
            log.error("Exception", e);
            output = ApiSharedMethods.getErrorMessageString();
        }

        return Response.status(responseCode).entity(output).build();
    }

    @POST
    @Path("/list")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getAllCountryRecords() {
        log.info("COUNTRY GET ALL CALLED");
        ArrayList<ApiV1Country> result;
        int responseCode = Response.Status.BAD_REQUEST.getStatusCode();

        try {
            result = countryDataManager.getAllCountries();
            if (result != null && !result.isEmpty()) {
                responseCode = Response.Status.OK.getStatusCode(); // 200 OK
            } else {
                throw new OctopusDAOException("Country list is empty");
            }
        } catch (OctopusDAOException e) {
            log.error("DAO Exception", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Exception", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("INVALID DATA").build();
        }

        return Response.status(responseCode).entity(result).build();
    }

    @POST
    @Path("/editCountry")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response editCountryRecord(ApiV1Country data) {
        log.info("COUNTRY EDIT CALLED");
        int responseCode = Response.Status.BAD_REQUEST.getStatusCode();

        try {
            ApiV1Country result = countryDataManager.updateCountry(data);
            if (result != null) {
                responseCode = Response.Status.OK.getStatusCode(); // 200 OK
            } else {
                throw new OctopusDAOException("Error occurred while updating the country");
            }
        } catch (OctopusDAOException e) {
            log.error("DAO Exception", e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            log.error("Exception", e);
            return Response.status(Response.Status.BAD_REQUEST).entity("ERROR WHILE PROCESSING THE REQUEST").build();
        }

        return Response.status(responseCode).entity(data).build();
    }

    @GET
    @Path("/countryName")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCountryByName(@QueryParam("name") String name) {
        log.info("GET COUNTRY BY NAME CALLED");
        ApiV1Country result;
        String output = "NOK";
        int responseCode = Response.Status.BAD_REQUEST.getStatusCode();

        try {
            result = countryDataManager.getCountryByName(name);
            if (result != null) {
                responseCode = Response.Status.OK.getStatusCode(); // 200 OK
                output = convertToApiString(result);
            } else {
                throw new OctopusDAOException("INVALID COUNTRY NAME");
            }
        } catch (OctopusDAOException e) {
            output = e.getMessage();
        } catch (Exception e) {
            log.error("Exception", e);
            output = ApiSharedMethods.getErrorMessageString();
        }

        return Response.status(responseCode).entity(output).build();
    }

    @GET
    @Path("/countryCode")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCountryByCode(@QueryParam("code") String code) {
        log.info("GET COUNTRY BY CODE CALLED");
        ApiV1Country result;
        String output = "NOK";
        int responseCode = Response.Status.BAD_REQUEST.getStatusCode();

        try {
            result = countryDataManager.getCountryByCode(code);
            if (result != null) {
                responseCode = Response.Status.OK.getStatusCode();
                output = convertToApiString(result);
            } else {
                throw new OctopusDAOException("INVALID COUNTRY CODE");
            }
        } catch (OctopusDAOException e) {
            output = e.getMessage();
        } catch (Exception e) {
            log.error("Exception", e);
            output = ApiSharedMethods.getErrorMessageString();
        }

        return Response.status(responseCode).entity(output).build();
    }

    private String convertToApiString(Object object) {
        return gson.toJson(object);
    }
}