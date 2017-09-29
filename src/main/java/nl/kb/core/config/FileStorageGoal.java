package nl.kb.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FileStorageGoal {
    @JsonProperty("processing")
    PROCESSING,
    @JsonProperty("rejected")
    REJECTED,
    @JsonProperty("done")
    DONE
}
