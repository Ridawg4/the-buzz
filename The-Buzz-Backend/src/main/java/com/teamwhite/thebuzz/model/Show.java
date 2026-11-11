
package com.teamwhite.thebuzz.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "shows", schema = "development")
public class Show {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "show_id", columnDefinition = "BINARY(16)")
    private UUID showId;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "dj_id", columnDefinition = "BINARY(16)")
    private UUID djId;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "end_time")
    private Timestamp endTime;

    @Column(name = "show_date")
    private Date showDate;

    public Show() {
    }

    public UUID getShowId() {
        return showId;
    }

    public void setShowId(UUID showId) {
        this.showId = showId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getDjId() {
        return djId;
    }

    public void setDjId(UUID djId) {
        this.djId = djId;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }
}
