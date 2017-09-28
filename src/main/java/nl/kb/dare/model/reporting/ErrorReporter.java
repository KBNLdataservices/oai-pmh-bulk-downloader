package nl.kb.dare.model.reporting;

import nl.kb.dare.websocket.socketupdate.ErrorStatusUpdate;
import org.skife.jdbi.v2.DBI;

import static nl.kb.dare.model.Aggregations.getAggregateCounts;

public class ErrorReporter {
    private static final String SQL =
            "select count(*) as count, status_code as status_code, repository_id as repository_id " +
            "from ERROR_REPORTS " +
            "left join DARE_PREPROCES on ERROR_REPORTS.DARE_PREPROCES_id = DARE_PREPROCES.id\n" +
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
