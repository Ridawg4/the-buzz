import Navbar from "../Components/Navbar.jsx";
import {useEffect, useState} from "react";
import UserListItem from "../Components/UserListItem.jsx";
import "./css/user-list.css"
import ModerationUserEdit from "../Components/ModerationUserEdit.jsx";

function UserList() {
    const [ userEntries, setUserEntries ] = useState([]);
    const [ permissionEntries, setPermissionEntries ] = useState([]);
    const [ isEditing, setIsEditing ] = useState(false);
    const [ userDetailsToEdit, setUserDetailsToEdit ] = useState(null);

    const getUsers = async () => {
        if (!userEntries.length > 0) {
            fetch("http://localhost:8080/api/v1/user", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                response.json().then((data) => {
                    let users = data.users

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

    const editAccount = (isEditing, userDetails) => {
        setUserDetailsToEdit(userDetails)
        setIsEditing(isEditing)
    }

    const closeEditing = () => {
        setUserDetailsToEdit(null)
        setIsEditing(false)
    }

    const isEditingPageVisible = () => {
        if (isEditing) {
           return  (
               <ModerationUserEdit userId={userDetailsToEdit.userId} username = {userDetailsToEdit.username} email = {userDetailsToEdit.email} joinDate = {userDetailsToEdit.joinDate} fullName ={userDetailsToEdit.fullName}  userDesc = {userDetailsToEdit.desc} endEditing={closeEditing}/>
           )
        } else {
            getUsers();
            return (
                <div className="user-list">
                {userEntries.map((object, index) => (
                    <UserListItem key={object.user_id} userId={object.user_id} username={object.username}
                                  email={object.email} joinDate={object.join_date} fullName={object.full_name}
                                  userDesc={object.user_desc} permissionId={object.permissions.permission_id}
                                  permissionGroups={permissionEntries} setEditing = {editAccount}/>
                ))}
            </div>
            )
        }
    }

    useEffect(() => {
        getUsers()
    })

    return (
        <>
            <Navbar />
            {isEditingPageVisible()}
        </>
    )
}

export default UserList;