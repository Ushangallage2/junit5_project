package com.octopus.orm.api.vi.pojo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.octopus.orm.api.vi.service.impl.OctopusDAOException;
import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import javax.ws.rs.core.Response;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/1.0/country/addCountry", new AddCountryHandler());
        server.createContext("/api/1.0/country/editCountry", new EditCountryHandler());
        server.createContext("/api/1.0/country/countryName", new GetCountryByNameHandler());
        server.createContext("/api/1.0/country/countryCode", new GetCountryByCodeHandler());
        server.createContext("/api/1.0/country/list", new GetAllCountriesHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }
}

abstract class BaseHandler implements HttpHandler {
    protected final CountryServiceImpl countryService = new CountryServiceImpl();
    protected final Gson gson = new Gson();

    protected static final String SUCCESS_STATUS = "success";
    protected static final String FAILURE_STATUS = "failure";

    protected ApiV1Country parseRequest(HttpExchange exchange) throws IOException {
        return gson.fromJson(new InputStreamReader(exchange.getRequestBody()), ApiV1Country.class);
    }

    protected String createResponse(String status, String message, Object data) {
        HashMap<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status);
        responseMap.put("message", message);
        responseMap.put("data", data);
        return gson.toJson(responseMap);
    }

    protected void sendResponse(HttpExchange exchange, int responseCode, String response) throws IOException {
        exchange.sendResponseHeaders(responseCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        } // Automatically close OutputStream
    }
}

class AddCountryHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            ApiV1Country country = parseRequest(exchange);
            Response serviceResponse = countryService.addCountryRecord(country);
            String responseBody = (String) serviceResponse.getEntity(); // Extract the response body as a String
            int responseCode = serviceResponse.getStatus(); // Get the response status

            sendResponse(exchange, responseCode, responseBody); // Send back the response
        } catch (Exception e) {
            sendResponse(exchange, 500, createResponse(FAILURE_STATUS, "Failed to add country: " + e.getMessage(), null));
        }
    }
}


//class EditCountryHandler extends BaseHandler {
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        try {
//            ApiV1Country country = parseRequest(exchange);
//            String response = countryService.editCountryRecord(country).toString();
//            sendResponse(exchange, 200, createResponse(SUCCESS_STATUS, "Country edited successfully", response));
//        } catch (Exception e) {
//            sendResponse(exchange, 500, createResponse(FAILURE_STATUS, "Failed to edit country: " + e.getMessage(), null));
//        }
//    }
//}

class EditCountryHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            ApiV1Country country = parseRequest(exchange);
            Response serviceResponse = countryService.editCountryRecord(country);

            // Extract the response entity and status code from the Response object
            String responseBody = (String) serviceResponse.getEntity();
            int responseCode = serviceResponse.getStatus();

            // Send the response back to the client
            sendResponse(exchange, responseCode, responseBody);
        } catch (Exception e) {
            sendResponse(exchange, 500, createResponse(FAILURE_STATUS, "Failed to edit country: " + e.getMessage(), null));
        }
    }
}


class GetCountryByCodeHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String code = exchange.getRequestURI().getQuery().split("=")[1];
        try {
            Response serviceResponse = countryService.getCountryByCode(code);
            // Extract the entity directly from the Response object
            String responseBody = (String) serviceResponse.getEntity();
            int responseCode = serviceResponse.getStatus();
            sendResponse(exchange, responseCode, responseBody);
        } catch (Exception e) {
            sendResponse(exchange, 500, createResponse(FAILURE_STATUS, "Failed to get country by code: " + e.getMessage(), null));
        }
    }
}

class GetCountryByNameHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String name = exchange.getRequestURI().getQuery().split("=")[1];
        try {
            Response serviceResponse = countryService.getCountryByName(name);
            // Extract the entity directly from the Response object
            String responseBody = (String) serviceResponse.getEntity();
            int responseCode = serviceResponse.getStatus();
            sendResponse(exchange, responseCode, responseBody);
        } catch (Exception e) {
            sendResponse(exchange, 500, createResponse(FAILURE_STATUS, "Failed to get country by name: " + e.getMessage(), null));
        }
    }
}

// Assuming GetAllCountriesHandler remains the same as previously provided.
class GetAllCountriesHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            Response serviceResponse = countryService.getAllCountryRecords();
            int responseCode = serviceResponse.getStatus();
            String responseBody = serviceResponse.getEntity().toString();
            sendResponse(exchange, responseCode, responseBody);
        } catch (Exception e) {
            sendResponse(exchange, 500, createResponse(FAILURE_STATUS, "Internal Server Error", null));
        }
    }
}