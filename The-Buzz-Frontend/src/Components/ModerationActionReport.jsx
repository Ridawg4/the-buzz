import React, {useContext} from "react";
import ReportHandlingPopup from "./ReportHandlingPopup.jsx";
import ReportUserEdit from "./ReportUserEdit.jsx";
import {UserContext} from "../Contexts/UserContext.jsx";
import LoadingCircle from "./LoadingCircle.jsx";

function ModerationActionReport({ userObject, reportId, contentType, reportObject, closeHandlingReport }) {
    const [activeAction, setActiveAction] = React.useState("");
    const [explanation, setExplanation] = React.useState("");
    const [textFormData, setTextFormData] = React.useState(null);
    const [imageFormData, setImageFormData] = React.useState(null);
    const [savedReport, setSavedReport] = React.useState(false);
    const globalUser = useContext(UserContext);

    const changeActionType = () => {
        let actionOption = document.getElementById(reportId + "-Action-Dropdown");

        setActiveAction(actionOption.value)
    }

    const changeExplanation = () => {
        let explanationOption = document.getElementById(reportId + "-Explanation-Dropdown");

        setExplanation(explanationOption.value)
    }

    const saveChangesFromForm = (formData, dataType) => {
        if(dataType === "text") {
            setTextFormData(formData);
        }
        if(dataType === "image") {
            setImageFormData(formData);
        }
    }

    const showActiveAction = () => {
        if (contentType === "user-account") {
            if (activeAction === "noting") {
                return (<></>);
            } else if (activeAction === "warn") {
                return (<></>);
            } else if (activeAction === "edit") {
                return (<ReportUserEdit userObject={userObject} reportId={reportId} contentType={contentType} sendValuesToParent={saveChangesFromForm} />);
            } else if (activeAction === "delete") {
                return (<></>);
            } else {
                return (<></>);
            }
        } else {
            return (<></>);
        }
    }

    const showExplanationBox = () => {
        if(explanation === "other") {
            return (<textarea className="ModerationReasonTextArea" id={reportId + "-Response"} placeholder="Enter Reason here"></textarea>)
        } else {
            return (null)
        }
    }

    const cancelReport = () => {
        closeHandlingReport()
    }

    const submitReport = () => {
        let formData = new FormData();
        let actionOption = document.getElementById(reportId + "-Action-Dropdown");

        formData.append("reportId", reportId);
        formData.append("originalReport", JSON.stringify(reportObject));
        formData.append("handlerId", globalUser.userId);
        formData.append("action", actionOption.value);
        if (explanation !== "other") {
            formData.append("explanation", explanation);
        } else {
            let textArea = document.getElementById(reportId + "-Response");
            formData.append("explanation", textArea.innerText);
        }

        if(!savedReport) {
            setSavedReport(true);
            fetch("http://localhost:8080/api/v1/reports/submit-report", {
                method: "POST",
                credentials: "include",
                body: formData
            }).then(res => {
                if (res.status === 202) {
                    if(actionOption.value === "edit") {
                        if(textFormData !== null) {
                            alterUserAccountDetails();
                        }
                        if(imageFormData !== null) {
                            alterUserProfilePicture();
                        }
                    } else if (actionOption.value === "delete") {
                        deleteUserAccount();
                    }
                    cancelReport();
                }
            })
        }
    }

    const alterUserAccountDetails = () => {
        fetch("http://localhost:8080/api/v1/user/modify-user", {
            method: "POST",
            credentials: "include",
            body: textFormData
        }).then(res => {
            if (res.status === 403) {
                console.log("Not authorized");
            }
            if (res.status === 400) {
                console.log("Error modifying password | Bad Request");
            }
        })
    }

    const deleteUserAccount = () => {
        console.log(userObject.userId)
        fetch("http://localhost:8080/api/v1/user/delete-account?approved=true&userId=" + userObject.userId, {
            method: "POST",
            credentials: "include"
        }).then(res => {
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

    const alterUserProfilePicture = () => {
        fetch("http://localhost:8080/api/v1/user/modify-profile-picture?time=" + Date.now() + "&userId=" + userObject.userId, {
            method: "POST",
            credentials: "include",
            body: imageFormData
        }).then(res => {
            if (res.status === 201) {
                console.log("updated");
            }
            if (res.status === 403) {
                console.log("Forbidden")
            }
            if (res.status === 400) {
                console.log("Error modifying password | Bad Request");
                res.text().then(text => {
                    console.log("Error modifying password | Bad Request " + text);
                })
            }
        })
    }


    const isSubmitting = () => {
        if(!savedReport) {
            return (<div id={reportId + "ModerationReport"} className="ModerationActionReport">
                <select id={reportId + "-Action-Dropdown"} onChange={changeActionType}>
                    <option value="nothing">No Action</option>
                    <option value="warn">Warning</option>
                    <option value="edit">Edit Account</option>
                    <option value="delete">Delete Account</option>
                </select>
                {showActiveAction()}
                <select id={reportId + "-Explanation-Dropdown"} onChange={changeExplanation}>
                    <option value="profanity">Profanity</option>
                    <option value="duplicate">Duplicate Request</option>
                    <option value="other">Other Reason</option>
                </select>
                {showExplanationBox()}
                <div className="ModerationActionReportOptions">
                    <button onClick={cancelReport}>Cancel Report</button>
                    <button onClick={submitReport}>Submit Report</button>
                </div>
            </div>)
        } else {
            return (<div id={reportId + "ModerationReport"} className="ModerationActionReport">
                <LoadingCircle />
            </div>)
        }
    }

    return (isSubmitting())
}

export default ModerationActionReport;