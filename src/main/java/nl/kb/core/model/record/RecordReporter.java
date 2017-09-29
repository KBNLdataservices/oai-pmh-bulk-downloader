package nl.kb.core.model.record;

import nl.kb.core.model.statuscodes.ProcessStatus;
import nl.kb.core.websocket.socketupdate.RecordStatusUpdate;
import org.skife.jdbi.v2.DBI;

import static nl.kb.core.model.Aggregations.getAggregateCounts;

public class RecordReporter {

    private static final String SQL = "select count(*) as count," +
            "record_status.state as status_code, " +
            "record_status.repository_id as repository_id " +
            "from record_status " +
            "group by repository_id, state";

    private final DBI db;

    public RecordReporter(DBI db) {
        this.db = db;
    }

    public RecordStatusUpdate getStatusUpdate() {
        return new RecordStatusUpdate(getAggregateCounts(db, SQL, this::codeToHumanKey));
    }

    private String codeToHumanKey(int statusCode) {
        final ProcessStatus processStatus = ProcessStatus.forCode(statusCode);
        if (processStatus == null) {
            return Integer.toString(statusCode);
        }
        return processStatus.getStatus();
    }
}
