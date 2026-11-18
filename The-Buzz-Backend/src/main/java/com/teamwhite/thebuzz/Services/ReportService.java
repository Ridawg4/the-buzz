package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.ActiveReportRepository;
import com.teamwhite.thebuzz.Repositories.HandledReportRepositories;
import com.teamwhite.thebuzz.model.ActiveReport;
import com.teamwhite.thebuzz.model.HandledReport;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@Service
public class ReportService {
    private final ActiveReportRepository activeReportRepository;
    private final HandledReportRepositories handledReportRepository;
    private final AccountModerationService accountModerationService;

    public ReportService(ActiveReportRepository activeReportRepository, HandledReportRepositories handledReportRepository, AccountModerationService accountModerationService) {
        this.activeReportRepository = activeReportRepository;
        this.handledReportRepository = handledReportRepository;
        this.accountModerationService = accountModerationService;
    }

    public JsonObject getAllReports() {
        List<Object[]> entries = activeReportRepository.getAllReports();
        ActiveReport[] reports = new ActiveReport[entries.size()];
        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
        JsonObjectBuilder objWrapper = Json.createObjectBuilder();

        for(int i = 0; i < entries.size(); i++) {
            Object[] currObject = entries.get(i);

            if(currObject[2] != null) {
                reports[i] = new ActiveReport((int) currObject[0], (String) currObject[1], (String) currObject[2],
                        (String) currObject[4], (String) currObject[5], (LocalDateTime) currObject[6]);
            } else {
                reports[i] = new ActiveReport((int) currObject[0], (String) currObject[1], (int) currObject[3],
                        (String) currObject[4], (String) currObject[5], (LocalDateTime) currObject[6]);
            }
        }

        for(ActiveReport report : reports) {
            arrBuilder.add(report.asJsonObject());
        }

        objWrapper.add("reports", arrBuilder.build());

        return objWrapper.build();
    }

    public JsonObject getReportByReportId(int reportId) {
        List<Object[]> entries = activeReportRepository.getReportById(reportId);

        if(entries.isEmpty()) {
            throw new IllegalArgumentException("No Active Report exists at that ID");
        }

        if(entries.size() > 1) {
            throw new IllegalArgumentException("Database somehow has more than one Active Report at ID");
        }
        Object[] currObject = entries.get(0);

        if(currObject[2] != null) {
            return new ActiveReport((int) currObject[0], (String) currObject[1], (String) currObject[2],
                    (String) currObject[4], (String) currObject[5], (LocalDateTime) currObject[6]).asJsonObject();
        } else {
             return new ActiveReport((int) currObject[0], (String) currObject[1], (int) currObject[3],
                    (String) currObject[4], (String) currObject[5], (LocalDateTime) currObject[6]).asJsonObject();
        }
    }

    public void createReport(ActiveReport report) {
        activeReportRepository.createReport(report.getContentType(), report.getContentUuidId(), report.getContentIntId(), report.getReporter(), report.getReasonForReporting(), report.getDateReported());
    }

    public void createHandledReport(HandledReport report, String unbanDate) {
        /**
         * @TODO METHOD TO NOTIFY USERS OF ACCOUNT ACTIONS
         *
         * USE SuccessAuthenticationHandler and check if the user has account actions not labeled
         * as acknowledged yet and show them
         *
         * For account actions, force all sessions on next request to logout, forcing them
         * to see the account action
         *
         * For account deletions, we can ban the username by leaving that value in the users table
         * but removing all other values associated with it, so no accounts can be created with that email
         *
         * Make the front end not call delete, instead, make it call a report endpoint for bans, since a
         * permanent ban is the same as deleting their account
         *
         * Make the front end upon noticing an unrecognized account action force the user to acknowledge it
         * or log out. If they try to ignore it, make sure they are logged out and their session is deleted
         */
        ActiveReport originalReport = new ActiveReport(report.getOriginalReport());
        if(originalReport.getContentType().equals("user-account")) {
            if (report.getAction().equals("warn")) {
                accountModerationService.createAccountModeration(originalReport.getContentUuidId(), report.getExplanation(), report.getHandlerId(), report.getAction());
            }
            if (report.getAction().equals("edit")) {
                accountModerationService.createAccountModeration(originalReport.getContentUuidId(), report.getExplanation(), report.getHandlerId(), report.getAction());
            }
            if (report.getAction().equals("ban")) {
                if (unbanDate == null) {
                    throw new RuntimeException("Ban cannot have unbanDate of null");
                }
                LocalDateTime unbanDateTime = LocalDateTime.parse(unbanDate, DateTimeFormatter.ISO_DATE_TIME);
                accountModerationService.createAccountModeration(originalReport.getContentUuidId(), report.getExplanation(), report.getHandlerId(), report.getAction(), Timestamp.valueOf(unbanDateTime));
            }
        }
        handledReportRepository.createReport(report.getOriginalReport(), report.getHandlerId(), report.getAction(), report.getExplanation(), report.getDateHandled());
        activeReportRepository.deleteActiveReportById(report.getReportId());
    }
}
