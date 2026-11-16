package com.teamwhite.thebuzz.Services;

import com.teamwhite.thebuzz.Repositories.ActiveReportRepository;
import com.teamwhite.thebuzz.Repositories.HandledReportRepositories;
import com.teamwhite.thebuzz.model.ActiveReport;
import com.teamwhite.thebuzz.model.HandledReport;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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

    public void createHandledReport(HandledReport report) {
        if(report.getAction().equals("edit")) {
            // @TODO IMPLEMENT CREATING ACCOUNT MODERATION ENTRY WITH ACCOUNTMODERATIONSERVICE
        }
        if(report.getAction().equals("delete")) {
            // @TODO IMPLEMENT CREATING ACCOUNT MODERATION ENTRY WITH ACCOUNTMODERATIONSERVICE
        }
        Logger.getGlobal().info(String.valueOf(report.getReportId()));
        handledReportRepository.createReport(report.getOriginalReport(), report.getHandlerId(), report.getAction(), report.getExplanation(), report.getDateHandled());
        activeReportRepository.deleteActiveReportById(report.getReportId());
    }
}
