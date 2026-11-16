package com.teamwhite.thebuzz.Repositories;

import com.teamwhite.thebuzz.model.ActiveReport;
import com.teamwhite.thebuzz.model.HandledReport;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HandledReportRepositories extends CrudRepository<HandledReport, Integer> {

    @Query(value="SELECT report_id, original_report, BIN_TO_UUID(handler_id), report_response, response_explanation, date_handled FROM handled_reports", nativeQuery=true)
    public List<Object[]> getAllHandledReports();

    @Query(value="SELECT report_id, original_report, BIN_TO_UUID(handler_id), report_response, response_explanation, date_handled FROM handled_reports WHERE report_id = ?1", nativeQuery=true)
    public List<Object[]> getHandledReportById(int id);

    @Modifying
    @Transactional
    @Query(value="INSERT INTO handled_reports(original_report, handler_id, report_response, response_explanation, date_handled) VALUES (?1, UUID_TO_BIN(?2), ?3, ?4, ?5)", nativeQuery=true)
    public void createReport(String originalReport, String handlerId, String response, String responseExplanation, LocalDateTime dateReported);
}
