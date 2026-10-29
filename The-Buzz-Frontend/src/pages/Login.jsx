import "./css/login.css"
import {useState} from "react";

function Login() {
    const [error, setError] = useState(false);
    const onLogin = (event) => {
        event.preventDefault();
        const form = document.getElementById("loginForm");
        fetch("http://localhost:8080/api/v1/login", {
            method: "POST",
            body: new FormData(form),
            credentials: "include"
        }).then(response => {
            if(response.ok){
                window.location.replace("/")
            }
            if(response.status === 401) {
                setError(true)
            }
        });
    }

    return (
        <>
            <div id="centerdiv">
                <div id="loginDiv">
                    {error && <p>'Credentials Do Not Match Records'</p>}
                    <form id="loginForm" onSubmit={e => e.preventDefault}>
                        <h3>Login</h3>
                        <label htmlFor="email">Email</label>
                        <input type="email" name="username"/>
                        <label htmlFor="password">Password</label>
                        <input type="password" name="password"/>
                        <button onClick= {onLogin}>Login</button>
                    </form>
                </div>
            </div>
        </>
    )
}

export default Login;