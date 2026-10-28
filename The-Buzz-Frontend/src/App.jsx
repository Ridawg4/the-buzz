
import { BrowserRouter, Routes, Route, Link } from "react-router-dom";
import logo from "./assets/wsbu-logo.png";
import Home from "./pages/Home";
import DJs from "./pages/DJs";
import "./App.css";
import Login from "./pages/Login.jsx";
import Navbar from "./Components/Navbar.jsx";

function App() {
  return (
    <BrowserRouter>
      <div className="app">

        <Navbar />

        {/* Page routes */}
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/djs" element={<DJs />} />
        </Routes>

      </div>
    </BrowserRouter>
  );
}

export default App;