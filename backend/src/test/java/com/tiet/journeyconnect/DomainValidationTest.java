package com.tiet.journeyconnect;

import com.tiet.journeyconnect.dto.AuthDtos.LoginRequest;
import com.tiet.journeyconnect.exceptions.InvalidCampusDomainException;
import com.tiet.journeyconnect.services.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Test Suite for @thapar.edu institutional email validation.
 */
@SpringBootTest
public class DomainValidationTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Domain Validation: Non-thapar email (@gmail.com) is rejected with InvalidCampusDomainException")
    void testNonThaparEmailRejected() {
        LoginRequest req = new LoginRequest();
        req.setEmail("outsider@gmail.com");
        req.setPassword("Password@123");

        MockHttpServletResponse res = new MockHttpServletResponse();
        assertThrows(InvalidCampusDomainException.class, () -> authService.login(req, res),
                "Personal @gmail.com accounts must be rejected with InvalidCampusDomainException");
    }

    @Test
    @DisplayName("Domain Validation: Non-thapar email (@yahoo.com) is rejected with InvalidCampusDomainException")
    void testYahooEmailRejected() {
        LoginRequest req = new LoginRequest();
        req.setEmail("intruder@yahoo.com");
        req.setPassword("Password@123");

        MockHttpServletResponse res = new MockHttpServletResponse();
        assertThrows(InvalidCampusDomainException.class, () -> authService.login(req, res),
                "@yahoo.com accounts must be rejected");
    }

    @Test
    @DisplayName("Domain Validation: Valid official @thapar.edu email is permitted through domain check")
    void testValidThaparEmailAllowed() {
        LoginRequest req = new LoginRequest();
        req.setEmail("student1@thapar.edu");
        req.setPassword("Thapar@123");

        MockHttpServletResponse res = new MockHttpServletResponse();
        var result = authService.login(req, res);
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("token"));
    }
}
