package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.BCryptPasswordHasher;
import com.mycompany.myapp.web.rest.vm.LoginVM;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.transaction.Transactional;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class TestUserJWTControllerIT {

    @Inject
    BCryptPasswordHasher passwordHasher;

    @Test
    public void testAuthorize() throws Exception {
        UserRepository userRepository = CDI.current().select(UserRepository.class).get();

        User user = new User();
        user.login = "user-jwt-controller";
        user.email = "user-jwt-controller@example.com";
        user.activated = true;
        user.password = passwordHasher.hash("test");
        user.createdBy = "test";
        user.createdDate = Instant.now();

        Panache.getTransactionManager().begin();
        userRepository.persistAndFlush(user);
        Panache.getTransactionManager().commit();

        LoginVM login = new LoginVM();
        login.username = "user-jwt-controller";
        login.password = "test";

        given()
            .body(login)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .when()
            .post("/api/authenticate")
            .then()
            .statusCode(200)
            .body("id_token", instanceOf(String.class))
            .body("id_token", notNullValue())
            .header(HttpHeaders.AUTHORIZATION, not(blankOrNullString()));
    }

    @Test
    public void testAuthorizeWithRememberMe() throws Exception {
        UserRepository userRepository = CDI.current().select(UserRepository.class).get();

        User user = new User();
        user.login = "user-jwt-controller-remember-me";
        user.email = "user-jwt-controller-remember-me@example.com";
        user.activated = true;
        user.password = passwordHasher.hash("test");
        user.createdBy = "test";
        user.createdDate = Instant.now();
        
        Panache.getTransactionManager().begin();
        userRepository.persistAndFlush(user);
        Panache.getTransactionManager().commit();

        LoginVM login = new LoginVM();
        login.username = "user-jwt-controller-remember-me";
        login.password = "test";
        login.rememberMe = true;

        given()
            .body(login)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .when()
            .post("/api/authenticate")
            .then()
            .statusCode(200)
            .body("id_token", instanceOf(String.class))
            .body("id_token", notNullValue())
            .header(HttpHeaders.AUTHORIZATION, not(blankOrNullString()));
    }

    @Test
    public void testAuthorizeFails() {
        LoginVM login = new LoginVM();
        login.username = "wrong-user";
        login.password = "wrong password";

        given()
            .body(login)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .when()
            .post("/api/authenticate")
            .then()
            .statusCode(401)
//            .body("id_token", nullValue())
            .header(HttpHeaders.AUTHORIZATION, nullValue());
    }
}
