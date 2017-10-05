package nl.kb.core.model.stylesheet;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class StylesheetManager {

    private final StylesheetDao stylesheetDao;

    public StylesheetManager(StylesheetDao stylesheetDao) {

        this.stylesheetDao = stylesheetDao;
    }

    public List<Stylesheet> list() {
        return stylesheetDao.list();
    }

    public boolean exists(String name) {
        return stylesheetDao.list().stream().anyMatch(stylesheet -> stylesheet.getName().equalsIgnoreCase(name));
    }

    public Stylesheet create(String name, InputStream data) throws IOException {

        stylesheetDao.create(name, IOUtils.toByteArray(data));

        return stylesheetDao.fetchByName(name);
    }

    public Stylesheet update(String name, InputStream data) throws IOException {
        final Stylesheet lastVersion = stylesheetDao.fetchByName(name);
        stylesheetDao.createVersion(lastVersion);

        stylesheetDao.update(lastVersion.getId(), IOUtils.toByteArray(data));

        return stylesheetDao.fetchByName(name);
    }
}
