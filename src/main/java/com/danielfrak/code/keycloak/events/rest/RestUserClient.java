package com.danielfrak.code.keycloak.events.rest;

import com.danielfrak.code.keycloak.events.dtos.HybrisUser;
import com.danielfrak.code.keycloak.events.dtos.HybrisUserResponse;
import com.danielfrak.code.keycloak.providers.rest.rest.UserPasswordDto;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface RestUserClient {

    @POST
    @Path("/customer")
    Response addHybrisUser(HybrisUser hybrisUser);

    @PUT
    @Path("/customer")
    Response updateHybrisUser(HybrisUser hybrisUser);
}
