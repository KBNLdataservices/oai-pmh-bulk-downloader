package nl.kb.core.websocket.socketupdate;

import nl.kb.core.websocket.SocketUpdate;

public class RecordStatusUpdate implements SocketUpdate {

    private final Object data;

    public RecordStatusUpdate(Object data) {
        this.data = data;
    }

    @Override
    public String getType() {
        return "record-change";
    }

    @Override
    public Object getData() {
        return data;
    }
}
