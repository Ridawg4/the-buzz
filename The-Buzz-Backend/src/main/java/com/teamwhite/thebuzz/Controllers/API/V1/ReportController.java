package com.teamwhite.thebuzz.Controllers.API.V1;

import com.teamwhite.thebuzz.Services.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping()
    @PreAuthorize("hasAuthority('CAN_SEE_ALL_SYSTEM_PANELS')")
    public ResponseEntity<String> getAllReports(@RequestParam(required = false) String reportId) {
        if(reportId != null) {
            try {
                return new ResponseEntity<>(reportService.getReportByReportId(Integer.parseInt(reportId)).toString(), HttpStatus.OK);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Provided reportId does not match any reports", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(reportService.getAllReports().toString(), HttpStatus.OK);
    }
}
