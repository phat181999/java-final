package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserHappyPath(){
   // Arrange
    when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

    CreateUserRequest request = new CreateUserRequest();
    request.setUsername("test");
    request.setPassword("testPassword");
    request.setConfirmPassword("testPassword");

    // Act
    ResponseEntity<User> response = userController.createUser(request);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());

    User user = response.getBody();
    assertNotNull(user);
    assertEquals(0, user.getId());
    assertEquals("test", user.getUsername());
    assertEquals("thisIsHashed", user.getPassword());

    // Additional conditions
    // Mismatched passwords
    CreateUserRequest mismatchedPasswordsRequest = new CreateUserRequest();
    mismatchedPasswordsRequest.setUsername("testMismatched");
    mismatchedPasswordsRequest.setPassword("password1");
    mismatchedPasswordsRequest.setConfirmPassword("password2");

    ResponseEntity<User> mismatchedPasswordsResponse = userController.createUser(mismatchedPasswordsRequest);
    assertEquals(400, mismatchedPasswordsResponse.getStatusCodeValue());
    assertNull(mismatchedPasswordsResponse.getBody());

    // Existing username
    CreateUserRequest existingUsernameRequest = new CreateUserRequest();
    existingUsernameRequest.setUsername("test");
    existingUsernameRequest.setPassword("newPassword");
    existingUsernameRequest.setConfirmPassword("newPassword");

    ResponseEntity<User> existingUsernameResponse = userController.createUser(existingUsernameRequest);
    assertEquals(400, existingUsernameResponse.getStatusCodeValue());
    assertNull(existingUsernameResponse.getBody());

    // Empty username
    CreateUserRequest emptyUsernameRequest = new CreateUserRequest();
    emptyUsernameRequest.setUsername("");
    emptyUsernameRequest.setPassword("password");
    emptyUsernameRequest.setConfirmPassword("password");

    ResponseEntity<User> emptyUsernameResponse = userController.createUser(emptyUsernameRequest);
    assertEquals(400, emptyUsernameResponse.getStatusCodeValue());
    assertNull(emptyUsernameResponse.getBody());

    // Empty password
    CreateUserRequest emptyPasswordRequest = new CreateUserRequest();
    emptyPasswordRequest.setUsername("newUser");
    emptyPasswordRequest.setPassword("");
    emptyPasswordRequest.setConfirmPassword("");

    ResponseEntity<User> emptyPasswordResponse = userController.createUser(emptyPasswordRequest);
    assertEquals(400, emptyPasswordResponse.getStatusCodeValue());
    assertNull(emptyPasswordResponse.getBody());

    }

   
}
