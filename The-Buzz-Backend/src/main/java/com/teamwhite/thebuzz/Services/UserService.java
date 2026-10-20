package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Exceptions.NoUserProvidedException;
import com.teamwhite.thebuzz.Repositories.UserRepository;
import com.teamwhite.thebuzz.model.BuzzUser;
import jakarta.annotation.Nonnull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public BuzzUser getUserRecordByEmail(String email) {
        BuzzUser user = null;
        try {
            List<Object[]> returnedValues = repository.getUserRecordByEmail(email);
            if(returnedValues == null) {
                throw new RuntimeException("results are null from getUserRecordByEmail");
            }
            if(returnedValues.size() > 1) {
                throw new RuntimeException("results are more than one");
            }
            // @TODO ERROR STRING IS INCORRECT
            Object[] objFromQuery = returnedValues.get(0);
            user = new BuzzUser( (String) objFromQuery[0], (String) objFromQuery[1],
                    (String) objFromQuery[2], (String) objFromQuery[3], Timestamp.valueOf((LocalDateTime) objFromQuery[4]),
                    (String) objFromQuery[5], (String) objFromQuery[6], (String) objFromQuery[7],
                    (String) objFromQuery[8], (String) objFromQuery[9], (String) objFromQuery[10],
                    (String) objFromQuery[11], (String) objFromQuery[12]);

        } catch(Exception e) {
            throw new RuntimeException("Error handling ResultSet " + e.getMessage());
        }
        return user;
    }

    public List<BuzzUser> getAllUsers() {
        List<BuzzUser> list = new ArrayList<BuzzUser>();

        try {
            ResultSet results = repository.getAllUsers();
            if(results == null) {
                throw new RuntimeException("results are null from getUserRecordByEmail");
            }

            while(results.next()) {
                list.add(new BuzzUser(results.getString(1), results.getString(2),
                        results.getString(3), results.getString(4),
                        results.getTimestamp(5), results.getString(6),
                        results.getString(7), results.getString(8),
                        results.getString(9), results.getString(10),
                        results.getString(11), results.getString(12),
                        results.getString(13)));
            }

        } catch(Exception e) {
            throw new RuntimeException("Error handling ResultSet " + e.getMessage());
        }

        return list;
    }

    public void createUser(@Nonnull BuzzUser user) throws NoUserProvidedException {
        repository.createUser(user.getUserId(), user.getUsername(), user.getPasswordHash(),
                user.getOauthKey(), user.getJoinDate(), user.getUserDesc(), user.getFullName(),
                user.getUserPfpLocation(), user.getEmail(), user.getFavorites().toString(),
                user.getVolumeSettings().toString(), user.getNotificationSettings().toString(),
                user.getPermissions().asJsonObject().toString());
    }

    public void createUser(@Nonnull String username, @Nonnull String email, @Nonnull String unHashedPassword) throws NoUserProvidedException {
        BuzzUser user = new BuzzUser(username, passwordEncoder.encode(unHashedPassword), email);

        try {
            repository.createUser(user.getUserId(), user.getUsername(), user.getPasswordHash(),
                    user.getOauthKey(), user.getJoinDate(), user.getUserDesc(), user.getFullName(),
                    user.getUserPfpLocation(), user.getEmail(), user.getFavorites().toString(),
                    user.getVolumeSettings().toString(), user.getNotificationSettings().toString(),
                    user.getPermissions().asJsonObject().toString());
        } catch (Exception e) {
            Logger.getGlobal().warning(e.getMessage());
        }
    }

    // @TODO IMPLEMENT
    public void updateUser(BuzzUser user) throws NoUserProvidedException {
//        if(user == null) {
//            throw new NoUserProvidedException("User provided in UserService.createUser() is null");
//        }

//        repository.createUser(user.getUserId(), user.getUsername(), user.getPasswordHash(),
//                user.getOauthKey(), user.getJoinDate(), user.getUserDesc(), user.getFullName(),
//                user.getUserPfpLocation(), user.getEmail(), user.getFavorites(), user.getVolumeSettings(),
//                user.getNotificationSettings(), user.getPermissionData());
    }

    public void deleteUser(String email) throws NoUserProvidedException {
        if(email == null) {
            throw new NoUserProvidedException("User provided in UserService.createUser() is null");
        }

        repository.removeUserByEmail(email);
    }

    // @TODO IMPLEMENT
    public void updateUserPasswordHash(String email, String oldPasswordHash, String newPasswordHash) throws NoUserProvidedException {
        if(email == null) {
            throw new NoUserProvidedException("User provided in UserService.createUser() is null");
        }

        repository.updateUserPasswordHashByEmail(email, newPasswordHash);
    }

    public boolean doesUserExist(String email) throws NoUserProvidedException {
        int resultCounter = 0;

        if(email == null) {
            throw new NoUserProvidedException("User provided in UserService.createUser() is null");
        }

        ResultSet results = repository.doesUserExist(email);

        if(results == null) {
            return false;
        }
        return true;
    }
}
