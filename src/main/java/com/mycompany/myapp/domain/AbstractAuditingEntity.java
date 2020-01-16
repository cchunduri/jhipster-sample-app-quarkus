package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@MappedSuperclass
//@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity implements Serializable {

    public static final long serialVersionUID = 1L;

//    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    @JsonIgnore
    public String createdBy;

//    @CreatedDate
    @Column(name = "created_date", updatable = false)
    @JsonIgnore
    public Instant createdDate = Instant.now();

//    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    @JsonIgnore
    public String lastModifiedBy;

//    @LastModifiedDate
    @Column(name = "last_modified_date")
    @JsonIgnore
    public Instant lastModifiedDate = Instant.now();

}