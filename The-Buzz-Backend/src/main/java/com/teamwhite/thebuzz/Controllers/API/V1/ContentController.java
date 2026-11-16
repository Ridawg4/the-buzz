package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Exceptions.InvalidAuthenticationPrincipalException;
import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Resources.ResourcePaths;
import com.teamwhite.thebuzz.Services.LocalStorage;
import com.teamwhite.thebuzz.Services.ReportService;
import com.teamwhite.thebuzz.model.ActiveReport;
import com.teamwhite.thebuzz.model.BuzzUser;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ReportService reportService;

    public ContentController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/report/report-content")
    public ResponseEntity<String> reportPost(@RequestParam String contentType,
                                             @RequestParam(required = false) String contentUUID,
                                             @RequestParam(required = false) String contentId,
                                             @RequestParam(required = false) String reporter,
                                             @RequestParam String reason,
                                             Authentication userAuth) {
        if(contentUUID == null && contentId == null) {
            ResponseEntity.badRequest().build();
        }
        ActiveReport receivedReport = null;

        if(reporter != null) {
            BuzzUser user = null;
            try {
                user = (BuzzUser) userAuth.getPrincipal();
            } catch (InvalidAuthenticationPrincipalException exc) {
                ResponseEntity.badRequest().build();
            }

            // If the user doesn't have the correct permissions, they are unauthorized
            assert user != null;
            if(contentUUID != null) {
                receivedReport = new ActiveReport(contentType, contentUUID, user.getUserId(), reason);
            } else {
                receivedReport = new ActiveReport(contentType, contentId, user.getUserId(), reason);
            }

        } else {
            if(contentUUID != null) {
                receivedReport = new ActiveReport(contentType, contentUUID, reason);
            } else {
                receivedReport = new ActiveReport(contentType, contentId, reason);
            }
        }

        reportService.createReport(receivedReport);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("audio/live")
    public ResponseEntity<Resource> getAudio() throws MalformedURLException {
        ResponseEntity<Resource> resp;

        /* An Optional<T> is a wrapper you can create to alert the programmer that an object
          or may not be present inside. This is a slightly nicer way to handle null objects */
        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.LIVE_SEGMENTS_DIRECTORY, "live.m3u8");

        if(resourceOptional.isPresent()) {
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
            // HTTP standard indicate that .m3u8 files are returned with the type shown below
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.M3U8);

        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Return the generated request
        return resp;
    }

    @GetMapping("audio/{filename}")
    public ResponseEntity<Resource> getLiveAudioSegment(@PathVariable String filename) throws MalformedURLException {
        ResponseEntity<Resource> resp;

        // Creates the Optional<T> received from local storage
        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.LIVE_SEGMENTS_DIRECTORY, filename);

        if(resourceOptional.isPresent()) {
            // If the Optional<T> is not empty, send the file contained inside
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.MP3);

        } else {
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Return the generated request
        return resp;
    }

    @GetMapping("audio/{resource}/{filename}")
    public ResponseEntity<Resource> getAudio(@PathVariable String filename, @PathVariable String resource) throws MalformedURLException {
        ResponseEntity<Resource> resp;

        // Creates the Optional<T> received from local storage
        Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(
                ResourcePaths.TEMP_DIRECTORY + resource + ResourcePaths.SEPARATOR
                        + "Segments" + ResourcePaths.SEPARATOR, filename);

        if(resourceOptional.isPresent()) {
            // If the Optional<T> is not empty, send those files
            resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
            // Required by HTTP HLS standard, clients deny loading the files without this tag
            resp.getHeaders().add(HttpHeaders.CONTENT_TYPE, ContentTypes.M3U8);
        } else {
            // If file is not present, alert client of such
            resp = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return resp;
    }
}
