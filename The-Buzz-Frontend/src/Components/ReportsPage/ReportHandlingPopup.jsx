import AccountInfoPopup from "../AccountPage/AccountInfoPopup.jsx";
import {useState} from "react";
import ModerationActionReport from "./ModerationActionReport.jsx";

function ReportHandlingPopup({ reportId, contentType, contentUUID = null, contentId = null, userWhoReported, dateReported, reason, closeHandlingReport}) {
    const [creatingReport, setCreatingReport] = useState(false);
    const [loadedUserAccount, setLoadedUserAccount] = useState({});

    const cancelHandling = () => {
        closeHandlingReport();
    }
    const isReportAnonymous = () => {
        if(userWhoReported === "ec61f5f9-33dc-4905-9b84-468bc506a7a3") {
            return "Anonymous"
        } else {
            return (<AccountInfoPopup userId ={userWhoReported} />)
        }
    }
    const showItemThatWasReported = () => {
        if(contentType === "user-account") {
            return (<>
                <label>Reported User:</label>
                <AccountInfoPopup userId ={contentUUID} />
                </>)
        } else {
            return <p>{contentType}</p>
        }
    }

    const loadUserDetails = () => {
        fetch("http://localhost:8080/api/v1/user/load-details?userId=" + contentUUID,
            {credentials: "include"}
        ).then((response) => {

            response.json().then((json) => {
                let userObject = {};
                userObject.userId = json.userId;
                userObject.username = json.username;
                userObject.email = json.email;
                userObject.userJoinDate = json.join_date;
                userObject.fullName = json.full_name;
                userObject.userDesc = json.user_desc;

                setLoadedUserAccount(userObject)
            })
        });
    }

    const createReport = () => {
        setCreatingReport(true);
    }
    const isCreatingReport = () => {
        if(creatingReport) {
            loadUserDetails()
            return (<>
                <ModerationActionReport userObject = {loadedUserAccount} contentType = {contentType} reportId={reportId} reportObject={{reportId, contentType, contentUUID, contentId, userWhoReported, dateReported, reason}} closeHandlingReport = {cancelHandling}/>
                <div className="reportHandlingElement" id={reportId + "-handling-popup"}>
                <label>Report Id:</label>
                <p>{reportId}</p>
                {showItemThatWasReported()}
                <label>Reported By</label>
                <p>{isReportAnonymous()}</p>
                <label>Created at:</label>
                <p>{dateReported}</p>
                <label>Reason:</label>
                <p>{reason}</p>
                <div className="reportHandlingPopupOptions">
                    <button onClick={cancelHandling}>Cancel Report</button>
                    <button onClick={createReport}>Create Report</button>
                </div>
            </div></>)
        } else {
            return (<div className="reportHandlingElement" id={reportId + "-handling-popup"}>
                <label>Report Id:</label>
                <p>{reportId}</p>
                {showItemThatWasReported()}
                <label>Reported By</label>
                <p>{isReportAnonymous()}</p>
                <label>Created at:</label>
                <p>{dateReported}</p>
                <label>Reason:</label>
                <p>{reason}</p>
                <div className="reportHandlingPopupOptions">
                    <button onClick={cancelHandling}>Cancel Report</button>
                    <button onClick={createReport}>Create Report</button>
                </div>
            </div>)
        }
    }

    return (isCreatingReport())
}

export default ReportHandlingPopup;