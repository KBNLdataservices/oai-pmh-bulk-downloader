package nl.kb.core.websocket.socketupdate;

import nl.kb.core.model.stylesheet.Stylesheet;
import nl.kb.core.websocket.SocketUpdate;

import java.util.List;

public class StylesheetUpdate implements SocketUpdate {
    private final List<Stylesheet> data;

    public StylesheetUpdate(List<Stylesheet> data) {
        this.data = data;
    }

    @Override
    public String getType() {
        return "stylesheet-update";
    }

    @Override
    public Object getData() {
        return data;
    }
}
