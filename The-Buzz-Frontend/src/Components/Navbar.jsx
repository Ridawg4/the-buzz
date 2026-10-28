import {Link} from "react-router-dom";
import logo from "../assets/wsbu-logo.png";

function Navbar() {
{/* Navigation bar */}
return (
<nav className="navbar">
    <div className="navbar-center-objects">
        <Link to="/#listen">Listen Live</Link>
        <Link to="/#schedule">Schedule</Link>
    {/* Logo takes users to homepage */}
    <Link to="/" className="nav-logo">
        <img src={logo} alt="WSBU Home"/>
    </Link>
        <Link to="/djs">DJs</Link>
        <Link to="/login">Login</Link>
    </div>
</nav>
)
}

export default Navbar;