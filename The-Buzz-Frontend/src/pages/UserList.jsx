import Navbar from "../Components/Navbar.jsx";
import {useEffect, useState} from "react";
import UserListItem from "../Components/UserListItem.jsx";
import "./css/user-list.css"

function UserList() {
    const [ userEntries, setUserEntries ] = useState([]);
    const [ permissionEntries, setPermissionEntries ] = useState([]);

    const getUsers = () => {
        if (!userEntries.length > 0) {
            fetch("http://localhost:8080/api/v1/user", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                response.json().then((data) => {
                    let users = data.users
                    console.log("permissionData", users)

                    setUserEntries(users)
                })
            })
            fetch("http://localhost:8080/api/v1/permissions", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                response.json().then((data) => {
                    let permissions = data.permissions

                    setPermissionEntries(permissions)
                })
            })
        }
    }

    useEffect(() => {
        getUsers()
    })

    return (
        <>
            <Navbar />
            <div className="user-list">
                {userEntries.map((object, index) => (
                    <UserListItem key={object.user_id} userId={object.user_id} username={object.username} email={object.email} joinDate = {object.join_date} fullName = {object.full_name} userDesc = {object.user_desc} permissionId = {object.permissions.permission_id} permissionGroups={permissionEntries}/>
                ))}
            </div>
        </>
    )
}

export default UserList;