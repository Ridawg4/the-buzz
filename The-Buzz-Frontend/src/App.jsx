import { BrowserRouter, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import DJs from "./pages/DJs";
import "./App.css";
import Login from "./pages/Login.jsx";
import Signup from "./pages/Signup.jsx";
import React, {useState} from "react";
import {UserContext} from "./Contexts/UserContext.jsx";
import Account from "./pages/Account.jsx";

function App() {
    const [userId, setUserId] = useState(null);

    React.useEffect(() => {
        if(userId === null) {
            if(cookieStore.get("session")) {
                fetch("http://localhost:8080/api/v1/user/AuthValid",
                    {credentials: "include"})
                    .then(res => {
                            if((res.ok && !res.redirected) || res.status === 403 || res.status === 401) {
                                res.text()
                                    .then(text => {
                                            setUserId(text)
                                        }
                                    )
                            } else {
                                setUserId(null)
                            }
                        }
                    );
            }
        }
    })

  return (
		<BrowserRouter>
            <UserContext.Provider value={{ userId, setUserId }}>
			  <div className="app">

				{/* Page routes */}
				<Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/djs" element={<DJs />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />
                    <Route path="/account" element={<Account />} />
				</Routes>
			  </div>
            </UserContext.Provider>
		</BrowserRouter>
  );
}

export default App;