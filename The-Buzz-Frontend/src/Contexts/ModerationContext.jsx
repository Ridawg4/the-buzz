import {createContext, useContext, useEffect, useState} from "react";
import {useUserAuth} from "./UserContext.jsx";

const ModerationContext = createContext(null);

export function ModerationContextProvider({ children }) {
    const [permissionData, setPermissionData] = useState([]);
    const [permissionLoadError, setPermissionLoadError] = useState({});
    const [userData, setUserData] = useState([]);
    const [userLoadError, setUserLoadError] = useState({});
    const [reportData, setReportData] = useState([]);
    const [reportLoadError, setReportLoadError] = useState({});
    const [isLoadingPermissionData, setIsLoadingPermissionData] = useState(true);
    const [isLoadingUserData, setIsLoadingUserData] = useState(true);
    const [isLoadingReportData, setIsLoadingReportData] = useState(true);
    // const { isModerator, userId } = useUserAuth();

    const getPermissions = async (permissionEntries) => {
            return await fetch("http://localhost:8080/api/v1/permissions", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                if(response.status === 200) {
                    return response.json()
                        .then((data) => {
                            setPermissionData(data.permissions)
                            setIsLoadingUserData(false);
                            return {success: true};
                        });
                } else if(response.status === 403) {
                    setUserLoadError({success: false,
                        error: "not authorized"})
                    return {success: false,
                        error: "not authorized"};
                }
            })
    }

    const getUsers = async () => {
        return await fetch("http://localhost:8080/api/v1/user", {
            method: "GET",
            credentials: "include",
        }).then((response) => {
            if(response.status === 200) {
                return response.json().then((data) => {
                    setUserData(data.users)

                    return {success: true};
                })
            }
            if(response.status === 403) {
                return {success: false, error: "not authorized"};
            }
        })
    }

    const getReportEntries = async () => {
            return await fetch("http://localhost:8080/api/v1/reports", {
                method: "GET",
                credentials: "include",
            }).then((response) => {
                if(response.status === 200) {
                    return response.json().then((data) => {
                        let reports = data.reports;
                        if(reportData.length !== reports.length) {
                            setReportData(reports)
                        }
                        return {success: true};
                    })
                } if(response.status === 403) {
                    return {success: false, error: "not authorized"};
                }
            })
    }

    const submitReport = async (formData, textFormData, imageFormData, actionOption) => {
        return await fetch("http://localhost:8080/api/v1/reports/submit-report", {
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
                return {success: true};
            } else {
                return {success: false, error: "unknown error"};
            }
        })
    }

    const alterUserAccountDetails = (textFormData) => {
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

    const alterUserProfilePicture = (imageFormData) => {
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

    const loadUserDetailsOfReport = async (userId) => {
        return await fetch("http://localhost:8080/api/v1/content/user/load-profile-info?userId=" + userId,
            {credentials: "include"}
        ).then((response) => {
            if(response.status === 200) {
                return response.json()
                    .then((json) => {
                        let userObject = {};
                        userObject.username = json.username;
                        userObject.userJoinDate = json.join_date;
                        userObject.fullName = json.full_name;
                        userObject.userDesc = json.user_desc;

                        return userObject;
                    })
            } else {
                return undefined;
            }
        })
    }

    useEffect(() => {
        let permissionDataInterval;
        let userDataInterval;
        let reportDataInterval;

        if (location.pathname === "/admin/permission") {
            permissionDataInterval = setInterval(async () => {
                let data = await getPermissions();
                if (data.success) {
                    setIsLoadingPermissionData(false)
                } else {
                    setPermissionLoadError({success: false, error: "forbidden"})
                }
            }, 5000)
        }
        if (location.pathname === "/admin/users") {
            permissionDataInterval = setInterval(async () => {
                let data = await getPermissions();
                if (data.success) {
                    setIsLoadingPermissionData(false)
                } else {
                    setPermissionLoadError({success: false, error: "forbidden"})
                }
            }, 5000)
            userDataInterval = setInterval(async () => {
                let data = await getUsers();
                if (data.success) {
                    setIsLoadingPermissionData(false)
                } else {
                    setUserLoadError({success: false, error: "forbidden"})
                }}, 5000);
        }
        if (location.pathname === "/admin/reports") {
            reportDataInterval = setInterval(async () => {
                let data = await getReportEntries();
                if (data.success) {
                    setIsLoadingReportData(false)
                } else {
                    setReportLoadError({success: false, error: "forbidden"})
                }
            }, 5000)
        }
        return () => {
            clearInterval(permissionDataInterval);
            clearInterval(userDataInterval);
            clearInterval(reportDataInterval);
        };
    }, [reportData])

    return (<ModerationContext.Provider value={{ permissionData, isLoadingPermissionData, permissionLoadError, userData, isLoadingUserData, userLoadError, reportData, reportLoadError, isLoadingReportData, submitReport, loadUserDetailsOfReport }} >
        {children}
    </ModerationContext.Provider>);
}

export function useModeration() {
    const context = useContext(ModerationContext);
    if(context === null){
        throw new Error("UserAuth must be defined");
    }
    return context;
}