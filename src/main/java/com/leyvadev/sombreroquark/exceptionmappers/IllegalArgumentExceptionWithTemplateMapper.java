package com.leyvadev.sombreroquark.exceptionmappers;


import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class IllegalArgumentExceptionWithTemplateMapper implements ExceptionMapper<IllegalArgumentExceptionWithTemplate> {

    @Override
    public Response toResponse(IllegalArgumentExceptionWithTemplate ex) {
        String message = ex.getMessage();

        String html = "<html><head><title>Error</title></head><body><h1 style='text-align:center;'>Error: " + message + "</h1>";

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_HTML)
                .entity(html)
                .build();
    }
}