import React from "react";
import {useNavigate} from "react-router-dom";

function AccountDetailsInputBox({ id, labelName, defaultText, canEdit = true, type = "text", children }) {
    const [isModifyValue, setModifyValue] = React.useState(false);
    const [lastValue, setLastValue] = React.useState('');
    const navigate = useNavigate();

    const enableModifying = () => {
        if (canEdit) {
            if (!isModifyValue) {
                setModifyValue(!isModifyValue);
                setLastValue(document.getElementById(id).value);
                document.getElementById(id).disabled = isModifyValue;
                document.getElementById(id + "-action-modals").style.display = "flex";
                document.getElementById(id + "-edit-button").style.visibility = "hidden";
            }
        }
    }

    const hideEditButtonIfNotEditable = () => {
        if(!canEdit) {
            document.getElementById(id + "-edit-button").style.visibility = "hidden";
        }
    }

    const cancelEdits = () => {
        setModifyValue(false);
        document.getElementById(id).value = lastValue
        document.getElementById(id).disabled = isModifyValue;
        document.getElementById(id + "-action-modals").style.display = "none";
        document.getElementById(id + "-edit-button").style.visibility = "visible";
    }

    const saveEdits = () => {
        let formData = new FormData();
        formData.append("username", '');
        formData.append("email", '');
        formData.append("full_name", '');
        formData.append("user_desc", '');

        formData.set(id, document.getElementById(id).value);

        fetch("http://localhost:8080/api/v1/user/modify-user", {
            method: "POST",
            credentials: "include",
            body: formData
        }).then(res => {
            if(res.status === 401) {
                console.log("Error creating modify user");
                navigate("/login");
            }
            if(res.status === 400) {
                console.log("Error modifying password | Bad Request");
                res.text().then(text => {
                    console.log("Error modifying password | Bad Request " + text);
                })
            }
        })

        setModifyValue(false);
        setLastValue(document.getElementById(id).value);
        document.getElementById(id).disabled = isModifyValue;
        document.getElementById(id + "-action-modals").style.display = "none";
        document.getElementById(id + "-edit-button").style.visibility = "visible";
    }

    React.useEffect(() => hideEditButtonIfNotEditable())
    return (
        <div className="accountDetailsInputBox">
            <label>{labelName}</label>
            <div>
            <input disabled id={id} defaultValue={defaultText} type={type}>
                {children}
            </input>
                <button id={id + "-edit-button"} onClick={enableModifying}>Edit</button>
            </div>
            <div id={id + "-action-modals"} className={"action-modals"}>
                <button onClick={cancelEdits}>Cancel</button>
                <button onClick={saveEdits}>Save</button>
            </div>
        </div>
    )
}
export default AccountDetailsInputBox;