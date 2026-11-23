import Navbar from "../../Components/Navigation/Navbar.jsx";
import {useEffect, useState} from "react";
import UserListItem from "../../Components/UserListPage/UserListItem.jsx";
import "../css/user-list.css"
import ModerationUserEdit from "../../Components/ReportsPage/ModerationUserEdit.jsx";
import LoadingCircle from "../../Components/LoadingCircle.jsx";
import {useModeration} from "../../Contexts/ModerationContext.jsx";

function UserList() {
    const [ isEditing, setIsEditing ] = useState(false);
    const [ userDetailsToEdit, setUserDetailsToEdit ] = useState(null);
    const [ isLoading, setIsLoading ] = useState(true);
    const {userData, isLoadingUserData, userLoadError, permissionData} = useModeration();

    const editAccount = (isEditing, userDetails) => {
        setUserDetailsToEdit(userDetails)
        setIsEditing(isEditing)
    }

    const closeEditing = () => {
        setUserDetailsToEdit(null)
        setIsEditing(false)
    }

    const isEditingPageVisible = () => {
        if(!isLoadingUserData) {
            if (isEditing) {
                return (
                    <ModerationUserEdit userId={userDetailsToEdit.userId} username={userDetailsToEdit.username}
                                        email={userDetailsToEdit.email} joinDate={userDetailsToEdit.joinDate}
                                        fullName={userDetailsToEdit.fullName} userDesc={userDetailsToEdit.desc}
                                        endEditing={closeEditing}/>
                )
            } else {
                return (
                    <div className="user-list">
                        {userData.map((object, index) => (
                            <UserListItem key={object.user_id} userId={object.user_id} username={object.username}
                                          email={object.email} joinDate={object.join_date} fullName={object.full_name}
                                          userDesc={object.user_desc} permissionId={object.permissions.permission_id}
                                          permissionGroups={permissionData} setEditing={editAccount}/>
                        ))}
                    </div>
                )
            }
        } else {
            if(userLoadError.success !== null && userLoadError.success === false ) {
                return <p>You are not authorized to access this resource</p>
            }
            return (<div className="centerdiv"><LoadingCircle /></div>)
        }
    }
    //@TODO MAKE USER LIST AND REPORT LIST USE MODERATORCONTEXT TO HANDLE METHOD CALLS AND POLLING

    return (
        <>
            <Navbar />
            {isEditingPageVisible()}
        </>
    )
}

export default UserList;