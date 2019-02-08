package org.example.jerseyguice;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import static org.example.jerseyguice.JettyServer.log;

@Provider
public class MyFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private final MySingleton service;

    @Inject
    public MyFilter(MySingleton service) {
        this.service = service;
    }

    @Override
    public void filter(final ContainerRequestContext requestContext) {
        log("Request Filter: " + service.call());
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        log("Response filter: " + service.call());
    }
}
