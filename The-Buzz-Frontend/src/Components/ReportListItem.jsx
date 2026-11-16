import AccountEditPermissions from "./AccountEditPermissions.jsx";
import {useContext} from "react";
import {UserContext} from "../Contexts/UserContext.jsx";

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

    return (
        <div className="report-list-item" key={reportId}>
            <p>{reportId}</p>
            <p>{contentType}</p>
            <p>{isUUIDOrId()}</p>
            <p>{userWhoReported}</p>
            <p>{dateReported}</p>
            <p>{reason}</p>
            <button onClick={handleReport}></button>
        </div>
    )
}

export default UserListItem;