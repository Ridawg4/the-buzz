function AccountEditPermissions({defaultValue, userId, permissionGroups}) {

    const changeUserPermissionGroup = () => {
        const permissionSelector = document.getElementById(userId);
        fetch("http://localhost:8080/api/v1/user/modify-user-permissions?userId=" + userId + "&permissionGroupId=" + permissionSelector.value, {
            method: "POST",
            credentials: "include",
        }).then((response) => {
            if(response.status !== 202) {
                console.log(response);
            }
        })
    }

    return (
            <select id={userId} onChange={changeUserPermissionGroup} defaultValue={defaultValue}>
                {permissionGroups.map((object, index) => (
                    <option key={index} value={object.permission_name}>{object.permission_name}</option>
                ))}
            </select>
    )
}

export default AccountEditPermissions;