//package com.octopus.orm.api.vi.pojo;
//
//import com.octopus.orm.api.vi.pojo.CountryServiceImpl;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.io.InputStreamReader;
//import java.net.InetSocketAddress;
//
//import com.sun.net.httpserver.HttpServer;
//import com.sun.net.httpserver.HttpHandler;
//import com.sun.net.httpserver.HttpExchange;
//import com.google.gson.Gson;
//
//public class Main {
//    public static void main(String[] args) throws IOException {
//        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
//        server.createContext("/api/1.0/country/add", new AddCountryHandler());
//        server.createContext("/api/1.0/country/edit", new EditCountryHandler());
//        server.createContext("/api/1.0/country/name", new GetCountryByNameHandler());
//        server.createContext("/api/1.0/country/code", new GetCountryByCodeHandler());
//        server.setExecutor(null); // creates a default executor
//        server.start();
//        System.out.println("Server started on port 8000");
//    }
//}
//
//abstract class BaseHandler implements HttpHandler {
//    protected final CountryServiceImpl countryService = new CountryServiceImpl();
//    protected final Gson gson = new Gson();
//
//    protected ApiV1Country parseRequest(HttpExchange exchange) throws IOException {
//        return gson.fromJson(new InputStreamReader(exchange.getRequestBody()), ApiV1Country.class);
//    }
//
//    protected void sendResponse(HttpExchange exchange, int responseCode, String response) throws IOException {
//        exchange.sendResponseHeaders(responseCode, response.length());
//        OutputStream os = exchange.getResponseBody();
//        os.write(response.getBytes());
//        os.close();
//    }
//}
//
//class AddCountryHandler extends BaseHandler {
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        ApiV1Country country = parseRequest(exchange);
//        String response = countryService.addCountryRecord(country).toString();
//        sendResponse(exchange, 200, response);
//    }
//}
//
//class EditCountryHandler extends BaseHandler {
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        ApiV1Country country = parseRequest(exchange);
//        String response = countryService.editCountryRecord(country).toString();
//        sendResponse(exchange, 200, response);
//    }
//}
//
//class GetCountryByNameHandler extends BaseHandler {
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        String name = exchange.getRequestURI().getQuery().split("=")[1];
//        String response = countryService.getCountryByName(name).toString();
//        sendResponse(exchange, 200, response);
//    }
//}
//
//class GetCountryByCodeHandler extends BaseHandler {
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        String code = exchange.getRequestURI().getQuery().split("=")[1];
//        String response = countryService.getCountryByCode(code).toString();
//        sendResponse(exchange, 200, response);
//    }
//}
//
//


package com.octopus.orm.api.vi.pojo;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/api/1.0/country/add", new AddCountryHandler());
        server.createContext("/api/1.0/country/edit", new EditCountryHandler());
        server.createContext("/api/1.0/country/name", new GetCountryByNameHandler());
        server.createContext("/api/1.0/country/code", new GetCountryByCodeHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }
}

abstract class BaseHandler implements HttpHandler {
    protected final CountryServiceImpl countryService = new CountryServiceImpl();
    protected final Gson gson = new Gson();

    protected ApiV1Country parseRequest(HttpExchange exchange) throws IOException {
        return gson.fromJson(new InputStreamReader(exchange.getRequestBody()), ApiV1Country.class);
    }

    protected void sendResponse(HttpExchange exchange, int responseCode, String response) throws IOException {
        exchange.sendResponseHeaders(responseCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

class AddCountryHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ApiV1Country country = parseRequest(exchange);
        String response = countryService.addCountryRecord(country).toString();
        sendResponse(exchange, 200, response);
    }
}

class EditCountryHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ApiV1Country country = parseRequest(exchange);
        String response = countryService.editCountryRecord(country).toString();
        sendResponse(exchange, 200, response);
    }
}

class GetCountryByNameHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String name = exchange.getRequestURI().getQuery().split("=")[1];
        String response = countryService.getCountryByName(name).toString();
        sendResponse(exchange, 200, response);
    }
}

class GetCountryByCodeHandler extends BaseHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String code = exchange.getRequestURI().getQuery().split("=")[1];
        String response = countryService.getCountryByCode(code).toString();
        sendResponse(exchange, 200, response);
    }
}

