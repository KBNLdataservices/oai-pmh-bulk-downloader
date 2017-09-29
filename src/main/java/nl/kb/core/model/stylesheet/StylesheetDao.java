package nl.kb.core.model.stylesheet;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(StylesheetMapper.class)
public interface StylesheetDao {

    @SqlQuery("SELECT * FROM stylesheets")
    List<Stylesheet> list();

    @SqlUpdate("INSERT INTO stylesheets (name, xslt) VALUES (:name, :xslt)")
    void create(@Bind("name") String name, @Bind("xslt") String xslt);

    @SqlQuery("SELECT * FROM stylesheets WHERE name = :name AND is_latest = 1")
    Stylesheet fetchByName(@Bind("name") String name);
}
