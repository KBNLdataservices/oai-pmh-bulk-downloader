package nl.kb.dare.model.reporting;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;

@RegisterMapper(ExcelReportRowMapper.class)
public interface ExcelReportDao {

    @SqlQuery("select STATUS_CODE, TS_CREATE, MESSAGE, URL, OAI_ID, TS_PROCESSED, STATE, IP_NAME, OAI_DATESTAMP " +
            "from ERROR_REPORTS left join DARE_PREPROCES on ERROR_REPORTS.DARE_PREPROCES_id = DARE_PREPROCES.id " +
            "where repository_id = :repositoryId")
    Iterator<ExcelReportRow> getExcelForRepository(@Bind("repositoryId") Integer repositoryId);
}
