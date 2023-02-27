package com.leyvadev.sombreroquark.resources;

import com.leyvadev.sombreroquark.utils.JwtUtils;
import org.jose4j.lang.JoseException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Base64;

@Path("/hello")
public class GreetingResource {
    @Inject
    JwtUtils jwtUtils;
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws JoseException {
return "Hello RESTEasy";
    }
}