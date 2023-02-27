package com.leyvadev.sombreroquark.exceptionmappers;


import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException exception) {

        Map<String, Object> responseBody = Map.of(
                "code", Response.Status.BAD_REQUEST.getStatusCode(),
                "error", exception.getMessage()
        );
        return Response.status(Response.Status.BAD_REQUEST).entity(responseBody).build();
    }

}