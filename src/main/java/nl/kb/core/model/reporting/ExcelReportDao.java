package nl.kb.core.model.reporting;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;

@RegisterMapper(ExcelReportRowMapper.class)
public interface ExcelReportDao {

    @SqlQuery("select STATUS_CODE, TS_CREATE, MESSAGE, URL, OAI_ID, TS_PROCESSED, STATE, IP_NAME, OAI_DATESTAMP " +
            "from error_reports left join record_status on error_reports.record_status_id = record_status.id " +
            "where repository_id = :repositoryId")
    Iterator<ExcelReportRow> getExcelForRepository(@Bind("repositoryId") Integer repositoryId);
}
