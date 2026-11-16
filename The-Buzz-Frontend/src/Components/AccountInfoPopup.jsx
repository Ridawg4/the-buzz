import {useEffect, useState} from "react";
import "../pages/css/Account-Info-Popup.css"
import LoadingCircle from "./LoadingCircle.jsx";

function AccountInfoPopup({ userId }) {
    const [userObject, setUserObject] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    const loadUserDetails = () => {
        fetch("http://localhost:8080/api/v1/content/user/load-profile-info?userId=" + userId,
            {credentials: "include"}
        ).then((response) => {
            response.json().then((json) => {
                let userObject = new Object();
                userObject.username = json.username;
                userObject.userJoinDate = json.join_date;
                userObject.fullName = json.full_name;
                userObject.userDesc = json.user_desc;

                setUserObject(userObject)
                setIsLoading(false)
            })
        })
    }
    useEffect(() => {
        loadUserDetails()
    })

    const getUserInfoPopup = () => {
        if(!isLoading) {
           return ( <div className="account-info-popup">
                <img className="info-popup-profile-picture" src={"http://localhost:8080/api/v1/content/user/load-profile-picture?userId=" + userId}></img>
                <p>{userObject.userName}</p>
                <p>{userObject.fullName}</p>
                <p>{userObject.userDesc}</p>
                <p>{userObject.userJoinDate}</p>
            </div> )
        } else {
            return (<LoadingCircle />)
        }
    }

    return(getUserInfoPopup())
}

export default AccountInfoPopup;