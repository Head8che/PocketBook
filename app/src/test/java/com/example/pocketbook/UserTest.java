package com.example.pocketbook;

import com.example.pocketbook.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNull;


public class UserTest {

    /**
     * creates an invalid instance of the User class
     * @return invalid User object
     */
    private User mockInvalidUser() {
        String firstName = "";  // invalid firstName
        String lastName = "mockLastName";
        String email = "mock@mock.com";
        String username = "mockUsername";
        String password = "mockPassword";
        String photo = "";

        return new User(firstName, lastName, email, username, password, photo);
    }

    /**
     * creates a valid instance of the User class
     * @return valid User object
     */
    private User mockValidUser() {
        String firstName = "mockFirstName";  // valid firstName
        String lastName = "mockLastName";
        String email = "mock@mock.com";
        String username = "mockUsername";
        String password = "mockPassword";
        String photo = "";

        return new User(firstName, lastName, email, username, password, photo);
    }

    /**
     * tests the getters of the User class
     * getters should return a valid value if the User is valid and null otherwise
     */
    @Test
    void testGetters() {
        User user;

        user = mockInvalidUser();

        // all getters should return null with invalid User
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getPhoto());

        user = mockValidUser();

        // all getters should be non-null with a valid User
        Assertions.assertEquals("mockFirstName", user.getFirstName());
        Assertions.assertEquals("mockLastName", user.getLastName());
        Assertions.assertEquals("mock@mock.com", user.getEmail());
        Assertions.assertEquals("mockUsername", user.getUsername());
        Assertions.assertEquals("mockPassword", user.getPassword());
        Assertions.assertEquals("", user.getPhoto());
    }
}
