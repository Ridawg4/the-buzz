import { useState } from "react";

function Home() {
  const [isPlaying, setIsPlaying] = useState(false);

  return (
    <>
      <section id="home" className="hero">
        <h1>Welcome to The Buzz!</h1>
        <p>St. Bonaventure University's Radio Station</p>
        <span>YOUR MUSIC. YOUR STATION.</span>
      </section>

      <section id="listen" className="radio-player">
        <h2>Listen Live</h2>
        <p>WSBU 88.3 FM</p>

        <button onClick={() => setIsPlaying(!isPlaying)}>
          {isPlaying ? "Pause" : "▶ Play Radio"}
        </button>
      </section>

      <section id="schedule" className="content-section">
        <h2>Upcoming Shows</h2>
        <p>Check out our radio schedule!</p>
      </section>
    </>
  );
}

export default Home;