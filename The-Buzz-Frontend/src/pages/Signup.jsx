import {useState} from "react";
import {useNavigate} from "react-router-dom";

function Signup() {
    const [emailError, setEmailError] = useState(false);
    const [passwordMatchError, setPasswordMatchError] = useState(false);
    const [globalError, setGlobalError] = useState(false);
    const navigate = useNavigate();

    const onSignup = (event) => {
        event.preventDefault();

        const emailField = document.getElementById("email")
        const passwordField = document.getElementById("password")
        const usernameField = document.getElementById("username")
        let signupForm = new FormData();

        signupForm.append("email", emailField.value);
        signupForm.append("username", usernameField.value);
        signupForm.append("password", passwordField.value);

        fetch("http://localhost:8080/api/v1/signup", {
            method: "POST",
            body: signupForm
        }).then(res => {
            console.log(res)
            if (res.status !== 201) {
                setGlobalError(true);
            } else {
                directToLogin()
            }
        })
    };

    const checkIfEmailIsNew = () => {
        const emailField = document.getElementById("email")
        fetch("http://localhost:8080/api/v1/signup/emailExist?email=" + emailField.value)
            .then(res => {
                console.log(res)
                if (res.status === 409) {
                    setEmailError(true);
                }
                if (res.status === 401) {
                    setEmailError(true);
                }
                if (res.status === 204) {
                    setEmailError(false);
                }
            })
    }

    const verifyPasswordsMatch = () => {
        const passwordField = document.getElementById("password")
        const verifyPasswordField = document.getElementById("verifyPassword")

        if(passwordField.value === verifyPasswordField.value) {
            setPasswordMatchError(false);
        } else {
            setPasswordMatchError(true);
        }
    }

    const directToLogin = () => {
        navigate("/login");
    }

    return (
        <>
            <div id="centerdiv">
                <div id="loginDiv">
                    {globalError && <p>'An error occurred upon submitting your request'</p>}
                    <form onSubmit = {e => e.preventDefault}>
                        <h3>Sign Up</h3>
                        {emailError && <p>'Email already exists'</p>}
                        <label htmlFor="email">Choose an Email</label>
                        {/* Why does React choose to use onInput() for onChange,
                        practically a self-inflicted DOS waiting to happen
                        This is the better way to do this though, so*/}
                        <input id="email" onChange={checkIfEmailIsNew} type="email" name="username"/>
                        <label htmlFor="username">Create a Username</label>
                        <input id = "username" type="text" name="username"/>
                        <label htmlFor="password">Create a Password</label>
                        <input type="password" name="password" id="password"/>
                        {passwordMatchError && <p>'Passwords do not match'</p>}
                        <label htmlFor="password">Retype Your Password</label>
                        <input id="verifyPassword" type="password" name="password" onChange = {verifyPasswordsMatch}/>
                        <button onClick = {onSignup}>Sign Up</button>
                    </form>
                </div>
                <a onClick = {directToLogin}>Login</a>
                <a onClick = {() => navigate("/")}>Back to Home</a>
            </div>
        </>
    )
}

export default Signup;