package com.teamwhite.thebuzz.Repositories;


import com.teamwhite.thebuzz.model.Permissions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PermissionsRepository extends CrudRepository<Permissions, Integer> {

    @Query(value="SELECT permission_id, permissions_name, permission_desc, permissions, BIN_TO_UUID(created_by), created_at, isDefaultPermissionGroup FROM permissions", nativeQuery = true)
    public List<Object[]> getAllPermissions();

    @Query(value="SELECT permission_id FROM permissions WHERE permission_id = ?1", nativeQuery = true)
    public List<Object[]> getPermissionById(int id);

    @Query(value="SELECT permission_id FROM permissions WHERE isDefaultPermissionGroup = ?1", nativeQuery = true)
    public List<Object[]> getPermissionIdByIsDefaultPermissionGroup(boolean isDefaultPermissionGroup);

    @Query(value="SELECT permission_id, permissions_name, permission_desc, permissions, BIN_TO_UUID(created_by), created_at, isDefaultPermissionGroup FROM permissions WHERE isDefaultPermissionGroup = ?1", nativeQuery = true)
    public List<Object[]> getDefaultPermissions(boolean isDefaultPermissionGroup);

    @Transactional
    @Modifying
    @Query(value="UPDATE development.permissions SET permissions = ?2 WHERE permission_id = ?1", nativeQuery = true)
    public void updatePermissionByPermissionId(int id, String permissionData);

    @Transactional
    @Modifying
    @Query(value="UPDATE development.permissions SET isDefaultPermissionGroup = ?2 WHERE permission_id = ?1", nativeQuery = true)
    public void updateDefaultPermissionByPermissionId(int id, boolean isDefaultPermissionGroup);

    @Transactional
    @Modifying
    @Query(value="INSERT INTO development.permissions(permission_id, permissions_name, permission_desc, permissions, created_by, created_at) VALUES (?1, ?2, ?3, ?4, UUID_TO_BIN(?5), ?6, ?7)", nativeQuery = true)
    public void createPermissionGroup(int id, String permissionName, String permissionDesc, String permissionData, String createdBy, Timestamp createdAt, boolean isDefaultPermissionGroup);
}
