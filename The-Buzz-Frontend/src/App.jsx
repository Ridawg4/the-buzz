
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import logo from "./assets/wsbu-logo.png";
import Home from "./pages/Home";
import DJs from "./pages/DJs";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <div className="app">

        {/* Navigation bar */}
        <nav className="navbar">

          <div className="nav-left">
            <Link to="/#listen">Listen Live</Link>
            <Link to="/#schedule">Schedule</Link>
          </div>

          {/* Logo takes users to homepage */}
          <Link to="/" className="nav-logo">
            <img src={logo} alt="WSBU Home" />
          </Link>

          <div className="nav-right">
            <Link to="/djs">DJs</Link>
            <Link to="/login">Login</Link>
          </div>

        </nav>

        {/* Page routes */}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/djs" element={<DJs />} />
        </Routes>

      </div>
    </BrowserRouter>
  );
}

export default App;