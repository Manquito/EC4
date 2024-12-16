package com.idat.restserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.idat.restserver.entity.Country;
import com.idat.restserver.repository.CountryRepository;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Path("/countries")
public class CountryController {
    @Autowired
    private CountryRepository countryRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCountries() {
        try {
            List<Country> countries = countryRepository.findAll();
            String json = objectMapper.writeValueAsString(countries);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al convertir a JSON")
                    .build();
        }
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCountryByName(@PathParam("name") String name) {
        Country country = countryRepository.findByName(name);
        if (country != null) {
            try {
                String json = objectMapper.writeValueAsString(country);
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            } catch (JsonProcessingException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Error al convertir a JSON")
                        .build();
            }
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"País no encontrado\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @PUT
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCountry(@PathParam("name") String name, String json) {
        try {
            Country updateCountry = objectMapper.readValue(json, Country.class);
            Country country = countryRepository.findByName(name);
            if (country != null) {
                country.setName(updateCountry.getName());
                country.setContinent(updateCountry.getContinent());
                country.setLanguage(updateCountry.getLanguage());
                countryRepository.save(country);
                String responseMessage = "{\"message\":\"País actualizado correctamente\"}";
                return Response.status(Response.Status.OK)
                        .entity(responseMessage)
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            return Response.status(Response.Status.NOT_FOUND).entity("País no encontrado").build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al procesar la solicitud")
                    .build();
        }
    }

    @DELETE
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCountry(@PathParam("name") String name) {
        Country country = countryRepository.findByName(name);
        if (country != null) {
            countryRepository.delete(country);
            String responseMessage = "{\"message\":\"País eliminado correctamente\"}";
            return Response.status(Response.Status.NO_CONTENT)
                    .entity(responseMessage)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"message\":\"País no encontrado\"}")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCountry(String json) {
        try {
            Country newCountry = objectMapper.readValue(json, Country.class);
            countryRepository.save(newCountry);
            String createdJson = objectMapper.writeValueAsString(newCountry);
            return Response.status(Response.Status.CREATED)
                    .entity(createdJson)
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (JsonProcessingException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error al procesar la solicitud")
                    .build();
        }
    }
}