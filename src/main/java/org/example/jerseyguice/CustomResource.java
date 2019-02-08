package org.example.jerseyguice;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/myendpoint")
public class CustomResource {

    private final MySingleton service;
    private final PerRequestService requestService;

    @Inject
    CustomResource(MySingleton service, PerRequestService perRequestService) {
        this.service = service;
        this.requestService = perRequestService;
    }

    @GET
    public String getit() {
        return "Success: " + service.call() + ", " + requestService.call();
    }

}
