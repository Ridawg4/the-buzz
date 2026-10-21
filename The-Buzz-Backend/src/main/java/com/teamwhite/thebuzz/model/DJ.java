package com.teamwhite.thebuzz.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Represents a DJ profile in the database.
 */
@Entity
@Table(name = "djs", schema = "development")
public class DJ {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "dj_id", columnDefinition = "BINARY(16)")
    private UUID djId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "dj_name")
    private String djName;

    @Column(name = "genre")
    private String genre;

    @Column(name = "dj_pfp_location")
    private String djPfpLocation;

    // Default constructor
    public DJ() {
    }

    // DJ ID
    public UUID getDjId() {
        return djId;
    }

    public void setDjId(UUID djId) {
        this.djId = djId;
    }

    // User ID
    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    // DJ Name
    public String getDjName() {
        return djName;
    }

    public void setDjName(String djName) {
        this.djName = djName;
    }

    // Genre
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // DJ Profile Picture
    public String getDjPfpLocation() {
        return djPfpLocation;
    }

    public void setDjPfpLocation(String djPfpLocation) {
        this.djPfpLocation = djPfpLocation;
    }
}