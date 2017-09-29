package nl.kb.core.model.record;

import nl.kb.core.idgen.IdGenerator;
import nl.kb.core.model.repository.Repository;
import nl.kb.core.model.repository.RepositoryDao;
import nl.kb.core.model.statuscodes.ProcessStatus;
import nl.kb.core.websocket.SocketNotifier;
import nl.kb.core.websocket.socketupdate.RecordStatusUpdate;
import nl.kb.http.HttpResponseException;
import nl.kb.oaipmh.OaiRecordHeader;
import nl.kb.oaipmh.OaiStatus;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class RecordBatchLoaderTest {

    private static final int REPOSITORY_ID = 1;
    private RepositoryDao repositoryDao;

    @Before
    public void setUp() {
        final Repository repository = new Repository.RepositoryBuilder()
                .setSet("set:with:nesting")
                .createRepository();

        repositoryDao = mock(RepositoryDao.class);
        when(repositoryDao.findById(REPOSITORY_ID)).thenReturn(repository);

    }

    @Test
    public void addToBatchShouldUpdateExistingPendingRecordsWhichAreDeletedLater() {
        final String theId = "the-id";
        final RecordDao recordDao = mock(RecordDao.class);
        final Record existing = mock(Record.class);
        final OaiRecordHeader recordHeader = mock(OaiRecordHeader.class);
        final RecordBatchLoader instance = new RecordBatchLoader(recordDao, repositoryDao, mock(IdGenerator.class),
                mock(RecordReporter.class), mock(SocketNotifier.class), false);


        when(existing.getState()).thenReturn(ProcessStatus.PENDING.getCode());
        when(recordHeader.getOaiStatus()).thenReturn(OaiStatus.DELETED);
        when(recordHeader.getIdentifier()).thenReturn(theId);
        when(recordDao.findByOaiId(theId)).thenReturn(existing);
        instance.addToBatch(REPOSITORY_ID, recordHeader);
        final InOrder inOrder = inOrder(existing, recordDao);

        inOrder.verify(existing).setState(ProcessStatus.DELETED);
        inOrder.verify(recordDao).updateState(existing);
    }

    @Test
    public void addToBatchShouldUpdateExistingFailedRecordsWhichAreDeletedLater() {
        final String theId = "the-id";
        final RecordDao recordDao = mock(RecordDao.class);
        final Record existing = mock(Record.class);
        final OaiRecordHeader recordHeader = mock(OaiRecordHeader.class);
        final RecordBatchLoader instance = new RecordBatchLoader(recordDao, repositoryDao, mock(IdGenerator.class),
                mock(RecordReporter.class), mock(SocketNotifier.class), false);


        when(existing.getState()).thenReturn(ProcessStatus.FAILED.getCode());
        when(recordHeader.getOaiStatus()).thenReturn(OaiStatus.DELETED);
        when(recordHeader.getIdentifier()).thenReturn(theId);
        when(recordDao.findByOaiId(theId)).thenReturn(existing);

        instance.addToBatch(1, recordHeader);

        final InOrder inOrder = inOrder(existing, recordDao);
        inOrder.verify(existing).setState(ProcessStatus.DELETED);
        inOrder.verify(recordDao).updateState(existing);
    }


    @Test
    public void addToBatchShouldNotUpdateExistingProcessedRecordsWhichAreDeletedLater() {
        final String theId = "the-id";
        final RecordDao recordDao = mock(RecordDao.class);
        final Record existing = mock(Record.class);
        final OaiRecordHeader recordHeader = mock(OaiRecordHeader.class);
        final RecordBatchLoader instance = new RecordBatchLoader(recordDao, repositoryDao, mock(IdGenerator.class),
                mock(RecordReporter.class), mock(SocketNotifier.class), false);

        when(existing.getState()).thenReturn(ProcessStatus.PROCESSED.getCode());
        when(recordHeader.getOaiStatus()).thenReturn(OaiStatus.DELETED);
        when(recordHeader.getIdentifier()).thenReturn(theId);
        when(recordDao.findByOaiId(theId)).thenReturn(existing);

        instance.addToBatch(1, recordHeader);

        final InOrder inOrder = inOrder(existing, recordDao);
        inOrder.verify(recordDao).findByOaiId(theId);
        inOrder.verify(existing, times(2)).getState();
        verifyNoMoreInteractions(existing);
        verifyNoMoreInteractions(recordDao);
    }

    @Test
    public void addToBatchShouldNotUpdateExistingRecordsInProcessingWhichAreDeletedLater() {
        final String theId = "the-id";
        final RecordDao recordDao = mock(RecordDao.class);
        final Record existing = mock(Record.class);
        final OaiRecordHeader recordHeader = mock(OaiRecordHeader.class);
        final RecordBatchLoader instance = new RecordBatchLoader(recordDao, repositoryDao, mock(IdGenerator.class),
                mock(RecordReporter.class), mock(SocketNotifier.class), false);

        when(existing.getState()).thenReturn(ProcessStatus.PROCESSING.getCode());
        when(recordHeader.getOaiStatus()).thenReturn(OaiStatus.DELETED);
        when(recordHeader.getIdentifier()).thenReturn(theId);
        when(recordDao.findByOaiId(theId)).thenReturn(existing);

        instance.addToBatch(1, recordHeader);

        final InOrder inOrder = inOrder(existing, recordDao);
        inOrder.verify(recordDao).findByOaiId(theId);
        inOrder.verify(existing, times(2)).getState();
        verifyNoMoreInteractions(existing);
        verifyNoMoreInteractions(recordDao);
    }

    @Test
    public void addToBatchShouldNotUpdateExistingRecordsBeyondKnownCodesWhichAreDeletedLater() {
        final String theId = "the-id";
        final RecordDao recordDao = mock(RecordDao.class);
        final Record existing = mock(Record.class);
        final OaiRecordHeader recordHeader = mock(OaiRecordHeader.class);
        final RecordBatchLoader instance = new RecordBatchLoader(recordDao, repositoryDao, mock(IdGenerator.class),
                mock(RecordReporter.class), mock(SocketNotifier.class), false);

        when(existing.getState()).thenReturn(1234);
        when(recordHeader.getOaiStatus()).thenReturn(OaiStatus.DELETED);
        when(recordHeader.getIdentifier()).thenReturn(theId);
        when(recordDao.findByOaiId(theId)).thenReturn(existing);

        instance.addToBatch(1, recordHeader);

        final InOrder inOrder = inOrder(existing, recordDao);
        inOrder.verify(recordDao).findByOaiId(theId);
        inOrder.verify(existing, times(2)).getState();
        verifyNoMoreInteractions(existing);
        verifyNoMoreInteractions(recordDao);
    }

    @Test
    public void flushBatchShouldStoreAllTheRecordsAddedToTheBatch() throws SAXException, IOException, HttpResponseException {
        final IdGenerator idGenerator = mock(IdGenerator.class);
        final OaiRecordHeader oaiRecordHeader1 = mock(OaiRecordHeader.class);
        final OaiRecordHeader oaiRecordHeader2 = mock(OaiRecordHeader.class);
        final RecordDao recordDao = mock(RecordDao.class);
        final SocketNotifier socketNotifier = mock(SocketNotifier.class);
        final RecordReporter recordReporter = mock(RecordReporter.class);
        final RecordStatusUpdate recordStatusUpdate = mock(RecordStatusUpdate.class);
        final RecordBatchLoader instance = new RecordBatchLoader(recordDao, repositoryDao, idGenerator, recordReporter, socketNotifier, false);
        when(oaiRecordHeader1.getIdentifier()).thenReturn("oai:1");
        when(oaiRecordHeader1.getOaiStatus()).thenReturn(OaiStatus.AVAILABLE);
        when(oaiRecordHeader1.getDateStamp()).thenReturn("date-oai-1");
        when(oaiRecordHeader2.getIdentifier()).thenReturn("oai:2");
        when(oaiRecordHeader2.getOaiStatus()).thenReturn(OaiStatus.AVAILABLE);
        when(oaiRecordHeader2.getDateStamp()).thenReturn("date-oai-2");
        when(idGenerator.getUniqueIdentifiers(2)).thenReturn(Stream.of("1", "2").collect(Collectors.toList()));
        when(recordDao.existsByDatestampAndIdentifier(any())).thenReturn(false);
        when(recordReporter.getStatusUpdate()).thenReturn(recordStatusUpdate);
        instance.addToBatch(1, oaiRecordHeader1);
        instance.addToBatch(1, oaiRecordHeader2);

        instance.flushBatch(1);

        final InOrder inOrder = inOrder(recordDao, socketNotifier, recordReporter, idGenerator);
        inOrder.verify(idGenerator).getUniqueIdentifiers(2);
        inOrder.verify(recordDao).insertBatch(argThat(isAListThat(
                containsInAnyOrder(
                        allOf(
                                hasProperty("ipName", is("set_1")),
                                hasProperty("oaiIdentifier", is("oai:1")),
                                hasProperty("oaiDateStamp", is("date-oai-1"))

                        ),
                        allOf(
                                hasProperty("ipName", is("set_2")),
                                hasProperty("oaiIdentifier", is("oai:2")),
                                hasProperty("oaiDateStamp", is("date-oai-2"))
                        )
                ))
        ));
        inOrder.verify(recordReporter).getStatusUpdate();
        inOrder.verify(socketNotifier).notifyUpdate(recordStatusUpdate);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void flushBatchShouldStopWhenTheBatchIsEmpty() throws SAXException, IOException, HttpResponseException {
        final IdGenerator idGenerator = mock(IdGenerator.class);
        final RecordDao recordDao = mock(RecordDao.class);
        final SocketNotifier socketNotifier = mock(SocketNotifier.class);
        final RecordReporter recordReporter = mock(RecordReporter.class);
        final RecordBatchLoader instance = new RecordBatchLoader(recordDao, repositoryDao, idGenerator, recordReporter, socketNotifier, false);

        instance.flushBatch(1);

        verifyNoMoreInteractions(recordDao, socketNotifier, recordReporter, idGenerator);
    }


    private static <T> Matcher<List<T>> isAListThat(final Matcher<Iterable<? extends T>> matcher) {
        return new BaseMatcher<List<T>>() {
            @Override public boolean matches(Object item) {
                return matcher.matches(item);
            }

            @Override public void describeTo(Description description) {
                matcher.describeTo(description);
            }
        };
    }
}