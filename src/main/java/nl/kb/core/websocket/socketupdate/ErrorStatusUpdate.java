package nl.kb.core.websocket.socketupdate;

import nl.kb.core.websocket.SocketUpdate;

public class ErrorStatusUpdate implements SocketUpdate {

    private final Object data;

    public ErrorStatusUpdate(Object data) {

        this.data = data;
    }

    @Override
    public String getType() {
        return "error-change";
    }

    @Override
    public Object getData() {
        return data;
    }
}
