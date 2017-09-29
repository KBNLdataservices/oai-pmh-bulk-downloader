package nl.kb.core.websocket.socketupdate;

import nl.kb.core.scheduledjobs.ObjectHarvestSchedulerDaemon;
import nl.kb.core.websocket.SocketUpdate;

public class ObjectHarvesterRunstateUpdate implements SocketUpdate {
    private final ObjectHarvestSchedulerDaemon.RunState runState;

    public ObjectHarvesterRunstateUpdate(ObjectHarvestSchedulerDaemon.RunState runState) {
        this.runState = runState;
    }

    @Override
    public String getType() {
        return "record-fetcher";
    }

    @Override
    public Object getData() {
        return runState;
    }
}
