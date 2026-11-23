import React, {useContext} from "react";
import ReportHandlingPopup from "./ReportHandlingPopup.jsx";
import ReportUserEdit from "./ReportUserEdit.jsx";
import {useUserAuth} from "../../Contexts/UserContext.jsx";
import LoadingCircle from "../LoadingCircle.jsx";
import {useModeration} from "../../Contexts/ModerationContext.jsx";

function ModerationActionReport({ userObject, reportId, contentType, reportObject, closeHandlingReport }) {
    const [activeAction, setActiveAction] = React.useState("");
    const [explanation, setExplanation] = React.useState("");
    const [textFormData, setTextFormData] = React.useState(null);
    const [imageFormData, setImageFormData] = React.useState(null);
    const [savedReport, setSavedReport] = React.useState(false);
    const globalUser = useUserAuth();
    const {submitReport} = useModeration();

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

    const startSubmitReport = async () => {
        let formData = new FormData();
        let actionOption = document.getElementById(reportId + "-Action-Dropdown");

        formData.append("reportId", reportId);
        formData.append("originalReport", JSON.stringify(reportObject));
        formData.append("handlerId", globalUser.userId);
        formData.append("action", actionOption.value);

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
            let result = await submitReport(formData, textFormData, imageFormData, actionOption)

            if(result.success) {
                cancelReport()
            }
            if(result.success) {
                setSavedReport(false);
                console.log("error submitting report");
            }
        }
    }

    document.addEventListener("DOMContentLoaded", () => {
        let explanationOption = document.getElementById(reportId + "-Explanation-Dropdown");

        setExplanation(explanationOption.value)

        let actionOption = document.getElementById(reportId + "-Action-Dropdown");

        setActiveAction(actionOption.value)
    })


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
                    <button onClick={startSubmitReport}>Submit Report</button>
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