package com.example.pocketbook;

import com.example.pocketbook.model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Contains user credentials such as username, first/last name and email address.
 * This class is used to intialize the instane of a user and pass information to activites/fragments.
 */
public class UserTest {
    private ArrayList<User> userList = new ArrayList<User>();

    @Test
    public void setNameTest() {
        User user = new User("Rebecca","Kay","taylorsmith@gmail.com","TaylorSmith","taylor123",null);
        assertEquals(user.getFirstName(), "Rebecca");
        assertEquals(user.getLastName(), "Kay");
    }

    @Test
    public void setUsernameTest() {
        User user = new User("Taylor","Smith","taylorsmith@gmail.com","Taytay","taylor123", null);
        assertEquals(user.getUsername(), "Taytay");
    }

    @Test
    public void setPasswordTest() {
        User user = new User("Taylor","Smith","taylorsmith@gmail.com","TaylorSmith","taylor123", null);
        assertEquals(user.getPassword(), "taylor123");
    }

    @Test
    public void setPictureTest() {
        User user = new User("Taylor","Smith","taylorsmith@gmail.com","TaylorSmith","taylor123",null);
        String picture = "taylor.jpeg";
        user.setPhoto(picture);
        assertEquals(user.getPhoto(), "taylor.jpeg");
    }


    @Test
    public void constructorTest(){
        User user = new User("Taylor","Smith","taylorsmith@gmail.com","TaylorSmith","taylor123",null);
        assertEquals(user.getFirstName(), "Taylor");
        assertEquals(user.getLastName(), "Smith");
        assertEquals(user.getUsername(), "TaylorSmith");
        assertEquals(user.getPhoto(), "");
    }

    @Test
    public void constructorTestWithPhoto(){
        User user = new User("Taylor","Smith","taylorsmith@gmail.com","TaylorSmith","taylor123","TaylorSmith.jpg");
        assertEquals(user.getFirstName(), "Taylor");
        assertEquals(user.getLastName(), "Smith");
        assertEquals(user.getUsername(), "TaylorSmith");
        assertEquals(user.getPhoto(), "TaylorSmith.jpg");
    }

    @Test
    public void editProfileTest() {
        User user = new User("Mario","Luigi","supersmash@gmail.com","supersmash","kart101",null);
        userList.add(user);
        User updatedUserInfo = new User("Luigi","Mario","supersmash@bros.com","supersmashbros","kart101",null);
        userList.set(0,updatedUserInfo);
        assertTrue(userList.contains(updatedUserInfo));
    }

    @Test
    public void editProfileTestWithPhoto() {
        User user = new User("Mario","Luigi","supermario@gmail.com","supermario","kart101",null);
        userList.add(user);
        User updatedUserInfo = new User("Luigi","Mario","supersmash@bros.com","supersmashbros","kart#34101","supersmash.jpeg");
        userList.set(0,updatedUserInfo);
        assertTrue(userList.contains(updatedUserInfo));
    }

    @Test
    public void editProfileTestWithoutPhoto() {
        User user = new User("Mario","Luigi","supersmash@hotmail.com","supersmash","kart101","supermario.jpeg");
        userList.add(user);
        User updatedUserInfo = new User("Luigi","Mario","supersmash@pocketbook.com","supersmashbrospocketbook","kart@12101",null);
        userList.set(0,updatedUserInfo);
        assertTrue(userList.contains(updatedUserInfo));
    }
}
