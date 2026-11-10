import React, {useContext} from "react";
import {UserContext} from "../Contexts/UserContext.jsx";
import {useLocation, useNavigate} from "react-router-dom";

function LogoutButton() {
    const { userId, setUserId } = useContext(UserContext);
    const location = useLocation();
    const navigate = useNavigate();


    const logout = ((event) => {
        event.preventDefault()
        fetch("http://localhost:8080/api/v1/logout", {
            credentials: "include",
            redirect: 'error'
        }).catch(error => {
            setUserId(null);
            if(location.pathname === "/account") {
                navigate("/login")
            }
            if(location.pathname === "/admin") {
                navigate("/login")
            }
        })
    })

    return (<button onClick = {logout}>Logout</button>)
}

export default LogoutButton;