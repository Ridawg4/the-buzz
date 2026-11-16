package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.ActiveReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActiveReportRepository extends CrudRepository<ActiveReport, Integer> {

    @Query(value="SELECT report_id, content_type, BIN_TO_UUID(content_uuid_id), content_int_id, BIN_TO_UUID(user_who_reported), reason, date_reported FROM active_reports", nativeQuery=true)
    public List<Object[]> getAllReports();

    @Query(value="SELECT report_id, content_type, BIN_TO_UUID(content_uuid_id), content_int_id, BIN_TO_UUID(user_who_reported), reason, date_reported FROM active_reports WHERE report_id = ?1", nativeQuery=true)
    public List<Object[]> getReportById(int id);

    @Modifying
    @Transactional
    @Query(value="INSERT INTO active_reports(content_type, content_uuid_id, content_int_id, user_who_reported, reason, date_reported) VALUES (?1, UUID_TO_BIN(?2), ?3, UUID_TO_BIN(?4), ?5, ?6)", nativeQuery=true)
    public void createReport(String contentType, String contentUUID, int contentId, String reporter, String reason, LocalDateTime dateReported);

    @Modifying
    @Transactional
    @Query(value="DELETE FROM active_reports WHERE report_id = ?1", nativeQuery=true)
    public void deleteActiveReportById(int reportId);
}
