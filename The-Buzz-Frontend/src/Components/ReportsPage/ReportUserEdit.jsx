import React, {useContext} from "react";
import {useNavigate} from "react-router-dom";
import "../../pages/css/Account.css"
import AccountDetailsInputBox from "../AccountPage/AccountDetailsInputBox.jsx";
import AccountDetailsProfilePictureInput from "../AccountPage/AccountDetailsProfilePictureInput.jsx";
import AccountPasswordInputBox from "../AccountPage/AccountPasswordInputBox.jsx";

function ReportUserEdit( {userObject, sendValuesToParent} ) {

    return (
            <div>
                <h3>
                    Account Details For User: {userObject.email}
                </h3>
                <AccountDetailsProfilePictureInput userId={userObject.userId} id="pfp-location" sendChangesToParent = {sendValuesToParent}/>

                <AccountDetailsInputBox userId = {userObject.userId} defaultText={userObject.username} id="username" labelName = "Username" sendChangesToParent = {sendValuesToParent}/>

                <AccountDetailsInputBox userId = {userObject.userId} defaultText={userObject.email} id="email" labelName = "Email" type="email" sendChangesToParent = {sendValuesToParent}/>

                <AccountPasswordInputBox userId = {userObject.userId} id="password" labelName = "Change Password" type="password"/>

                <AccountDetailsInputBox userId = {userObject.userId} defaultText={userObject.joinDate} id="join-date" labelName = "Join Date" canEdit = {false} sendChangesToParent = {sendValuesToParent}/>

                <AccountDetailsInputBox userId = {userObject.userId} defaultText={userObject.fullName} id="full_name" labelName = "Full Name" sendChangesToParent = {sendValuesToParent}/>

                <AccountDetailsInputBox userId = {userObject.userId} defaultText={userObject.userDesc} id="user_desc" labelName = "Description" sendChangesToParent = {sendValuesToParent}/>
            </div>
    )
}



export default ReportUserEdit;