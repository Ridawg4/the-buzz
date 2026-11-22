import React, {useContext} from "react";
import {useUserAuth} from "../Contexts/UserContext.jsx";
import {useLocation, useNavigate} from "react-router-dom";

function LogoutButton() {
    const { userId, logout } = useUserAuth();
    const location = useLocation();
    const navigate = useNavigate();


    return (<button onClick = {logout}>Logout</button>)
}

export default LogoutButton;