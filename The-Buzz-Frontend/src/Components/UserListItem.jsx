import AccountEditPermissions from "./AccountEditPermissions.jsx";
import {useContext} from "react";
import {UserContext} from "../Contexts/UserContext.jsx";

function UserListItem({userId, username, email, joinDate, fullName, userDesc, permissionId, permissionGroups, setEditing}) {
    const globalUser = useContext(UserContext);

    const editUser = () => {
        let userDetails = {};
        userDetails.userId = userId;
        userDetails.username = username;
        userDetails.email = email;
        userDetails.fullName = fullName;
        userDetails.desc = userDesc;
        userDetails.permissionId = permissionId;

        setEditing(true, userDetails)
    }

    const checkIfUserIsCurrUser = () => {
        const index = permissionGroups.findIndex((object) => object.permission_id === permissionId);
        let object = permissionGroups[index];
        if(object === undefined) {
            if(userId === globalUser.userId){
                return (
                    <div className="user-list-item-actions">
                        <p>Current User</p>
                    </div>
                )
            } else {
                return (
                    <div className="user-list-item-actions">
                        <AccountEditPermissions userId = {userId} permissionGroups={permissionGroups}/>
                        <button onClick={editUser}>Edit User</button>
                    </div>
                )
            }
        }

        if(userId === globalUser.userId){
            return (
                <div className="user-list-item-actions">
                    <p>Current User</p>
                </div>
            )
        } else {
            return (
                <div className="user-list-item-actions">
                    <AccountEditPermissions defaultValue={object.permission_name} userId = {userId} permissionGroups={permissionGroups}/>
                    <button onClick={editUser}>Edit User</button>
                </div>
            )
        }
    }

    return (
        <div className="user-list-item" key={userId}>
            <p>{userId}</p>
            <p>{username}</p>
            <p>{email}</p>
            <p>{joinDate}</p>
            <p>{fullName}</p>
            <p>{userDesc}</p>
            {checkIfUserIsCurrUser()}
        </div>
    )
}

export default UserListItem;