import PermissionEditBox from "./PermissionEditBox.jsx";
import {useContext, useEffect, useState} from "react";
import {useModeration} from "../../Contexts/ModerationContext.jsx";

function PermissionsEdit({permissionId, removeCreating} ) {
    const [ retrievedPermissions, setRetrievedPermissions ] = useState([]);
    const {permissionKeys, permissionVals, allPermissionKeys, allPermissionVals, isLoadingAllPermissionValues, editPermissionId} = useModeration();

    const submitPermissions = () => {
        const permissions = {};
        const permissionObject = {};

        for(let i = 0; i < permissionKeys.length; i++) {
            permissions[permissionKeys[i]] = retrievedPermissions[i]
        }
        // console.log(permissions);
        permissionObject.permission_id = permissionId
        permissionObject.permission_data = permissions
        let form = new FormData()
        form.append("permissionObject", JSON.stringify(permissionObject))
        form.append("permissionName", document.getElementById("permission-name-input").value)
        form.append("permissionDesc", document.getElementById("permission-description-input").value)

        fetch("http://localhost:8080/api/v1/permissions/create-permission-group", {
            method: "POST",
            credentials: "include",
            body: form
        }).then(res => {
            if(res.ok){
                removeCreating()
            }
        })
    }

    useEffect(() => {
        if (!isLoadingAllPermissionValues) {
            setRetrievedPermissions([...permissionVals]);
        }
    }, [isLoadingAllPermissionValues, permissionVals]);

    // @TODO Make getting all permission entries, this current file for editing those, setting default
    // permission groups, and creating permission entries

    const isReady = () => {
        if(isLoadingAllPermissionValues) {
            return null;
        } else {
            return (<>
                <div id="permission-create-div">
                    <label>Permission Name
                        <input type="text" placeholder="Permission Name" id="permission-name-input" />
                    </label>
                    <label>Permission Description
                        <input type="text" placeholder="Permission Description" id="permission-description-input" />
                    </label>
                    {permissionKeys.map((key, index) => (
                        <PermissionEditBox keyName={key} key={key} value={retrievedPermissions[index]} allKeys = {allPermissionKeys} allVals = {allPermissionVals} setValue={setRetrievedPermissions} />
                    ))}
                    <div id="permission-edit-actions">
                        <button onClick={submitPermissions}>Create Permissions</button>
                        <button onClick={removeCreating}>Cancel</button>
                    </div>
                </div>
            </>)
        }
    }

    return isReady()
}

export default PermissionsEdit;