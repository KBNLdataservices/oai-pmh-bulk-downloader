package nl.kb.core.websocket.socketupdate;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.kb.core.model.repository.Repository;
import nl.kb.core.websocket.SocketUpdate;

import java.util.List;

public class RepositoryUpdate implements SocketUpdate {
    @JsonProperty
    final List<Repository> data;

    public RepositoryUpdate(List<Repository> list) {
        this.data = list;
    }

    @Override
    public String getType() {
        return "repository-change";
    }

    @Override
    public Object getData() {

        return data;
    }
}
