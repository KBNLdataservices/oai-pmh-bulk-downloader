package nl.kb.core.model.stylesheet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class Stylesheet {
    private Integer id;
    private String name;
    private InputStream xslt;
    private LocalDateTime created;
    private Boolean isLatest;

    Stylesheet(Integer id, String name, InputStream xslt, LocalDateTime created, Boolean isLatest) {
        this.id = id;
        this.name = name;
        this.xslt = xslt;
        this.created = created;
        this.isLatest = isLatest;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public InputStream getXsltStream() {
        return xslt;
    }

    public String getXslt() {
        try {
            return IOUtils.toString(xslt, Charset.defaultCharset());
        } catch (IOException e) {
            return "";
        }
    }

    public LocalDateTime getCreated() {
        return created;
    }


    public Boolean getIsLatest() {
        return isLatest;
    }
}
