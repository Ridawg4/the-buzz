package com.teamyellow.thebuzz.Controllers;

import com.teamyellow.thebuzz.Records.Audio;
import com.teamyellow.thebuzz.Services.LocalStorage;
import com.teamyellow.thebuzz.Services.M3U8Encoder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioEncoderController {

    @PostMapping()
    public ResponseEntity<String> uploadAudioClip(@RequestParam("file") MultipartFile file) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(Level.INFO, file.getContentType());
        LocalStorage.addToTempStorage(file);
        ResponseEntity<String> resp = new ResponseEntity<>(HttpStatus.ACCEPTED);

        return resp;
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename) throws MalformedURLException {
        final String LOCAL_DIRECTORY = System.getenv("PWD");
        URI filePath = Path.of(LOCAL_DIRECTORY + File.separator
                + "TEMP" + File.separator + filename).toUri();

        Resource file = new UrlResource(filePath);
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(file.getFilename());

        M3U8Encoder.createPlaylist(filePath);

        ResponseEntity<Resource> resp = new ResponseEntity<>(file, HttpStatus.OK);


        return resp;
    }
}
