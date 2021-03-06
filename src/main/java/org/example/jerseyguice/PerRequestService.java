package org.example.jerseyguice;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Demonstrates Guice servletmodule and Guice filter are in place
 */
public class PerRequestService {
    final HttpSession session;
    final HttpServletRequest request;

    @Inject
    public PerRequestService(HttpSession session, HttpServletRequest request) {
        this.session = session;
        this.request = request;
    }

    public String call() {
        return "PerRequestService:(" + hashCode() + ") - " + request.getRequestURI();
    }
}
