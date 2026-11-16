import Navbar from "../Components/Navbar.jsx";
import {Link} from "react-router-dom";

function Admin() {

    return (
        <>
            <Navbar />
            <Link to="/admin/permissions">Permissions</Link>
            <Link to="/admin/users">Users</Link>
            <Link to="/admin/reports">Reports</Link>
        </>
    )
}

export default Admin;