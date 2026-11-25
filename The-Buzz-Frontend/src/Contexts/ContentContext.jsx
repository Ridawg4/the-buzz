import {createContext, useContext, useEffect, useState} from "react";
import {useUserAuth} from "./UserContext.jsx";

const ContentContext = createContext(null);

export function ContentContextProvider({ children }) {
    // const [permissionData, setPermissionData] = useState([]);
    // const [permissionLoadError, setPermissionLoadError] = useState({});
    // const [isLoadingPermissionData, setIsLoadingPermissionData] = useState(true);

    // const { isModerator, userId } = useUserAuth();



    // useEffect(() => {
    //
    //
    //     return () => {
    //
    //     };
    // }, [])

    return (<ContentContext.Provider value={{  }} >
        {children}
    </ContentContext.Provider>);
}

export function useContent() {
    const context = useContext(ContentContext);
    if(context === null){
        throw new Error("UserAuth must be defined");
    }
    return context;
}