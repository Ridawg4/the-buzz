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
            } else if (activeAction === "ban") {
                return (<input id={reportId + "-BanLengthInput"} type="datetime-local"></input>);
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
        console.log(explanation);
        if (explanation !== "other") {
            let explanationOption = document.getElementById(reportId + "-Explanation-Dropdown");
            formData.append("explanation", explanationOption.value);
        } else {
            let textArea = document.getElementById(reportId + "-Response");
            formData.append("explanation", textArea.value);
        }

        if(actionOption.value === "ban") {
            let banInput = document.getElementById(reportId + "-BanLengthInput");
            formData.append("unbanDate", banInput.value);
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
                    }
                    cancelReport();
                }
            })
        }
    }

    document.addEventListener("DOMContentLoaded", () => {
        let explanationOption = document.getElementById(reportId + "-Explanation-Dropdown");

        setExplanation(explanationOption.value)

        let actionOption = document.getElementById(reportId + "-Action-Dropdown");

        setActiveAction(actionOption.value)
    })

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
                    <option value="ban">Ban Account</option>
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