package nl.kb.core.model.reporting;

import nl.kb.core.websocket.socketupdate.ErrorStatusUpdate;
import org.skife.jdbi.v2.DBI;

import static nl.kb.core.model.Aggregations.getAggregateCounts;

public class ErrorReporter {
    private static final String SQL =
            "select count(*) as count, status_code as status_code, repository_id as repository_id " +
            "from error_reports " +
            "left join record_status on error_reports.record_status_id = record_status.id\n" +
            "group by status_code, repository_id";

    private final DBI db;

    public ErrorReporter(DBI db) {
        this.db = db;
    }
    
    public ErrorStatusUpdate getStatusUpdate() {
        return new ErrorStatusUpdate(getAggregateCounts(db, SQL, this::codeToHumanKey));

    }

    private String codeToHumanKey(int statusCode) {
        return Integer.toString(statusCode);
    }
}
