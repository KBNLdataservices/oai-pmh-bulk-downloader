package nl.kb.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import nl.kb.core.config.FileStorageFactory;
import nl.kb.core.config.FileStorageGoal;
import nl.kb.core.config.MailerFactory;

import java.util.Map;

class Config extends Configuration {
    private DataSourceFactory database;
    private MailerFactory mailerFactory;

    @JsonProperty
    private String hostName;

    @JsonProperty
    private String numbersEndpoint;

    @JsonProperty
    private Integer maxParallelHarvests;

    @JsonProperty
    private Integer maxParallelDownloads;

    @JsonProperty
    private Long downloadQueueFillDelayMs;

    @JsonProperty
    private Boolean authEnabled;


    @JsonProperty
    private Integer maxConsecutiveDownloadFailures = 10;

    @JsonProperty
    private Boolean batchLoadSampleMode;

    @JsonProperty("fileStorage")
    private Map<FileStorageGoal, FileStorageFactory> fileStorageFactory;


    @JsonProperty("database")
    DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    public Map<FileStorageGoal, FileStorageFactory> getFileStorageFactory() {
        return fileStorageFactory;
    }

    @JsonProperty("mailer")
    public MailerFactory getMailerFactory() { return mailerFactory; }

    @JsonProperty("mailer")
    public void setMailerFactory(MailerFactory mailerFactory) {
        this.mailerFactory = mailerFactory;
    }


    public String getHostName() {
        return hostName;
    }

    public String getNumbersEndpoint() {
        return numbersEndpoint;
    }

    public Integer getMaxParallelHarvests() {
        return maxParallelHarvests;
    }

    public Integer getMaxParallelDownloads() {
        return maxParallelDownloads;
    }

    public Long getDownloadQueueFillDelayMs() {
        return downloadQueueFillDelayMs;
    }

    public boolean getAuthEnabled() {
        return authEnabled;
    }

    public Integer getMaxConsecutiveDownloadFailures() {
        return maxConsecutiveDownloadFailures;
    }

    public Boolean getBatchLoadSampleMode() {
        return batchLoadSampleMode;
    }

    public void setBatchLoadSampleMode(Boolean batchLoadSampleMode) {
        this.batchLoadSampleMode = batchLoadSampleMode;
    }
}
