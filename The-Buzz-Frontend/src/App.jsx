import {BrowserRouter, Routes, Route} from "react-router-dom";
import Home from "./pages/Home";
import DJs from "./pages/DJs";
import "./App.css";
import Login from "./pages/Login.jsx";
import Signup from "./pages/Signup.jsx";
import React, {useState} from "react";
import {UserContext} from "./Contexts/UserContext.jsx";
import Account from "./pages/Account.jsx";
import PermissionsEdit from "./Components/PermissionsEdit.jsx";
import PermissionList from "./pages/PermissionList.jsx";
import UserList from "./pages/UserList.jsx";
import Admin from "./pages/Admin.jsx";
import ReportListItem from "./Components/ReportListItem.jsx";
import ReportsList from "./pages/ReportsList.jsx";

function App() {
    const [userId, setUserId] = useState(null);
    const [isError, setIsError] = useState(false);
    const [hasAlertedOfLogout, setHasAlertedOfLogout] = useState(false);

    const checkUserAuth = () => {
            fetch("http://localhost:8080/api/v1/user/AuthValid",
                {credentials: "include", redirect: "error"})
                .then(res => {
                        if((res.ok)) {
                            res.text()
                                .then(text => {
                                    if(userId === null) {
                                        setIsError(false)
                                        setHasAlertedOfLogout(false)
                                        setUserId(text)
                                    }
                                }).catch((e) => {
                                setUserId(null)
                            })
                        } if (res.status === 401) {
                        setUserId(null)
                        }
                    }
                ).catch((e) => {
                setUserId(null)
                if (hasAlertedOfLogout) {
                    setIsError(true)
                    setTimeout(setErrorFalse, 5000, []);
                    setHasAlertedOfLogout(true)
                }
            })
    }

    const setErrorFalse = () => {
        setIsError(false);
    }

    const showLogoutError = () => {
        if(isError) {
            if (!hasAlertedOfLogout) {
                return (
                    <div className="alert critical" role="alert">
                        <p>You have been logged out!</p>
                    </div>
                )
            }
        }
    }

    React.useEffect(() => {
        checkUserAuth()
        setInterval(checkUserAuth, 30000, [])
    })

  return (
		<BrowserRouter>
            <UserContext.Provider value={{ userId, setUserId }}>
			  <div className="app">
                  {showLogoutError()}

				{/* Page routes */}
				<Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/djs" element={<DJs />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/account" element={<Account />} />

                    <Route path="/admin" element={<Admin />} />
                    <Route path="/admin/permissions" element={<PermissionList />} />
                    <Route path="/admin/users" element={<UserList />} />
                    <Route path="/admin/reports" element={<ReportsList />} />
				</Routes>
			  </div>
            </UserContext.Provider>
		</BrowserRouter>
  );
}

export default App;