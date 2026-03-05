package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserService service;

    @BeforeEach
    void setup() {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        service = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Registration Successful")
    void registerNewUser() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Michael", "password", "Michael@gmail.com");
        RegisterResult result = service.register(request);
        assertNotNull(result.authToken());
        assertEquals("Michael", result.username());
    }

    @Test
    @DisplayName("Registration - User Already Exists")
    void registerFail() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("Michael", "password", "Michael@gmail.com");
        service.register(request);
        assertThrows(DataAccessException.class, () -> service.register(request));
    }
}