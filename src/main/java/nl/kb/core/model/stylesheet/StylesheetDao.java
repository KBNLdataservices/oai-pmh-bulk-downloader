package nl.kb.core.model.stylesheet;

import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(StylesheetMapper.class)
public interface StylesheetDao {

    @SqlQuery("SELECT * FROM stylesheets")
    List<Stylesheet> list();
}
