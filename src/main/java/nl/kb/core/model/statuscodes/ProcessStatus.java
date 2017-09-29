package nl.kb.core.model.statuscodes;

public enum ProcessStatus {
    PENDING(10, "pending"),
    DELETED(998, "deleted"),
    PROCESSED(40, "processed"),
    PROCESSING(20, "processing"),
    FAILED(999, "failure"),
    PREPROCESS(99999, "in preproces");

    private final int code;
    private final String status;

    ProcessStatus(int statusCode, String status) {
        this.code = statusCode;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }

    public static ProcessStatus forCode(final int statusCode) {
        for (ProcessStatus s : ProcessStatus.values()) {
            if (s.code == statusCode) {
                return s;
            }
        }
        return PREPROCESS;
    }

}
