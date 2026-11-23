import Navbar from "../Components/Navigation/Navbar.jsx";
import React, {useContext} from "react";
import {useUserAuth} from "../Contexts/UserContext.jsx";
import {useNavigate} from "react-router-dom";
import "./css/Account.css"
import AccountDetailsInputBox from "../Components/AccountPage/AccountDetailsInputBox.jsx";
import AccountDetailsProfilePictureInput from "../Components/AccountPage/AccountDetailsProfilePictureInput.jsx";
import AccountPasswordInputBox from "../Components/AccountPage/AccountPasswordInputBox.jsx";
import LogoutButton from "../Components/LogoutButton.jsx";

function Account() {
    const { userId, deleteAccount, loadUserDetails } = useUserAuth();
    const navigate = useNavigate();

    const verifyAccountDeletion = () => {
        document.getElementById("Account-Deletion-Confirmation-Popup").style.display = "grid";
    }

    const closePopup = () => {
        document.getElementById("Account-Deletion-Confirmation-Popup").style.display = "none";
    }

    const getUserDetails = async () => {
        let results = await loadUserDetails();

        const usernameBox = document.getElementById("username");
        const emailBox = document.getElementById("email");
        const joinDateBox = document.getElementById("join-date");
        const fullNameBox = document.getElementById("full_name");
        const descBox = document.getElementById("user_desc");

        usernameBox.value = results.username;
        emailBox.value = results.email;
        joinDateBox.value = results.join_date;
        fullNameBox.value = results.full_name;
        descBox.value = results.user_desc;
    }

    React.useEffect(() => {
        getUserDetails()
    }, [])

    {/* @TODO Make it look pretty then allow for account details editing */}
    return (
        <>
        <Navbar />
        <div className="centerdiv">
                <h3>
                    Account Details
                </h3>
            <div className="accountDetails">
                <AccountDetailsProfilePictureInput id="pfp-location"/>

                <AccountDetailsInputBox id="username" labelName = "Username"/>

                <AccountDetailsInputBox id="email" labelName = "Email" type="email"/>

                <AccountPasswordInputBox id="password" labelName = "Change Password" type="password"/>

                <AccountDetailsInputBox id="join-date" labelName = "Join Date" canEdit = {false}/>

                <AccountDetailsInputBox id="full_name" labelName = "Full Name"/>

                <AccountDetailsInputBox id="user_desc" labelName = "Description"/>
            </div>
            <div className="options">
                <button className="deleteAccountButton" onClick = {verifyAccountDeletion}>Delete Account</button>
            </div>
        </div>
            <div id="Account-Deletion-Confirmation-Popup">
                <h4>Woah there!</h4>
                <p>This action will permanently delete your user account and all settings related to your account</p>
                <div id="options">
                    <button onClick = {closePopup}>Cancel</button>
                    <button onClick = {deleteAccount}>Delete Account</button>
                </div>
        </div>
        </>
    )
}



export default Account;