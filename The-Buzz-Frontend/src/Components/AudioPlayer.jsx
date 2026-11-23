import {useState} from "react";

function AudioPlayer() {
    const [isPlaying, setIsPlaying] = useState(false);

    return (<button onClick={() => setIsPlaying(!isPlaying)}>
        {isPlaying ? "Pause" : "▶ Play Radio"}
    </button>)
}

export default AudioPlayer;