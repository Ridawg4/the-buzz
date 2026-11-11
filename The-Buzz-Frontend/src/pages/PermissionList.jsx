import Navbar from "../Components/Navbar.jsx";
import PermissionEditBox from "../Components/PermissionEditBox.jsx";
import {useContext, useEffect, useState} from "react";
import PermissionListItem from "../Components/PermissionListItem.jsx";
import PermissionsEdit from "./PermissionsEdit.jsx";
import PermissionsCreate from "./PermissionsCreate.jsx";

function PermissionList() {
    const [ permissionEntries, setPermissionEntries ] = useState([]);
    const [ isEditing, setIsEditing ] = useState(-1);
    const [ pemissionEditingName, setPemissionEditingName ] = useState('');
    const [ pemissionEditingDesc, setPemissionEditingDesc ] = useState('');
    const [ isCreating, setIsCreating ] = useState(-1);


    const getPermissions = () => {
        if (!permissionEntries.length > 0) {
            fetch("http://localhost:8080/api/v1/permissions", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                response.json().then((data) => {
                    let permissionData = data.permissions
                    console.log("permissionData", permissionData)

                    setPermissionEntries(data.permissions)
                })
            })
        }
    }

    const prepareForEditing = (permissionId, permissionName, permissionDesc) => {
        setPemissionEditingName(permissionName)
        setPemissionEditingDesc(permissionDesc)
        setIsEditing(permissionId)
    }

    const removeEditing = () => {
        setPermissionEntries([])
        setIsEditing(-1)
    }

    const removeCreating = () => {
        setPermissionEntries([])
        setIsCreating(-1)
    }

    const newPermission = () => {
        let nonUsedIndex = permissionEntries[permissionEntries.length - 1].permission_id + 1
        setIsCreating(nonUsedIndex)
    }

    const setState = () => {
        if(isEditing !== -1) {
            return <PermissionsEdit permissionName={pemissionEditingName} permissionDesc={pemissionEditingDesc} permissionId={isEditing} removeEditing={removeEditing}/>
        } else if(isCreating !== -1) {
            return <PermissionsCreate permissionId={isCreating} removeCreating={removeCreating}/>
        }  else {
            return (<div id="permission-list">
                {permissionEntries.map((object, index) => (
                    <PermissionListItem key={index} permissionId={object.permission_id} permissionName={object.permission_name} permissionDesc={object.permission_desc} createdBy={object.created_by} createdAt={object.created_at} editPage={prepareForEditing}/>
                ))}
                <button onClick={newPermission}>Create New Permission Group</button>
            </div>)
        }

    }

    useEffect(() => {
        getPermissions();
    })
    // @TODO Make getting all permission entries, this current file for editing those, setting default
    // permission groups, and creating permission entries

    return (
        <>
            <Navbar />
            {setState()}
        </>
    )
}

export default PermissionList;