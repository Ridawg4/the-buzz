package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.UserRepository;
import com.teamwhite.thebuzz.model.BuzzUser;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class BuzzUserDetailsManager implements UserDetailsManager, UserDetailsService {
    private UserService userService;

    public BuzzUserDetailsManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    /**
     * MUST BE EMAIL PER OUR SCHEMA, METHOD CANNOT BE CHANGED, OVERRIDEN METHOD
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails details = userService.getUserRecordByEmail(username);
        return details;
    }

    @Override
    public void createUser(UserDetails user) {
        userService.createUser((BuzzUser) user);
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String email) {
        userService.deleteUser(email);
    }

    @Override
    public void changePassword(@Nullable String oldPassword, @Nullable String newPassword) {
//        userService.updateUserPasswordHash( );
    }

    @Override
    public boolean userExists(String email) {
        return userService.doesUserExist(email);
    }
}
