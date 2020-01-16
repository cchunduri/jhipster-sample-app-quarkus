package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.BankAccount;
import com.mycompany.myapp.repository.BankAccountRepository;
//import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.util.HeaderUtil;
import com.mycompany.myapp.web.util.ResponseUtil;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;     
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.BankAccount}.
 */
@Path("/api/bank-accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
@Transactional
public class BankAccountResource {

    private final Logger log = LoggerFactory.getLogger(BankAccountResource.class);

    private static final String ENTITY_NAME = "bankAccount";

    @ConfigProperty(name = "application.name")
    String applicationName;

    @Context
    HttpHeaders headers;

    @Inject
    BankAccountRepository bankAccountRepository;    

    /**
     * {@code POST  /bank-accounts} : Create a new bankAccount.
     *
     * @param bankAccount the bankAccount to create.
     * @return the {@link Response} with status {@code 201 (Created)} and with body the new bankAccount, or with status {@code 400 (Bad Request)} if the bankAccount has already an ID.
     */
    @POST
    public Response createBankAccount(@Valid BankAccount bankAccount, @Context UriInfo uriInfo) {
        log.debug("REST request to save BankAccount : {}", bankAccount);
        if (bankAccount.id != null) {
//            throw new BadRequestAlertException("A new bankAccount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        bankAccountRepository.persist(bankAccount);
        headers.getRequestHeaders().putAll(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, bankAccount.id.toString()));
        URI uri = uriInfo.getAbsolutePathBuilder().path(bankAccount.id.toString()).build();
        return Response.created(uri).entity(bankAccount).build();
    }

    /**
     * {@code PUT  /bank-accounts} : Updates an existing bankAccount.
     *
     * @param bankAccount the bankAccount to update.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the updated bankAccount,
     * or with status {@code 400 (Bad Request)} if the bankAccount is not valid,
     * or with status {@code 500 (Internal Server Error)} if the bankAccount couldn't be updated.
     */
    @PUT    
    public Response updateBankAccount(@Valid BankAccount bankAccount) {
        log.debug("REST request to update BankAccount : {}", bankAccount);
        if (bankAccount.id == null) {
//            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        bankAccountRepository.persist(bankAccount);
        headers.getRequestHeaders().putAll(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bankAccount.id.toString()));
        return Response.ok().entity(bankAccount).build();

    }

    /**
     * {@code GET  /bank-accounts} : get all the bankAccounts.
     *
     * @return the {@link Response} with status {@code 200 (OK)} and the list of bankAccounts in body.
     */
    @GET
    public List<BankAccount> getAllBankAccounts() {    
        log.debug("REST request to get all BankAccounts");
        return bankAccountRepository.findAll().list();
    }

    /**
     * {@code GET  /bank-accounts/:id} : get the "id" bankAccount.
     *
     * @param id the id of the bankAccount to retrieve.
     * @return the {@link Response} with status {@code 200 (OK)} and with body the bankAccount, or with status {@code 404 (Not Found)}.
     */
    @GET
    @Path("/{id}")
    public Response getBankAccount(@PathParam("id") Long id) {
        log.debug("REST request to get BankAccount : {}", id);
        Optional<BankAccount> bankAccount = bankAccountRepository.findByIdOptional(id);                
        return ResponseUtil.wrapOrNotFound(bankAccount);
    }

    /**
     * {@code DELETE  /bank-accounts/:id} : delete the "id" bankAccount.
     *
     * @param id the id of the bankAccount to delete.
     * @return the {@link Response} with status {@code 204 (NO_CONTENT)}.
     */
    @DELETE
    @Path("/{id}")
    public Response deleteBankAccount(@PathParam("id") Long id) {
        log.debug("REST request to delete BankAccount : {}", id);
        bankAccountRepository.findByIdOptional(id).ifPresent(bankAccount -> {
            bankAccountRepository.delete(bankAccount);
        });
        headers.getRequestHeaders().putAll(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()));
        return Response.noContent().build();   
    }     
}