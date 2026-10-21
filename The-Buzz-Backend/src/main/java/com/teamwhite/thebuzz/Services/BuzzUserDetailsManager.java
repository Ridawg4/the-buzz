package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.model.BuzzUser;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class BuzzUserDetailsManager implements UserDetailsManager, UserDetailsService {
    private UserService userService;

    /**
     * Autowires the UserService class to the UserDetailsManager
     * @param userService
     */
    public BuzzUserDetailsManager(UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads a UserDetails object from the database (BuzzUser object)
     * @implNote MUST BE EMAIL PER OUR SCHEMA, METHOD CANNOT BE CHANGED, OVERRIDEN METHOD
     *
     * @param username EMAIL MUST BE USED INSTEAD OF USERNAME
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails details = userService.getUserRecordByEmail(username);
        // Denoted that no null value gets received, must throw UsernameNotFoundException instead
        if(details == null) {
            throw new UsernameNotFoundException("Email not found in database for email " + username);
        }
        return details;
    }

    /**
     * Creates a user with an existing BuzzUser object
     * @param user BuzzUser populated with values
     */
    @Override
    public void createUser(UserDetails user) {
        userService.createUser((BuzzUser) user);
    }

    /**
     * Updates a user with an existing BuzzUser object
     * @param user BuzzUser populated with values
     */
    @Override
    public void updateUser(UserDetails user) {

    }

    /**
     * Deletes a BuzzUser with a given email
     * @param email String used for finding a specific table entry
     */
    @Override
    public void deleteUser(String email) {
        userService.deleteUser(email);
    }

    /**
     * Changes the password of a given user
     * @param oldPassword current password (for re-authentication if required)
     * @param newPassword the password to change to
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
//        userService.updateUserPasswordHash( );
    }

    /**
     * Checks if a user exists with a given email
     * @param email String used for finding a specific table entry
     * @return
     */
    @Override
    public boolean userExists(String email) {
        return userService.doesUserExist(email);
    }
}
