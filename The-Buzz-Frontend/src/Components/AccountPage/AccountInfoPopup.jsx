import {useEffect, useState} from "react";
import "../../pages/css/Account-Info-Popup.css"
import LoadingCircle from "../LoadingCircle.jsx";
import {useModeration} from "../../Contexts/ModerationContext.jsx";

function AccountInfoPopup({ userId }) {
    const [isLoading, setIsLoading] = useState(true);
    const [userObject, setUserObject] = useState(true);
    const {loadUserDetailsOfReport} = useModeration()

    const getUserDetailsForUser = async () => {
        setUserObject(await loadUserDetailsOfReport(userId));
        setIsLoading(false);
    }

    const getUserInfoPopup = () => {
        if(!isLoading) {
           return ( <div className="account-info-popup">
                <img className="info-popup-profile-picture" src={"http://localhost:8080/api/v1/content/user/load-profile-picture?userId=" + userId}></img>
                <p>{userObject.username}</p>
                <p>{userObject.fullName}</p>
                <p>{userObject.userDesc}</p>
                <p>{userObject.userJoinDate}</p>
            </div> )
        } else {
            return (<LoadingCircle />)
        }
    }

    useEffect(() => {
        getUserDetailsForUser()
    }, [])

    return(getUserInfoPopup())
}

export default AccountInfoPopup;