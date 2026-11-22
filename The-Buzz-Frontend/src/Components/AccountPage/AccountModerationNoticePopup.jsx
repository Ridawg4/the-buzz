import {useContext, useEffect, useState} from "react";
import "../../pages/css/AccountModerationEntry.css"

function AccountModerationNoticePopup({ key, userId, actions, closePopup }) {
    const [isVisible, setIsVisible] = useState(true);
    const [isAllowedToAcknowledge, setIsAllowedToAcknowledge] = useState(true);

    const severityText = () => {
        if(actions.moderationSeverity === "ban") {
            if(!isAccountStillDisabled()) {
                return "Your account was banned";
            }
            return "Your account has been banned";
        }
        if(actions.moderationSeverity === "warn") {
            return "Your account has received a warning";
        }
        if(actions.moderationSeverity === "edit") {
            return "Your account info has been altered by an moderator";
        }
        return "Unknown moderator action"
    }

    const getActedOnDateTime = () => {
        let dateActed = new Date(actions.dateApplied)

        return dateActed.toDateString() + ", " + dateActed.toLocaleTimeString()
    }

    const getActionExpiresDateTime = () => {
        let dateActed = new Date(actions.dateExpires)

        return dateActed.toDateString() + ", " + dateActed.toLocaleTimeString()
    }

    const isAccountStillDisabledNoticeText = () => {
        if(isAccountStillDisabled()) {
            return (
                <>
                    <label>Account is available again at:</label>
                    <p>{getActionExpiresDateTime()}</p>
                </>
            )
        } else {
            return (null)
        }
    }

    const isAccountStillDisabled = () => {
        return new Date() < new Date(actions.dateExpires)
    }

    const updateRefresh = () => {
        if(isAccountStillDisabled()) {
            if(!isAllowedToAcknowledge) {
                setIsAllowedToAcknowledge(true)
            }
        } else {
            if(isAllowedToAcknowledge) {
                setIsAllowedToAcknowledge(false)
            }
        }
    }

    const isCanAcknowledgeActionButtonAllowed = () => {
        if(isAccountStillDisabled()) {
            return (
                <h4>This action is not able to be acknowledged at this time.</h4>
            )
        } else {
            return (
                <div className="moderation-action-options">
                    <button onClick={acknowledgeAction}>Acknowledge Report</button>
                </div>
            )
        }
    }

    const acknowledgeAction = () => {
        let formData = new FormData();
        formData.append("userId", userId);
        formData.append("reportId", actions.moderationId);
        formData.append("isAcknowledged", true);

        fetch("http://localhost:8080/api/v1/content/action/acknowledge", {
            method: "POST",
            body: formData
        })
            .then(res => {
                if(res.status === 202) {
                    setIsVisible(false)
                } else {
                    console.log("error: " + res.status)
                }
            })
    }

    const showSelf = () => {
        if(!isVisible) {
            return null;
        } else {
            return (
                <div className="NoticeEntry">
                    <h4>{severityText()}</h4>
                    <label>Reason:</label>
                    <p>{actions.moderationDesc}</p>
                    <br/>
                    <label>Moderation Action Happened On:</label>
                    <p>{getActedOnDateTime()}</p>
                    {isAccountStillDisabledNoticeText()}
                    {isCanAcknowledgeActionButtonAllowed()}
                </div>
            )
        }
    }

    useEffect(() => {
        setInterval(updateRefresh, 10000, [])
    })

    return showSelf();
}

export default AccountModerationNoticePopup