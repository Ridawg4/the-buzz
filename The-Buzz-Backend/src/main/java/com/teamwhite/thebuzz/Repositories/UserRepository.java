package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    @Query(value = "SELECT * FROM development.users WHERE username = ?1", nativeQuery = true)
    User getUserRecordByUsername(String username);
}
