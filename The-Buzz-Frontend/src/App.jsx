import {BrowserRouter, Routes, Route} from "react-router-dom";
import Home from "./pages/Home";
import DJs from "./pages/DJs";
import "./App.css";
import Login from "./pages/Login.jsx";
import Signup from "./pages/Signup.jsx";
import {useState} from "react";
import {UserContextProvider} from "./Contexts/UserContext.jsx";
import Account from "./pages/Account.jsx";
import PermissionList from "./pages/Moderation/PermissionList.jsx";
import UserList from "./pages/Moderation/UserList.jsx";
import Admin from "./pages/Admin.jsx";
import ReportsList from "./pages/Moderation/ReportsList.jsx";
import {ModerationContextProvider} from "./Contexts/ModerationContext.jsx";
import {ContentContextProvider} from "./Contexts/ContentContext.jsx";

function App() {
    const [isError, setIsError] = useState(false);
    const [hasAlertedOfLogout, setHasAlertedOfLogout] = useState(false);

    // @TODO REWRITE AUTH VALIDATION OUTSIDE HERE TO USE SETINTERVAL

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

  return (
		<BrowserRouter>
            <UserContextProvider>
			  <div className="app">
                  {showLogoutError()}

				{/* Page routes */}
				<Routes>
                    <Route path="/" element={<ContentContextProvider>
                        <Home />
                    </ContentContextProvider>} />
                    <Route path="/djs" element={<ContentContextProvider>
                        <DJs />
                    </ContentContextProvider>} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/account" element={<Account />} />

                    <Route path="/admin" element={<ModerationContextProvider>
                            <Admin />
                        </ModerationContextProvider>} />
                    <Route path="/admin/permissions" element={<ModerationContextProvider>
                            <PermissionList />
                        </ModerationContextProvider>} />
                    <Route path="/admin/users" element={<ModerationContextProvider>
                            <UserList />
                        </ModerationContextProvider>} />
                    <Route path="/admin/reports" element={<ModerationContextProvider>
                            <ReportsList />
                        </ModerationContextProvider>} />
				</Routes>
			  </div>
            </UserContextProvider>
		</BrowserRouter>
  );
}

export default App;