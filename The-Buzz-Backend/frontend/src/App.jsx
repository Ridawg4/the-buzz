
import { useState } from "react";
import logo from "./assets/wsbu-logo.png";
import "./App.css";

function App() {
  const [isPlaying, setIsPlaying] = useState(false);

  return (
    <div className="app">

      {/* Navigation bar */}
      <nav className="navbar">

        <div className="nav-left">
          <a href="#listen">Listen Live</a>
          <a href="#schedule">Schedule</a>
        </div>

        <a href="#home" className="nav-logo">
          <img src={logo} alt="WSBU Home" />
        </a>

        <div className="nav-right">
          <a href="#djs">DJs</a>
          <a href="#login">Login</a>
        </div>

      </nav>

      {/* Homepage */}
      <section id="home" className="hero">
        <h1>Welcome to The Buzz!</h1>
        <p>St. Bonaventure University's Radio Station</p>
        <span>YOUR MUSIC. YOUR STATION.</span>
      </section>

      {/* Radio player */}
      <section id="listen" className="radio-player">
        <h2>Listen Live</h2>
        <p>WSBU 88.3 FM</p>

        <button onClick={() => setIsPlaying(!isPlaying)}>
          {isPlaying ? "Pause" : "▶ Play Radio"}
        </button>
      </section>

      {/* Schedule */}
      <section id="schedule" className="content-section">
        <h2>Upcoming Shows</h2>
        <p>Check out our radio schedule!</p>
      </section>

      {/* DJs */}
      <section id="djs" className="content-section">
        <h2>Meet Our DJs</h2>
        <p>Get to know the voices behind The Buzz.</p>
      </section>

    </div>
  );
}

export default App;