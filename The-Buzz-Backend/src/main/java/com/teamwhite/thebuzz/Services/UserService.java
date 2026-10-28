package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Contexts.BuzzSessionService;
import com.teamwhite.thebuzz.Exceptions.NoUserProvidedException;
import com.teamwhite.thebuzz.Repositories.UserRepository;
import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service for handling all User tasks
 */
@Service
public class UserService {
    // Repository for all user records
    private final UserRepository repository;
    // BCrypt Password Encoder
    private final PasswordEncoder passwordEncoder;
    // Session Service
//    private final BuzzSessionService sessionService;

    /**
     * For autowiring values
     * @param repository User repository
     * @param passwordEncoder BCrypt password encoder
     */
    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Gets a specific user with a given email
     * @param email String used for finding a specific table entry
     * @return BuzzUser user as represented in the database
     */
    public BuzzUser getUserRecordByEmail(String email) throws IllegalArgumentException{
        if(email == null) {
            throw new IllegalArgumentException("email provided is null");
        }
        BuzzUser user;
        /** Gets the values from the request in the following format:
         * List<T> Rows in the table, analagous to entries
         * Object[] Columns in the table, the values in each row
         */
        List<Object[]> returnedValues = repository.getUserRecordByEmail(email);

        // If no values were returned, no users exist with that email
        if(returnedValues == null) { //@TODO CHANGE EXCEPTION
            throw new RuntimeException("results are null when expected results at getUserRecordByEmail()");
        }
        // If more than one record was returned, we can't know which user they are referring to
        // This also indicates a database failure, as schema denotes unique emails
        if(returnedValues.size() > 1) { //@TODO CHANGE EXCEPTION
            throw new RuntimeException("results are more than one");
        }
        if(returnedValues.isEmpty()) { //@TODO CHANGE EXCEPTION
            throw new RuntimeException("results are empty");
        }

        // Magic number, but List<T> does not have getFirst() method, this recreates that result
        Object[] objFromQuery = returnedValues.get(0);
        /**
         * This looks disgusting, but is the most efficient way to map them explicitly, as the
         * deserializer cannot map Timestamp, JsonObject, or UUID values automatically
         */
        user = new BuzzUser( (String) objFromQuery[0], (String) objFromQuery[1],
                (String) objFromQuery[2], (String) objFromQuery[3],
                Timestamp.valueOf((LocalDateTime) objFromQuery[4]), (String) objFromQuery[5],
                (String) objFromQuery[6], (String) objFromQuery[7], (String) objFromQuery[8],
                (String) objFromQuery[9], (String) objFromQuery[10], (String) objFromQuery[11],
                (String) objFromQuery[12]);
        return user;
    }

    /**
     * Gets all users as a list of users
     * @return List<BuzzUser> List of users
     */
    public List<BuzzUser> getAllUsers() {
        List<BuzzUser> list = new ArrayList<BuzzUser>();
        List<Object[]> dbResults = repository.getAllUsers();

        for(int i = 0; i < dbResults.size(); i++) {
            Object[] objFromQuery = dbResults.get(i);
            /**
             * This looks disgusting, but is the most efficient way to map them explicitly, as the
             * deserializer cannot map Timestamp, JsonObject, or UUID values automatically
             */
            list.add(new BuzzUser((String) objFromQuery[0], (String) objFromQuery[1],
                    (String) objFromQuery[2], (String) objFromQuery[3],
                    Timestamp.valueOf((LocalDateTime) objFromQuery[4]), (String) objFromQuery[5],
                    (String) objFromQuery[6], (String) objFromQuery[7], (String) objFromQuery[8],
                    (String) objFromQuery[9], (String) objFromQuery[10], (String) objFromQuery[11],
                    (String) objFromQuery[12]));
        }
        return list;
    }

    /**
     * Creates the database entry for a user based on a populated BuzzUser object
     * @param user BuzzUser object to place in the database
     * @throws NoUserProvidedException If the user provided is null
     */
    public void createUser(BuzzUser user) throws NoUserProvidedException {
        if(user == null) {
            throw new NoUserProvidedException("Expected BuzzUser, received: null");
        }
        // Required to catch any SQLExceptions, as they will silently fail otherwise
        try {
            repository.createUser(user.getUserId(), user.getUsername(), user.getPasswordHash(),
                    user.getOauthKey(), user.getJoinDate(), user.getUserDesc(), user.getFullName(),
                    user.getUserPfpLocation(), user.getEmail(), user.getFavorites().toString().trim(),
                    user.getVolumeSettings().toString().trim(), user.getNotificationSettings().toString().trim(),
                    user.getPermissions().asJsonObject().toString().trim());
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }

    /**
     * Creates a database entry with a user generated with a username, email, and password to hash
     * @param username String username
     * @param email String email, MUST NOT EXIST IN DATABASE
     * @param unHashedPassword String unhashed password, hashing happens in BuzzUser constructor
     * @throws IllegalArgumentException If any fields are null
     */
    public void createUser(String username, String email, String unHashedPassword) throws IllegalArgumentException {
        if(username == null) {
            throw new IllegalArgumentException("username provided is null");
        }
        if(unHashedPassword == null) {
            throw new IllegalArgumentException("unHashedPassword provided is null");
        }
        if(email == null) {
            throw new IllegalArgumentException("Email provided is null");
        }

        BuzzUser user = new BuzzUser(username, passwordEncoder.encode(unHashedPassword), email);

        // Required to catch any SQLExceptions, as they will silently fail otherwise
        try {
            repository.createUser(user.getUserId(), user.getUsername(), user.getPasswordHash(),
                    user.getOauthKey(), user.getJoinDate(), user.getUserDesc(), user.getFullName(),
                    user.getUserPfpLocation(), user.getEmail(), user.getFavorites().toString().trim(),
                    user.getVolumeSettings().toString().trim(), user.getNotificationSettings().toString().trim(),
                    user.getPermissions().asJsonObject().toString().trim());
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }

    public void modifyUser(String userId, String email, String username, String fullName, String userDesc) throws NoUserProvidedException {
        // @TODO BATCH INTO GROUPS TO NOT POTENTIALLY CALL DB A GAZILLION TIMES

        try {
            if (!email.isEmpty()) {
                Logger.getGlobal().info("Email isn't empty");
                repository.updateUserEmailByUserId(userId, email);
            }

            if (!username.isEmpty()) {
                Logger.getGlobal().info("username isn't empty");
                repository.updateUserUsernameByUserId(userId, username);
            }

            if (!fullName.isEmpty()) {
                repository.updateUserFullNameByUserId(userId, fullName);
            }

            if (!userDesc.isEmpty()) {
                repository.updateUserDescByUserId(userId, userDesc);
            }
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }

    /**
     * Deletes a user entry in the database permanently
     * @param email String used for finding a specific table entry
     * @throws IllegalArgumentException
     */
    public void deleteUser(String email) throws IllegalArgumentException {
        if(email == null) {
            throw new IllegalArgumentException("Email provided is null");
        }

        try {
            repository.removeUserByEmail(email);
        } catch(Exception e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }

    /**
     * Update a users stored password
     * @param email String used for finding a specific table entry
     * @param oldPasswordHash String old password hash to verify upon request
     * @param newPasswordHash String new password hash to replace old password hash
     * @throws IllegalArgumentException If any field is null
     */
    public void updateUserPasswordHash(String email, String oldPasswordHash, String newPasswordHash) throws IllegalArgumentException {
        if(email == null) {
            throw new IllegalArgumentException("Email provided is null");
        }
        if(oldPasswordHash == null) {
            throw new IllegalArgumentException("oldPasswordHash provided is null");
        }
        if(newPasswordHash == null) {
            throw new IllegalArgumentException("newPasswordHash provided is null");
        }

        try {
            repository.updateUserPasswordHashByEmail(email, newPasswordHash);
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }

    /**
     * Checks to see if an email is present in the database
     * @param email String used for finding a specific table entry
     * @return boolean If the email exists in the database
     * @throws IllegalArgumentException If the email is null
     */
    public boolean doesUserExist(String email) throws IllegalArgumentException {
        if(email == null) {
            throw new IllegalArgumentException("Email provided is null");
        }

        try {
            List<Object[]> results = repository.doesUserExist(email);

            // results will be empty if no entries can be found by the database
            return (!results.isEmpty());
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
        }
        // For now, force any errors to tell the system that an email exists
        return true;
    }
}
