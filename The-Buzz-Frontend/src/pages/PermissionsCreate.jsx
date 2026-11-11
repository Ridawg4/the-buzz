import PermissionEditBox from "../Components/PermissionEditBox.jsx";
import {useContext, useEffect, useState} from "react";

function PermissionsEdit({permissionId, removeCreating} ) {
    const [ permissionKeys, setPermissionKeys ] = useState([]);
    const [ permissionVals, setPermissionVals ] = useState([]);
    const [ retrievedPermissions, setRetrievedPermissions ] = useState(false);

    const getPermissions = () => {
        if (!retrievedPermissions) {
            fetch("http://localhost:8080/api/v1/permissions/defaults", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                response.json().then((data) => {
                    let permissionData = data.permission_data

                    setPermissionKeys(Object.keys(permissionData))
                    setPermissionVals(Object.values(permissionData))

                    setRetrievedPermissions(true)
                })
            })
        }
    }

    const submitPermissions = () => {
        const permissions = {};
        const permissionObject = {};

        for(let i = 0; i < permissionKeys.length; i++) {
            permissions[permissionKeys[i]] = permissionVals[i]
        }
        console.log(permissions);
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
        getPermissions();
    })
    // @TODO Make getting all permission entries, this current file for editing those, setting default
    // permission groups, and creating permission entries

    return (
        <>
            <div id="permission-create-div">
                <label>Permission Name
                    <input type="text" placeholder="Permission Name" id="permission-name-input" />
                </label>
                <label>Permission Description
                    <input type="text" placeholder="Permission Description" id="permission-description-input" />
                </label>
                {permissionKeys.map((key, index) => (
                    <PermissionEditBox keyName={key} key={key} value={permissionVals[index]} allKeys = {permissionKeys} allVals = {permissionVals} setValue = {setPermissionVals} />
                ))}
                <div id="permission-edit-actions">
                    <button onClick={submitPermissions}>Create Permissions</button>
                    <button onClick={removeCreating}>Cancel</button>
                </div>
            </div>
        </>
    )
}

export default PermissionsEdit;