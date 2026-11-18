package com.teamwhite.thebuzz.model;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
public class ActiveReport {
    @Id
    private int reportId;
    private String contentType;
    private UUID contentUuidId;
    private int contentIntId;
    private UUID reporter;
    private Timestamp dateReported;
    private String reasonForReporting;

    public ActiveReport(String contentType, int contentIntId, String reporter, String reasonForReporting) {
        this.contentType = contentType;
        this.contentIntId = contentIntId;
        this.reporter = UUID.fromString(reporter);
        this.reasonForReporting = reasonForReporting;
        this.dateReported = Timestamp.valueOf(LocalDateTime.now());
    }

    public ActiveReport(String contentType, String contentUuidId, String reporter, String reasonForReporting) {
        this.contentType = contentType;
        this.contentUuidId = UUID.fromString(contentUuidId);
        this.reporter = UUID.fromString(reporter);
        this.reasonForReporting = reasonForReporting;
        this.dateReported = Timestamp.valueOf(LocalDateTime.now());
    }

    public ActiveReport(String contentType, int contentIntId, String reasonForReporting) {
        this.contentType = contentType;
        this.contentIntId = contentIntId;
        this.reporter = UUID.fromString("ec61f5f9-33dc-4905-9b84-468bc506a7a3");
        this.reasonForReporting = reasonForReporting;
        this.dateReported = Timestamp.valueOf(LocalDateTime.now());
    }

    public ActiveReport(String contentType, String contentUuidId, String reasonForReporting) {
        this.contentType = contentType;
        this.contentUuidId = UUID.fromString(contentUuidId);
        this.reporter = UUID.fromString("ec61f5f9-33dc-4905-9b84-468bc506a7a3");
        this.reasonForReporting = reasonForReporting;
        this.dateReported = Timestamp.valueOf(LocalDateTime.now());
    }

    public ActiveReport(int reportId, String contentType, String contentUuidId, String reporter, String reasonForReporting, LocalDateTime dateReported) {
        this.reportId = reportId;
        this.contentType = contentType;
        this.contentUuidId = UUID.fromString(contentUuidId);
        this.reporter = UUID.fromString(reporter);
        this.reasonForReporting = reasonForReporting;
        this.dateReported = Timestamp.valueOf(dateReported);
    }

    public ActiveReport(int reportId, String contentType, int contentIntId, String reporter, String reasonForReporting, LocalDateTime dateReported) {
        this.reportId = reportId;
        this.contentType = contentType;
        this.contentIntId = contentIntId;
        this.reporter = UUID.fromString(reporter);
        this.reasonForReporting = reasonForReporting;
        this.dateReported = Timestamp.valueOf(dateReported);
    }

    public ActiveReport(String activeReportJsonString) {
        JsonObject jsonActiveReport = Json.createReader(new StringReader(activeReportJsonString)).readObject();

        this.contentType = jsonActiveReport.getString("contentType");
        this.reportId = jsonActiveReport.getInt("reportId");
        this.contentIntId = jsonActiveReport.getInt("contentId");
        this.contentUuidId = UUID.fromString(jsonActiveReport.getString("contentUUID"));
        this.reporter = UUID.fromString(jsonActiveReport.getString("userWhoReported"));
        this.reasonForReporting = jsonActiveReport.getString("reason");
        this.dateReported = Timestamp.valueOf(jsonActiveReport.getString("dateReported"));
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentUuidId() {
        return contentUuidId.toString();
    }

    public int getContentIntId() {
        return contentIntId;
    }

    public String getReporter() {
        return reporter.toString();
    }

    public LocalDateTime getDateReported() {
        return dateReported.toLocalDateTime();
    }

    public String getReasonForReporting() {
        return reasonForReporting;
    }


    public JsonObject asJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        builder.add("reportId", reportId);
        builder.add("contentType", contentType);
        builder.add("reporterId", reporter.toString());
        builder.add("reason", reasonForReporting);
        builder.add("dateReported", dateReported.toString());

        builder.add("contentUUID", contentUuidId.toString());
        builder.add("contentId", contentIntId);

        return builder.build();
    }
}
