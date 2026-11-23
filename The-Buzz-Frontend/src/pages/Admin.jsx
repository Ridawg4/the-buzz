import Navbar from "../Components/Navigation/Navbar.jsx";
import {Link} from "react-router-dom";
import {useUserAuth} from "../Contexts/UserContext.jsx";

function Admin() {
    const {isModerator} = useUserAuth();

    const isAuthorized = () => {
        if(isModerator) {
            return (
                <>
                    <Link to="/admin/permissions">Permissions</Link>
                    <Link to="/admin/users">Users</Link>
                    <Link to="/admin/reports">Reports</Link>
                </>
            )
        }
        return (<p>You are not authorized to access this resource</p>)
    }
    return (
        <>
            <Navbar />
            {isAuthorized()}
        </>)
}

export default Admin;