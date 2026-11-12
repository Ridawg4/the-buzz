import Navbar from "../Components/Navbar.jsx";
import React, {useContext} from "react";
import {UserContext} from "../Contexts/UserContext.jsx";
import {useNavigate} from "react-router-dom";
import "./css/Account.css"
import AccountDetailsInputBox from "../Components/AccountDetailsInputBox.jsx";
import AccountDetailsProfilePictureInput from "../Components/AccountDetailsProfilePictureInput.jsx";
import AccountPasswordInputBox from "../Components/AccountPasswordInputBox.jsx";
import LogoutButton from "../Components/LogoutButton.jsx";

function Account() {
    const { userId, setUserId } = useContext(UserContext);
    const navigate = useNavigate();

    const verifyAccountDeletion = () => {
        document.getElementById("Account-Deletion-Confirmation-Popup").style.display = "flex";
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

    const loadUserDetails = () => {
        fetch("http://localhost:8080/api/v1/user/load-details",
            {credentials: "include",
                }
        ).then((response) => {
            if (response.status === 403) {
                navigate("/login");
            }
            if (response.status === 401) {
                navigate("/login");
            }
            if (response.status === 500) {
                navigate("/login");
            }
            response.json().then((json) => {
                const usernameBox = document.getElementById("username");
                const emailBox = document.getElementById("email");
                const joinDateBox = document.getElementById("join-date");
                const fullNameBox = document.getElementById("full_name");
                const descBox = document.getElementById("user_desc");
                if (json === null) {
                    document.getElementById("FailedToGetInfo").style.display = "visible"
                }

                usernameBox.value = json.username;
                emailBox.value = json.email;
                joinDateBox.value = json.join_date;
                fullNameBox.value = json.full_name;
                descBox.value = json.user_desc;
            })
        }).catch((e) => {
            setUserId(null)
            navigate("/login");
        })
    }

    React.useEffect(() => {
        loadUserDetails()
    })

    {/* @TODO Make it look pretty then allow for account details editing */}
    return (
        <div className="centerdiv">
            <Navbar />
            <div>
                <h3>
                    Account Details
                </h3>
                <AccountDetailsProfilePictureInput id="pfp-location"/>

                <AccountDetailsInputBox id="username" labelName = "Username"/>

                <AccountDetailsInputBox id="email" labelName = "Email" type="email"/>

                <AccountPasswordInputBox id="password" labelName = "Change Password" type="password"/>

                <AccountDetailsInputBox id="join-date" labelName = "Join Date" canEdit = {false}/>

                <AccountDetailsInputBox id="full_name" labelName = "Full Name"/>

                <AccountDetailsInputBox id="user_desc" labelName = "Description"/>
            </div>
            <div>
                <button onClick = {verifyAccountDeletion}>Delete Account</button>
                <LogoutButton />
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