import "./css/login.css"
import {useEffect, useState} from "react";
import {useUserAuth} from "../Contexts/UserContext.jsx";
import {useNavigate} from "react-router-dom";
import AccountModerationNoticePopup from "../Components/AccountPage/AccountModerationNoticePopup.jsx";

function Login() {
    const { login, userIdFromAction, setLoggingIn } = useUserAuth();
    const [accountActions, setAccountActions] = useState([]);
    const [isAccountAction, setIsAccountAction] = useState(false);
    const navigate = useNavigate();

    const [error, setError] = useState(false);

    const onLogin = async (event) => {
        event.preventDefault();
        const form = document.getElementById("loginForm");

        let results = await login(form)
        console.log(results)

            if (results.success) {
                 directTo("/")
            }
            if(results.error) {
                if (results.errorType === "UNACK-ACC-ACTIONS") {
                    setAccountActions(results.actions);
                    setIsAccountAction(true)
                } else {
                setError(true);
                }
            }
    }

    const directTo = (path) => {
        navigate(path);
    }

    const returnHome = () => {
        directTo("/");
    }

    const returnToLogin = () => {
        setIsAccountAction(false)
    }

    const isAccountActionReceived = () => {
        if(isAccountAction) {
            return (
                    <div className="centerdiv">
                        <h1>Your Account has Unacknowledged Moderation Actions!</h1>
                        <h3>You must acknowledge these actions to continue to use your account.</h3>
                        <div className="ActionList">
                            {accountActions.map((actions, index) => (
                                <AccountModerationNoticePopup actions = {JSON.parse(actions)} userId = {userIdFromAction}  key={index} />
                            ))}
                        </div>
                        <div className="moderation-navigation">
                            <button onClick={returnHome}>Back To Home</button>
                            <button onClick={returnToLogin}>Return to Login</button>
                        </div>
                    </div>
            )
        } else {
            return (
                <>
                    <div id="centerdiv">
                        <div id="loginDiv">
                            <h3 className="FormTitle">Welcome Back!</h3>
                            {error && <p>'Credentials Do Not Match Records'</p>}
                            <form id="loginForm" onSubmit={e => e.preventDefault}>
                                <label htmlFor="email">Email</label>
                                <input type="email" name="username"/>
                                <label htmlFor="password">Password</label>
                                <input type="password" name="password"/>
                                <button onClick= {onLogin}>Login</button>
                            </form>
                            <div className="navSignupOrBack">
                                <a onClick = {() => navigate("/")}>Back to Home</a>
                                <p>Don't have an account? <a onClick = {() => navigate("/signup")}>Signup</a></p>
                            </div>
                        </div>
                    </div>
                </>
            )
        }
    }

    return (isAccountActionReceived())
}

export default Login;