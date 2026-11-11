import {useContext, useEffect, useState} from "react";
import {UserContext} from "../Contexts/UserContext.jsx";
import {Link} from "react-router-dom";
import LogoutButton from "./LogoutButton.jsx";

function AccountNavbarDropdown() {
    const { userId, setUserId } = useContext(UserContext);
    const [ isDj, setIsDj ] = useState(false);
    const [ isModerator, setIsModerator ] = useState(false);
    const [ hasRan, setHasRan ] = useState(false);

    const openAccountDropdown = () => {
        document.getElementById("dropdownOptions").style.display = "flex";
    }

    const closeAccountDropdown = () => {
        document.getElementById("dropdownOptions").style.display = "none";
    }

    const hasAdminRole = () => {
        fetch("http://localhost:8080/api/v1/user/HasPermission?permissionToLookFor=CAN_SEE_ALL_SYSTEM_PANELS",
            {credentials: "include"}
        ).then(response => {
            if (response.ok) {
                response.text().then(text => {
                    console.log(text);
                    if(text === "allowed") {
                        setIsModerator(true)
                    } else {
                        setIsModerator(false)
                    }
                })
            }
        })
    }
    const hasDjRole = () => {
        fetch("http://localhost:8080/api/v1/user/HasPermission?permissionToLookFor=CAN_SEE_DJ_SYSTEM_PANELS",
            {credentials: "include"}
        ).then(response => {
            if (response.ok) {
                response.text().then(text => {
                    if(text === "allowed") {
                        setIsDj(true)
                    } else {
                        setIsDj(false)
                    }
                })
            }
        })
    }

    useEffect(() => {
        if (!hasRan) {
            hasAdminRole()
            hasDjRole()
        }
        setHasRan(true)
    })

    if(userId === null) {
        return (
            <Link to="/login">Login</Link>
        )
    }
    return (
        <div className="account-dropdown-navbar" onMouseEnter={openAccountDropdown}>
            <img id="profile-pic-navbar" src= "http://localhost:8080/api/v1/user/profile-picture?time=0"/>
            <div id="dropdownOptions" onMouseLeave={closeAccountDropdown}>
                <Link to="/account" className="dropdownOption">Account</Link>
                { isDj ? null : <Link to="/admin/djs" className="dropdownOption">DJ Profile</Link>}
                { isModerator ? null : <Link to="/admin" className="dropdownOption">Moderation Panel</Link> }
                <LogoutButton />
            </div>
        </div>
    )
}

export default AccountNavbarDropdown;