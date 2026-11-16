import {useContext} from "react";
import {UserContext} from "../Contexts/UserContext.jsx";
import AccountInfoPopup from "./AccountInfoPopup.jsx";

function UserListItem({reportId, contentType, contentUUID = null, contentId = null, userWhoReported, dateReported, reason, setIsHandling}) {
    const globalUser = useContext(UserContext);

    const handleReport = () => {
        setIsHandling(true, reportId)
    }
    const isUUIDOrId = () => {
        if(contentId === 0) {
            return (contentUUID)
        } else {
            return (contentId)
        }
    }
    const isReportAnonymous = () => {
        if(userWhoReported === "ec61f5f9-33dc-4905-9b84-468bc506a7a3") {
            return "Anonymous"
        } else {
            return userWhoReported
        }
    }
    const showItemThatWasReported = () => {
        if(contentType === "user-account") {
            return (<AccountInfoPopup userId ={contentUUID} />)
        } else {
            return <p>{contentType}</p>
        }
    }

    return (
        <div className="report-list-item" key={reportId} onClick={handleReport}>
            <label>Report Id:</label>
            <p>{reportId}</p>
            <label>Reported Content:</label>
            {showItemThatWasReported()}
            <label>Reported By</label>
            <p>{isReportAnonymous()}</p>
            <label>Created at:</label>
            <p>{dateReported}</p>
            <label>Reason:</label>
            <p>{reason}</p>
        </div>
    )
}

export default UserListItem;