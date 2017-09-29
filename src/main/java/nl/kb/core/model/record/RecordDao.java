package nl.kb.core.model.record;

import nl.kb.oaipmh.OaiRecordHeader;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.Iterator;
import java.util.List;

@RegisterMapper(RecordMapper.class)
public interface RecordDao {

    @SqlBatch("INSERT INTO record_status (STATE, IP_NAME, TS_CREATE, REPOSITORY_ID, OAI_ID, OAI_DATESTAMP, LOOKUP) " +
            "VALUES (:state, :ipName, CURRENT_TIMESTAMP, :repositoryId, :oaiIdentifier, :oaiDateStamp, CONCAT(:ipName, :oaiIdentifier))")
    void insertBatch(@BindBean List<Record> recordList);


    @SqlQuery("select exists(select * from record_status where oai_id = :identifier and oai_datestamp = :dateStamp)")
    Boolean existsByDatestampAndIdentifier(@BindBean OaiRecordHeader oaiRecordHeader);



    @SqlQuery("select * from record_status where STATE = :process_status_code AND REPOSITORY_ID = :repository_id LIMIT :limit")
    List<Record> fetchNextWithProcessStatusByRepositoryId(
            @Bind("process_status_code") Integer processStatusCode,
            @Bind("limit") Integer limit,
            @Bind("repository_id") Integer repositoryId);



    @SqlUpdate("update record_status set STATE = :state, TS_PROCESSED = CURRENT_TIMESTAMP where ID = :id")
    void updateState(@BindBean Record record);

    @SqlUpdate("update record_status set STATE = :toState, TS_PROCESSED = CURRENT_TIMESTAMP " +
            "where REPOSITORY_ID = :repositoryId AND state = :fromState")
    void bulkUpdateState(
            @Bind("fromState") Integer fromState,
            @Bind("toState") Integer toState,
            @Bind("repositoryId") Integer repositoryId);

    @SqlQuery("select * from record_status where STATE = :process_status_code")
    Iterator<Record> fetchAllByProcessStatus(@Bind("process_status_code") Integer processStatusCode);

    @SqlQuery("select * from record_status where OAI_ID = :oaiId")
    Record findByOaiId(@Bind("oaiId") String oaiIdentifier);

    @SqlQuery("select * from record_status where IP_NAME = :ipName")
    Record findByIpName(@Bind("ipName") String ipName);


    @SqlQuery("SELECT * FROM record_status WHERE record_status.lookup LIKE :query ORDER BY state DESC LIMIT 10")
    List<Record> query(@Bind("query") String query);

}
