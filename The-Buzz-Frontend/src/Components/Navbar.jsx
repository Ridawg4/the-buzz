import {Link} from "react-router-dom";
import logo from "../assets/wsbu-logo.png";
import {UserContext} from "../Contexts/UserContext.jsx";
import {useContext} from "react";
import AccountNavbarDropdown from "./AccountNavbarDropdown.jsx";

function Navbar() {
    const { userId, setUserId } = useContext(UserContext);

    return (
        <nav className="navbar">

        {/* Left navigation */}
        <div className="nav-left">
            <Link to="/news">News</Link>
            <Link to="/schedule">Show Schedule</Link>
            <Link to="/djs">Explore DJs</Link>
        </div>

        {/* Center logo - Home */}
        <Link to="/" className="nav-logo">
            <img src={logo} alt="WSBU Home" />
        </Link>

        {/* Right navigation */}
        <div className="nav-right">
            <Link to="/shows">Explore Shows</Link>
            <Link to="/authors">Explore Authors</Link>
            <Link to="/favorites">Favorites</Link>
            <AccountNavbarDropdown />
        </div>

    </nav>
)}

export default Navbar;