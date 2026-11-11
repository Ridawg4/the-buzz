import Navbar from "../Components/Navbar.jsx";
import PermissionEditBox from "../Components/PermissionEditBox.jsx";
import {useContext, useEffect, useState} from "react";

function Permissions() {
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

    const submitChanges = () => {
        const permissionId = parseInt(document.getElementById("permissionId").value);
        const permissions = {};
        const permissionObject = {};

        for(let i = 0; i < permissionKeys.length; i++) {
            permissions[permissionKeys[i]] = permissionVals[i]
        }
        permissionObject.permission_id = permissionId
        permissionObject.permission_data = permissions
        console.log(permissionObject);
        let form = new FormData()
        form.append("permissionObject", JSON.stringify(permissionObject))

        fetch("http://localhost:8080/api/v1/permissions/update-permission-group", {
            method: "POST",
            credentials: "include",
            body: form
        }).then(res => {
            console.log(res.status)
        })
    }

    useEffect(() => {
        getPermissions();
    })
    // @TODO Make getting all permission entries, this current file for editing those, setting default
    // permission groups, and creating permission entries

    return (
        <>
            <Navbar />
            <div id="permission-edit-div">
                <label>Permission ID</label>
                <input type="number" id="permissionId"></input>
                {permissionKeys.map((key, index) => (
                    <PermissionEditBox keyName={key} key={key} value={permissionVals[index]} allKeys = {permissionKeys} allVals = {permissionVals} setValue = {setPermissionVals} />
                ))}
                <button onClick={submitChanges}>save permissions</button>
            </div>
        </>
    )
}

export default Permissions;