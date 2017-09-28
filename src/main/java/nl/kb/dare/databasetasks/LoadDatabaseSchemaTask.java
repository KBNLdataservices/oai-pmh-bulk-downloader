package nl.kb.dare.databasetasks;

import com.google.common.collect.ImmutableMultimap;
import io.dropwizard.servlets.tasks.Task;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.PrintWriter;

public class LoadDatabaseSchemaTask extends Task {
    private final DBI db;

    public LoadDatabaseSchemaTask(DBI db) {
        super("create-database-schema");
        this.db = db;
    }

    @Override
    public void execute(ImmutableMultimap<String, String> immutableMultimap, PrintWriter printWriter) throws Exception {
        final Handle h = db.open();

        SchemaLoader.runSQL("/database-schema/repositories.sql", h);
        SchemaLoader.runSQL("/database-schema/record_status.sql", h);
        SchemaLoader.runSQL("/database-schema/error_reports.sql", h);

        h.close();
    }

}
