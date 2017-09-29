package nl.kb.core.model;

public enum RunState {
    WAITING(0), RUNNING(1), INTERRUPTED(2), QUEUED(3);

    private final int code;

    RunState(int code) {
        this.code = code;
    }

    public static RunState forCode(final int statusCode) {
        for (RunState s : RunState.values()) {
            if (s.code == statusCode) {
                return s;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }
}
