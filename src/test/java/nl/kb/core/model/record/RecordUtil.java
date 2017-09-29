package nl.kb.core.model.record;

import nl.kb.oaipmh.OaiRecordHeader;
import nl.kb.oaipmh.OaiStatus;

public class RecordUtil {

    private RecordUtil() {

    }


    public static OaiRecordHeader makeRecordHeader(String oaiId, String oaiDateStamp, OaiStatus oaiStatus) {
        final OaiRecordHeader result = new OaiRecordHeader();
        result.setDateStamp(oaiDateStamp);
        result.setIdentifier(oaiId);
        result.setOaiStatus(oaiStatus);
        return result;
    }

    public static Record makeRecord(OaiRecordHeader header, int repositoryId, String ipName) {
        final Record result = Record.fromHeader(header, repositoryId);
        result.setIpName(ipName);
        return result;
    }
}
