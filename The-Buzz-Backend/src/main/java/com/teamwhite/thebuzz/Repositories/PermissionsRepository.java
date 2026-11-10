package com.teamwhite.thebuzz.Repositories;


import com.teamwhite.thebuzz.model.Permissions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface PermissionsRepository extends CrudRepository<Permissions, Integer> {

    @Query(value="SELECT permission_id, permissions_name, permission_desc, permissions, BIN_TO_UUID(created_by), created_at FROM permissions", nativeQuery = true)
    public List<Object[]> getAllPermissions();
}
