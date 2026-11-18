package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Exceptions.InvalidAuthenticationPrincipalException;
import com.teamwhite.thebuzz.Exceptions.NoUserFoundException;
import com.teamwhite.thebuzz.Records.ContentTypes;
import com.teamwhite.thebuzz.Resources.ResourcePaths;
import com.teamwhite.thebuzz.Services.AccountModerationService;
import com.teamwhite.thebuzz.Services.LocalStorage;
import com.teamwhite.thebuzz.Services.ReportService;
import com.teamwhite.thebuzz.Services.UserService;
import com.teamwhite.thebuzz.model.AccountModeration;
import com.teamwhite.thebuzz.model.ActiveReport;
import com.teamwhite.thebuzz.model.BuzzUser;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ReportService reportService;
    private final UserService userService;
    private final AccountModerationService accountModerationService;

    public ContentController(ReportService reportService, UserService userService, AccountModerationService accountModerationService) {
        this.reportService = reportService;
        this.userService = userService;
        this.accountModerationService = accountModerationService;
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

    @GetMapping("user/load-profile-info")
    public ResponseEntity<String> loadUserDetails(@RequestParam String userId) {
        if(userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            BuzzUser dbUser = userService.getUserRecordByUserId(userId);
            ResponseEntity<String> response = new ResponseEntity<>(dbUser.getAccountDetailsWithoutSensitiveInfo().toString(),
                    HttpStatus.OK);
            response.getHeaders().add("Content-Type", ContentTypes.JSON);

            return response;
        } catch (NoUserFoundException exc) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("user/load-profile-picture")
    public ResponseEntity<Resource> loadProfilePicture(@RequestParam String userId) {
        ResponseEntity<Resource> resp;
        if(userId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            String pathToPfp = userService.getUserRecordByUserId(userId).getUserPfpLocation();
            Optional<Resource> resourceOptional = LocalStorage.retrieveFileFromLocalStorage(pathToPfp);

            if(resourceOptional.isPresent()) {
                resp = new ResponseEntity<>(resourceOptional.get(), HttpStatus.OK);
                return resp;
            }
        } catch (IOException exc) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/action/acknowledge")
    public ResponseEntity<String> acknowledgeAction(@RequestParam String userId,
                                                    @RequestParam String reportId,
                                                    @RequestParam boolean isAcknowledged) {
        AccountModeration moderationEntry = null;
        Logger.getGlobal().info(reportId);
        try {
            moderationEntry = accountModerationService.getModerationById(Integer.parseInt(reportId));
        } catch (IllegalArgumentException exc) {
            return ResponseEntity.badRequest().body("ReportId does not match any account actions");
        }
        assert moderationEntry != null;
        if(LocalDateTime.now().isBefore(moderationEntry.getDateExpires().toLocalDateTime())) {
            return ResponseEntity.badRequest().body("Action cannot be acknowledged before it expires");
        }

        accountModerationService.acknowledgeAccountModeration(Integer.parseInt(reportId), true);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
