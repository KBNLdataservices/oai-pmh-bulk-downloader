package nl.kb.core.model.stylesheet;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(StylesheetMapper.class)
public interface StylesheetDao {

    @SqlQuery("SELECT * FROM stylesheets")
    List<Stylesheet> list();

    @SqlUpdate("INSERT INTO stylesheets (name, xslt) VALUES (:name, :xslt)")
    void create(@Bind("name") String name, @Bind("xslt") byte[] xslt);

    @SqlUpdate("INSERT INTO stylesheets (name, xslt, created, is_latest)" +
            " VALUES (:s.name, :s.xsltBytes, :s.created, 0)")
    void createVersion(@BindBean("s") Stylesheet lastVersion);

    @SqlQuery("SELECT * FROM stylesheets WHERE name = :name AND is_latest = 1")
    Stylesheet fetchByName(@Bind("name") String name);


    @SqlUpdate("UPDATE stylesheets SET xslt = :xslt, created = CURRENT_TIMESTAMP() WHERE id = :id")
    void update(@Bind("id") Integer id, @Bind("xslt") byte[] xslt);

    @SqlQuery("SELECT * FROM stylesheets WHERE id = :id")
    Stylesheet fetchById(@Bind("id") Integer id);
}
