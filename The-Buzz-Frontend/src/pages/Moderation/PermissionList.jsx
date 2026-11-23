import Navbar from "../../Components/Navigation/Navbar.jsx";
import PermissionEditBox from "../../Components/PermissionPage/PermissionEditBox.jsx";
import {useContext, useEffect, useRef, useState} from "react";
import PermissionListItem from "../../Components/PermissionPage/PermissionListItem.jsx";
import PermissionsEdit from "../../Components/PermissionPage/PermissionsEdit.jsx";
import PermissionsCreate from "../../Components/PermissionPage/PermissionsCreate.jsx";
import LoadingCircle from "../../Components/LoadingCircle.jsx";
import {useModeration} from "../../Contexts/ModerationContext.jsx";
import {useNavigate} from "react-router-dom";
import {useUserAuth} from "../../Contexts/UserContext.jsx";

function PermissionList() {
    const [ isEditing, setIsEditing ] = useState(-1);
    const [ pemissionEditingName, setPemissionEditingName ] = useState('');
    const [ pemissionEditingDesc, setPemissionEditingDesc ] = useState('');
    const [ isCreating, setIsCreating ] = useState(-1);
    let isNewPermission = useRef(false);
    const { permissionData, isLoadingPermissionData, permissionLoadError, setEditPermissionId, getPermissionValues } = useModeration();
    const { isModerator } = useUserAuth();
    const navigate = useNavigate();

    const prepareForEditing = (permissionId, permissionName, permissionDesc) => {
        setPemissionEditingName(permissionName)
        setPemissionEditingDesc(permissionDesc)
        getPermissionValues(permissionId);
        console.log("In permission list: " + permissionId)
        setIsEditing(permissionId)
    }

    const removeEditing = () => {
        setIsEditing(-1)
    }

    const removeCreating = () => {
        setIsCreating(-1)
        isNewPermission.current = true;
    }

    const newPermission = () => {
        let nonUsedIndex = permissionData[permissionData.length - 1].permission_id + 1
        setIsCreating(nonUsedIndex)
        setEditPermissionId(null);
        getPermissionValues();
        isNewPermission.current = true;
    }

    const setState = () => {
        if(isModerator) {
            if (!isLoadingPermissionData) {
                if (isEditing !== -1) {
                    return <PermissionsEdit permissionName={pemissionEditingName} permissionDesc={pemissionEditingDesc}
                                            permissionId={isEditing} removeEditing={removeEditing}/>
                } else if (isCreating !== -1) {
                    return <PermissionsCreate permissionId={isCreating} removeCreating={removeCreating}/>
                } else {
                    return (<div id="permission-list">
                        {permissionData.map((object, index) => (
                            <PermissionListItem key={index} permissionId={object.permission_id}
                                                permissionName={object.permission_name}
                                                permissionDesc={object.permission_desc} createdBy={object.created_by}
                                                createdAt={object.created_at}
                                                isDefaultPermissions={object.is_default_permission_group}
                                                editPage={prepareForEditing}/>
                        ))}
                        <button onClick={newPermission}>Create New Permission Group</button>
                    </div>)
                }
            } else {
                if(permissionLoadError.success !== null && permissionLoadError.success === false ) {
                    return <p>You are not authorized to access this resource</p>
                }
                return (<div className="centerdiv"><LoadingCircle/></div>)
            }
        } else {
            return (<div className="centerdiv"><LoadingCircle/></div>)
        }
    }

    return (
        <>
            <Navbar />
            {setState()}
        </>
    )
}

export default PermissionList;