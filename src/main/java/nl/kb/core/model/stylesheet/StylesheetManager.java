package nl.kb.core.model.stylesheet;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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
        final String xslt = IOUtils.toString(data, Charset.defaultCharset());

        stylesheetDao.create(name, xslt);

        return stylesheetDao.fetchByName(name);
    }
}
