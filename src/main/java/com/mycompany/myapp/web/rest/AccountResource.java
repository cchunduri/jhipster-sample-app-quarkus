package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.UserService;
import com.mycompany.myapp.service.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

/**
 * REST controller for managing the current user's account.
 */
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    UserService userService;

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param ctx the request security context.
     * @return the login if the user is authenticated.
     */
    @GET
    @Path("/authenticate")
    public String isAuthenticated(@Context SecurityContext ctx) {
        log.debug("REST request to check if the current user is authenticated");
        return ctx.getUserPrincipal().getName();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GET
    @Path("/account")
    @PermitAll
    public UserDTO getAccount(@Context SecurityContext ctx) {
        return userService.getUserWithAuthoritiesByLogin(ctx.getUserPrincipal().getName())
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

}