package com.teamwhite.thebuzz.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.StringReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class HandledReport {
    @Id
    private int reportId;
    private JsonObject originalReport;
    private UUID handlerId;
    private String action;
    private String explanation;
    private Timestamp dateHandled;

    public HandledReport(int reportId, String originalReport, String handlerId, String action, String explanation) {
        this.reportId = reportId;
        this.originalReport = Json.createReader(new StringReader(originalReport)).readObject();
        this.handlerId = UUID.fromString(handlerId);
        this.action = action;
        this.explanation = explanation;
        this.dateHandled = Timestamp.valueOf(LocalDateTime.now());
    }

    public HandledReport(int reportId, String originalReport, String handlerId, String action, String explanation, LocalDateTime dateReported) {
        this.reportId = reportId;
        this.originalReport = Json.createReader(new StringReader(originalReport)).readObject();
        this.handlerId = UUID.fromString(handlerId);
        this.action = action;
        this.explanation = explanation;
        this.dateHandled = Timestamp.valueOf(dateReported);
    }

    public String getOriginalReport() {
        return originalReport.toString();
    }

    public int getReportId() {
        return reportId;
    }

    public String getHandlerId() {
        return handlerId.toString();
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getDateHandled() {
        return dateHandled.toLocalDateTime();
    }

    public String getExplanation() {
        return explanation;
    }


    public JsonObject asJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("reportId", reportId);
        builder.add("originalReport", originalReport.toString());
        builder.add("handlerId", handlerId.toString());
        builder.add("action", action);
        builder.add("dateHandled", dateHandled.toString());
        builder.add("explanation", explanation);

        return builder.build();
    }
}
