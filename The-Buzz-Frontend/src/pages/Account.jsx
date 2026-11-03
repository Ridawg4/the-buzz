import Navbar from "../Components/Navbar.jsx";
import {useContext, useState} from "react";
import {UserContext} from "../Contexts/UserContext.jsx";
import {useNavigate} from "react-router-dom";
import "./css/Account.css"

function Account() {
    const { userId, setUserId } = useContext(UserContext);
    const navigate = useNavigate();

    if (userId === null) {
        navigate("/login");
    }

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
        fetch("http://localhost:8080/api/v1/user/load-details", {credentials: "include"}
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
                const fullNameBox = document.getElementById("full-name");
                const descBox = document.getElementById("desc");
                const pfpLocationBox = document.getElementById("pfp-location");
                if (json === null) {
                    document.getElementById("FailedToGetInfo").style.display = "visible"
                }

                usernameBox.innerText = json.username;
                emailBox.innerText = json.email;
                joinDateBox.innerText = json.join_date;
                fullNameBox.innerText = json.full_name;
                descBox.innerText = json.user_desc;
                pfpLocationBox.innerText = json.pfpLocation;
            })
        })
    }

    const logout = ((event) => {
        event.preventDefault()
        setUserId(null);
        fetch("/api/v1/logout", {
            credentials: "include",
        })
        navigate("/login")
    })

    loadUserDetails()
    {/* @TODO Make it look pretty then allow for account details editing */}
    return (
        <>
            <Navbar />
            <h3>
                Account Details
            </h3>
            <div>
                <label>Username</label>
                <p id="username"></p>

                <label>Email</label>
                <p id="email"></p>

                <label>Join Date</label>
                <p id="join-date"></p>

                <label>Full Name</label>
                <p id="full-name"></p>

                <label>Description</label>
                <p id="desc"></p>

                <label>Pfp location</label>
                <p id="pfp-location"></p>
            </div>
            <div>
                <button onClick = {verifyAccountDeletion}>deleteAccount</button>
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