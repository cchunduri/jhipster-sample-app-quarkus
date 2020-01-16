package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BankAccount;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;


/**
 * Hibernate Panache repository for the BankAccount entity.
 */
@SuppressWarnings("unused")
@ApplicationScoped
public class BankAccountRepository implements PanacheRepository<BankAccount> {
    
}