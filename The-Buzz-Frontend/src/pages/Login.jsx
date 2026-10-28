import "./css/login.css"

function Login() {
    return (
        <>
            <div id="centerdiv">
                <div id="loginDiv">
                    <form method="post" id="loginForm" action="http://localhost:8080/api/v1/login">
                        <h3>Login</h3>
                        <label htmlFor="email">Email</label>
                        <input type="email" name="username"/>
                        <label htmlFor="password">Password</label>
                        <input type="password" name="password"/>
                        <button type="submit">Login</button>
                    </form>
                </div>
            </div>
        </>
    )
}

export default Login;