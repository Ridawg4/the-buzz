
import "./DJs.css";

// Example DJ data - replace with real information later
const liveDJs = [
  {
    id: 1,
    name: "DJ Name",
    shows: ["Morning Buzz", "The Mix"],
    genre: "Pop / Indie",
  },
  {
    id: 2,
    name: "DJ Name",
    shows: ["Late Night Buzz", "Rock Hour"],
    genre: "Rock / Alternative",
  },
];

const allDJs = [
  {
    id: 3,
    name: "DJ Name",
    shows: ["Show 1", "Show 2", "Show 3"],
    genre: "Hip-Hop",
  },
  {
    id: 4,
    name: "DJ Name",
    shows: ["Show 1", "Show 2"],
    genre: "Indie",
  },
  {
    id: 5,
    name: "DJ Name",
    shows: ["Show 1", "Show 2"],
    genre: "Pop",
  },
  {
    id: 6,
    name: "DJ Name",
    shows: ["Show 1", "Show 2"],
    genre: "Alternative",
  },
  {
    id: 7,
    name: "DJ Name",
    shows: ["Show 1", "Show 2"],
    genre: "Rock",
  },
  {
    id: 8,
    name: "DJ Name",
    shows: ["Show 1", "Show 2"],
    genre: "Electronic",
  },
];

// Reusable DJ card
function DJCard({ dj, live = false }) {
  return (
    <article className="dj-card">
      <div className="dj-photo">
        <span>WSBU</span>
      </div>

      <div className="dj-info">
        {live && (
          <span className="live-badge">● LIVE NOW</span>
        )}

        <h3>{dj.name}</h3>
        <p className="dj-genre">{dj.genre}</p>

        <p className="shows-label">Shows:</p>
        <ul className="dj-shows">
          {dj.shows.map((show, index) => (
            <li key={index}>{show}</li>
          ))}
        </ul>
      </div>
    </article>
  );
}

function DJs() {
  return (
    <main className="djs-page">

      <section className="djs-section">
        <h1>Currently Live DJs</h1>

        <div className="djs-grid live-grid">
          {liveDJs.map((dj) => (
            <DJCard key={dj.id} dj={dj} live />
          ))}
        </div>
      </section>

      <hr className="section-divider" />

      <section className="djs-section">
        <h2>All DJs</h2>

        <div className="djs-grid all-grid">
          {allDJs.map((dj) => (
            <DJCard key={dj.id} dj={dj} />
          ))}
        </div>
      </section>

    </main>
  );
}

export default DJs;
