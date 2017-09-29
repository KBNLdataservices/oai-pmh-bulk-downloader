package nl.kb.core.model.stylesheet;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StylesheetMapper implements ResultSetMapper<Stylesheet> {

    @Override
    public Stylesheet map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException {
        final Blob xsltBlob = resultSet.getBlob("xslt");
        final Timestamp dCreated = resultSet.getTimestamp("created");

        final Integer id = resultSet.getInt("id");
        final String name = resultSet.getString("name");
        final InputStream xslt = xsltBlob != null ? xsltBlob.getBinaryStream() : null;
        final LocalDateTime created = dCreated != null ? dCreated.toLocalDateTime() : null;
        final Boolean isLatest = resultSet.getBoolean("is_latest");

        return new Stylesheet(
                id, name, xslt, created, isLatest
        );
    }
}
