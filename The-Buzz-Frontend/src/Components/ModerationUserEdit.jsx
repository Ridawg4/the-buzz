import React, {useContext} from "react";
import {useNavigate} from "react-router-dom";
import "../pages/css/Account.css"
import AccountDetailsInputBox from "../Components/AccountDetailsInputBox.jsx";
import AccountDetailsProfilePictureInput from "../Components/AccountDetailsProfilePictureInput.jsx";
import AccountPasswordInputBox from "../Components/AccountPasswordInputBox.jsx";
import LogoutButton from "../Components/LogoutButton.jsx";

function Account( {userId, username, email, joinDate, fullName, userDesc, endEditing} ) {
    const navigate = useNavigate();

    const verifyAccountDeletion = () => {
        // document.getElementById("Account-Deletion-Confirmation-Popup").style.display = "flex";
    }

    const closePopup = () => {
        document.getElementById("Account-Deletion-Confirmation-Popup").style.display = "none";
    }

    const deleteAccount = () => {
        fetch("http://localhost:8080/api/v1/user/delete-account?approved=true", {
            method: "POST",
            credentials: "include"
        }).then(res => {
            if(res.status === 200) {
                setUserId(null)
                navigate("/login")
            }
            if(res.status === 401) {
                setUserId(null)
                navigate("/login")
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

    const cancelChanges = () => {
        endEditing();
    }

    return (
        <div className="centerdiv">
            <div>
                <h3>
                    Account Details For User: {email}
                </h3>
                <button onClick={cancelChanges}>Back</button>
                <AccountDetailsProfilePictureInput userId={userId} id="pfp-location"/>

                <AccountDetailsInputBox userId = {userId} defaultText={username} id="username" labelName = "Username"/>

                <AccountDetailsInputBox userId = {userId} defaultText={email} id="email" labelName = "Email" type="email"/>

                <AccountPasswordInputBox userId = {userId} id="password" labelName = "Change Password" type="password"/>

                <AccountDetailsInputBox userId = {userId} defaultText={joinDate} id="join-date" labelName = "Join Date" canEdit = {false}/>

                <AccountDetailsInputBox userId = {userId} defaultText={fullName} id="full_name" labelName = "Full Name"/>

                <AccountDetailsInputBox userId = {userId} defaultText={userDesc} id="user_desc" labelName = "Description"/>
            </div>
            <div>
                <button onClick={cancelChanges}>Cancel</button>
                <button onClick = {verifyAccountDeletion}>Delete Account</button>
            </div>
            <div id="Account-Deletion-Confirmation-Popup">
                <h4>Woah there!</h4>
                <p>This action will permanently delete your user account and all settings related to your account</p>
                <div id="options">
                    <button onClick = {closePopup}>Cancel</button>
                    <button onClick = {deleteAccount}>Delete Account</button>
                </div>
            </div>
        </div>
    )
}



export default Account;