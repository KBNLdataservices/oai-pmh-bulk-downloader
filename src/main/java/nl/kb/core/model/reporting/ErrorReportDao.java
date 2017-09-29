package nl.kb.core.model.reporting;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

@RegisterMapper(StoredErrorReportMapper.class)
public interface ErrorReportDao {

    @SqlUpdate(
        "INSERT INTO error_reports (record_status_id, MESSAGE, URL, STACKTRACE, STATUS_CODE) " +
        "VALUES (:recordId, :report.errorMessage, :report.url, :report.filteredStackTrace, :report.statusCode)"
    )
    void insert(@Bind("recordId") Long recordId, @BindBean("report") ErrorReport errorReport);

    @SqlQuery("SELECT * FROM error_reports WHERE record_status_id = :recordId")
    StoredErrorReport fetchForRecordId(@Bind("recordId") Long recordId);

    @SqlUpdate("delete from error_reports where record_status_id in (" +
            "  select id" +
            "  from record_status" +
            "  where repository_id = :repositoryId" +
            "  and state = :state" +
            ")")
    void bulkDeleteForRepository(@Bind("state") Integer processStatusCode, @Bind("repositoryId") Integer repositoryId);

    @SqlUpdate("delete from error_reports where record_status_id = :recordId")
    void deleteForRecordId(@Bind("recordId") Long recordId);
}
