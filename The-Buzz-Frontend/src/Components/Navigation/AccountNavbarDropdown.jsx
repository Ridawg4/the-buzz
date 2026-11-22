import {useContext, useEffect, useState} from "react";
import {useUserAuth} from "../../Contexts/UserContext.jsx";
import {Link} from "react-router-dom";
import LogoutButton from "../LogoutButton.jsx";

function AccountNavbarDropdown() {
    const { userId, isDj, isModerator } = useUserAuth();

    const openAccountDropdown = () => {
        document.getElementById("dropdownOptions").style.display = "flex";
    }

    const closeAccountDropdown = () => {
        document.getElementById("dropdownOptions").style.display = "none";
    }

    if(userId === null) {
        return (
            <Link to="/login">Login</Link>
        )
    }
    return (
        <div className="account-dropdown-navbar" onMouseEnter={openAccountDropdown}>
            <img id="profile-pic-navbar" src= {"http://localhost:8080/api/v1/user/profile-picture?time=0"}/>
            <div id="dropdownOptions" onMouseLeave={closeAccountDropdown}>
                <Link to="/account" className="dropdownOption">Account</Link>
                { isDj ? <Link to="/admin/djs" className="dropdownOption">DJ Profile</Link> : null}
                { isModerator ? <Link to="/admin" className="dropdownOption">Moderation Panel</Link> : null }
                <LogoutButton />
            </div>
        </div>
    )
}

export default AccountNavbarDropdown;