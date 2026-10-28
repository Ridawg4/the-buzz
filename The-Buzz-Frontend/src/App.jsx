
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import logo from "./assets/wsbu-logo.png";
import Home from "./pages/Home";
import DJs from "./pages/DJs";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <div className="app">

	  
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