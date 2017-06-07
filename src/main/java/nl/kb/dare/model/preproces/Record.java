package nl.kb.dare.model.preproces;

import nl.kb.dare.model.statuscodes.ProcessStatus;
import nl.kb.oaipmh.OaiRecordHeader;
import nl.kb.oaipmh.OaiStatus;

public class Record {

    private Integer id;
    private ProcessStatus state;
    private String kbObjId;
    private String tsCreate;
    private String tsProcessed;
    private Integer repositoryId;
    private String oaiIdentifier;
    private final String oaiDateStamp;


    Record(Integer id, ProcessStatus state, String kbObjId,  Integer repositoryId,
           String oaiIdentifier, String oaiDateStamp) {
        this.id = id;
        this.state = state;
        this.kbObjId = kbObjId;
        this.repositoryId = repositoryId;
        this.oaiIdentifier = oaiIdentifier;
        this.oaiDateStamp = oaiDateStamp;
    }

    private Record(ProcessStatus state, String kbObjId, Integer repositoryId,
                   String oaiIdentifier, String oaiDateStamp) {
        this(null, state, kbObjId, repositoryId, oaiIdentifier, oaiDateStamp);
    }

    static Record fromHeader(OaiRecordHeader header, Integer repositoryId) {
        return new Record(
                header.getOaiStatus() == OaiStatus.AVAILABLE ? ProcessStatus.PENDING : ProcessStatus.DELETED,
                null,
                repositoryId,
                header.getIdentifier(),
                header.getDateStamp()
        );
    }

    public Integer getState() {
        return state.getCode();
    }

    public String getKbObjId() {
        return kbObjId;
    }

    public Integer getRepositoryId() {
        return repositoryId;
    }

    public void setKbObjId(Long kbObjId) {
        this.kbObjId = String.format("%d", kbObjId);
    }

    public void setState(ProcessStatus processStatus) {
        this.state = processStatus;
    }

    public Integer getId() {
        return id;
    }

    public String getOaiIdentifier() {
        return oaiIdentifier;
    }

    public String getOaiDateStamp() { return oaiDateStamp; }
}
