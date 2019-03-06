package org.example.jerseyguice;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class MyFilter implements ContainerRequestFilter, ContainerResponseFilter {
    static final Logger LOGGER = Logger.getLogger(MyFilter.class.getName());

    private final MySingleton service;

    @Inject
    public MyFilter(MySingleton service) {
        this.service = service;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        LOGGER.info("Request Filter: " + service.call());
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        LOGGER.info("Response filter: " + service.call());
    }
}
