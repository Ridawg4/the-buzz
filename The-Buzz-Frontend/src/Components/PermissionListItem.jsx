import React from "react";
import '../pages/css/PermissionListItem.css'

function PermissionListItem({ permissionId, permissionName, permissionDesc, createdBy, createdAt, editPage, deleteEntry}) {
    let [ isVisible, setIsVisible ] = React.useState(true);

    const editItem = () => {
        editPage(permissionId, permissionName, permissionDesc);
    }

    const setDefaultPermissionGroup = () => {
        fetch("http://localhost:8080/api/v1/permissions/update-default-permission-group?permissionId=" + permissionId + "&isDefaultPermissionGroup=true", {
            method: "POST",
            credentials: "include",
        }).then((response) => {
            if(!response.ok) {
                response.text().then(text => {
                    console.log(text);
                })
            }
        })
    }
    const deletePermission = () => {
            fetch("http://localhost:8080/api/v1/permissions/delete-permission-group?permissionId=" + permissionId + "&isConfirmed=true", {
                method: "POST",
                credentials: "include"
            }).then(res => {
                if(res.status === 202) {
                    setIsVisible(false);
                }
                if(res.status === 401) {
                    res.text().then(text => {
                        console.log("Deletion failed: Authorized | " + text);
                    })
                }
                if(res.status === 400) {
                    res.text().then(text => {
                        console.log("Deletion failed: Bad Request | " + text);
                    })
                }
                if(res.status === 500) {
                    console.log("Deletion failed from server error");
                }
            })
    }

    const promptDeletePermission = () => {
        document.getElementById(permissionId + "-permission-delete-popup-confirmation").style.display = "flex";
    }

    const closePopup = () => {
        document.getElementById(permissionId + "-permission-delete-popup-confirmation").style.display = "none";
    }

    {if(isVisible) {
       return (<>
                <div className="permission-list-item" key = {permissionId}>
                    <label>Permission ID
                        <p>{permissionId}</p>
                    </label>
                    <label>Permission Name
                        <p>{permissionName}</p>
                    </label>
                    <label>Permission Description
                        <p>{permissionDesc}</p>
                    </label>
                    <label>Creator
                        <p>{createdBy}</p>
                    </label>
                    <label>Creation Date
                        <p>{createdAt}</p>
                    </label>
                    <button onClick={editItem}>Edit Permissions</button>
                    <button onClick={promptDeletePermission}>Delete Permissions</button>
                    <button onClick={setDefaultPermissionGroup}>Set As The Default Permissions</button>
                    <div id={permissionId + "-permission-delete-popup-confirmation"} className = "permission-delete-popup-confirmation">
                        <h4>Woah there!</h4>
                        <p>This action will permanently delete your permission group and reset all users using this group to defaults</p>
                        <div id="options">
                            <button onClick = {closePopup}>Cancel</button>
                            <button onClick = {deletePermission}>Delete Account</button>
                        </div>
                    </div>
                </div>
                </>)
        }
        return <></>;
        }
}

export default PermissionListItem;