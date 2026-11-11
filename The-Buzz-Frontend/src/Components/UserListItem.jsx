import AccountEditPermissions from "./AccountEditPermissions.jsx";

function UserListItem({userId, username, email, joinDate, fullName, userDesc, permissionId, permissionGroups}) {

    return (
        <div className="user-list-item" key={userId}>
            <p>{userId}</p>
            <p>{username}</p>
            <p>{email}</p>
            <p>{joinDate}</p>
            <p>{fullName}</p>
            <p>{userDesc}</p>
            <p>{permissionId}</p>
            <AccountEditPermissions userId = {userId} permissionGroups={permissionGroups}/>
        </div>
    )
}

export default UserListItem;