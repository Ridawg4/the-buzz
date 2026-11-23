import {createContext, useContext, useEffect, useState} from "react";

const UserContext = createContext(null);

export function UserContextProvider({ children }) {
    const [userId, setUserId] = useState(null);
    const [ isDj, setIsDj ] = useState(false);
    const [ isModerator, setIsModerator ] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [isLoggingOut, setIsLoggingOut] = useState(false);

const checkUserAuth = async () => {
        if(location.pathname !== "/login" && location.pathname !== "/signup") {
            if(!isLoggingOut) {
                return await fetch("http://localhost:8080/api/v1/user/AuthValid",
                    {credentials: "include", redirect: "error"})
                    .then(async res => {
                            if ((res.ok)) {
                                res.text()
                                    .then(text => {
                                        if (userId === null) {
                                            setUserId(text)
                                            hasDjRole()
                                            hasAdminRole()
                                            setIsLoading(false)
                                        } else {
                                            setUserId(text)
                                        }
                                    }).catch((e) => {
                                    setUserId(null)
                                    setIsLoading(false)
                                })
                                if (userId !== null) {
                                    return {success: false}
                                }
                                return {success: true}
                            }
                            if (res.status === 401) {
                                setUserId(null)
                                setIsLoading(false)
                                return {success: false}
                            }
                        }
                    ).catch((e) => {
                        setUserId(null)
                        setIsLoading(false)
                        return {success: false}
                    })
            }
        } else {
            setIsLoggingOut(false)
            return {success: false}
        }
    }

    const login = async (form) => {
        return await fetch("http://localhost:8080/api/v1/login", {
            method: "POST",
            body: new FormData(form),
            credentials: "include"
        }).then(response => {
            if(response.ok){
                return response.text()
                    .then(text => {
                        setUserId(text)
                        hasDjRole()
                        hasAdminRole()
                        return {success: true};
                    })
            }
            if(response.status === 401) {
                return {success: false,
                    error: "Credentials do not match"
                };
            }
            if(response.status === 403) {
                return response.json()
                    .then((data) => {
                        if(data.error_code === "UNACK-ACC-ACTIONS") {
                            return {success: false,
                                error: "Account is banned",
                                errorType: "UNACK-ACC-ACTIONS",
                            actions: data.actions};
                        } else {
                            return {success: false,
                                error: "Unknown auth error"};
                        }
                    })
            }
            return {success: false,
                error: "Unknown auth error"};
        });
    }

    const signup = (signupForm) => {
        fetch("http://localhost:8080/api/v1/signup", {
            method: "POST",
            body: signupForm
        }).then(res => {
            console.log(res)
            if (res.status !== 201) {
                // setGlobalError(true);
            } else {
                // directToLogin()
            }
        })
    }

    const checkIfEmailIsNew = () => {
        const emailField = document.getElementById("email")
        fetch("http://localhost:8080/api/v1/signup/emailExist?email=" + emailField.value)
            .then(res => {
                console.log(res)
                if (res.status === 409) {
                    // setEmailError(true);
                }
                if (res.status === 401) {
                    // setEmailError(true);
                }
                if (res.status === 204) {
                    // setEmailError(false);
                }
            })
    }

    const logout = ((event) => {
        event.preventDefault()
        setIsLoggingOut(true)
        setIsModerator(false)
        setIsDj(false)
        fetch("http://localhost:8080/api/v1/logout", {
            credentials: "include",
            redirect: 'error'
        }).catch(error => {
            setUserId(null);
            if(location.pathname === "/account") {
                // navigate("/login")
            }
            if(location.pathname === "/admin") {
                // navigate("/login")
            }
        })
    })

    const hasAdminRole = async () => {
            fetch("http://localhost:8080/api/v1/user/HasPermission?permissionToLookFor=CAN_SEE_ALL_SYSTEM_PANELS",
                {credentials: "include"}
            ).then(response => {
                if (response.ok) {
                    response.text().then(text => {
                        if (text === "allowed") {
                            setIsModerator(true)
                        } else {
                            setIsModerator(false)
                        }
                    })
                }
            })
    }

    const hasDjRole = async () => {
            fetch("http://localhost:8080/api/v1/user/HasPermission?permissionToLookFor=CAN_SEE_DJ_SYSTEM_PANELS",
                {credentials: "include"}
            ).then(response => {
                if (response.ok) {
                    response.text().then(text => {
                        if (text === "allowed") {
                            setIsDj(true)
                        } else {
                            setIsDj(false)
                        }
                    })
                }
            })
    }

    const deleteAccount = async () => {
        return await fetch("http://localhost:8080/api/v1/user/delete-account?approved=true", {
            method: "POST",
            credentials: "include"
        }).then(res => {
            if(res.status === 200) {
                setUserId(null)
                return {success: true}
            }
            if(res.status === 401) {
                setUserId(null)
                return {success: false,
                error: "User is not authenticated"}
            }
            if(res.status === 400) {
                let error = res.text().then(text => {
                    return text;
                })
                return {success: false,
                    error: "Bad request | " + error}
            }
            if(res.status === 500) {
                return {success: false,
                    error: "Internal Server Error"}
            }
        })
    }

    const loadUserDetails = async () => {
        return await fetch("http://localhost:8080/api/v1/user/load-details",
            {credentials: "include",}
        ).then((response) => {
            if (response.status === 403) {
                return {
                    success: false,
                    error: "user is not authorized to access this information"
                };
            }
            if (response.status === 401) {
                return {
                    success: false,
                    error: "user is not authenticated"
                };
            }
            if (response.status === 500) {
                return {
                    success: false,
                    error: "internal server error"
                };
            }
            return response.json()
                .then((json) => {
                    if (json === null) {
                        return {
                            success: false,
                            error: "No details found"
                        }
                    }

                    return {
                        username: json.username,
                        email: json.email,
                        join_date: json.join_date,
                        full_name: json.full_name,
                        user_desc: json.user_desc
                    };
                })
        })
    }


    useEffect(() => {
        checkUserAuth();
        let userAuthInterval = setInterval(checkUserAuth, 5000)
        let hasDjInterval = setInterval(hasDjRole, 30000)
        let hasAdminInterval = setInterval(hasAdminRole, 30000)

        return () => {
            clearInterval(userAuthInterval);
            clearInterval(hasDjInterval);
            clearInterval(hasAdminInterval);
        };
    }, [])

return (
    <UserContext.Provider value={{userId, isDj, isModerator, login, signup, checkIfEmailIsNew, logout, deleteAccount, loadUserDetails}} >
        {children}
    </UserContext.Provider>
);
}

export function useUserAuth() {
    const context = useContext(UserContext);
    if(context === null){
        throw new Error("UserAuth must be defined");
    }
    return context;
}