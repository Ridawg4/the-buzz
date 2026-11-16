package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Exceptions.InvalidAuthenticationPrincipalException;
import com.teamwhite.thebuzz.Services.ReportService;
import com.teamwhite.thebuzz.model.ActiveReport;
import com.teamwhite.thebuzz.model.BuzzUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
