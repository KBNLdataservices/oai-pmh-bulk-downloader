package nl.kb.dare.objectharvester;

import com.google.common.collect.Lists;
import nl.kb.dare.model.preproces.Record;
import nl.kb.dare.model.reporting.ErrorReport;
import nl.kb.dare.model.repository.Repository;
import nl.kb.dare.model.statuscodes.ErrorStatus;
import nl.kb.filestorage.FileStorage;
import nl.kb.filestorage.FileStorageHandle;
import nl.kb.http.HttpFetcher;
import nl.kb.http.HttpResponseHandler;
import nl.kb.http.responsehandlers.ResponseHandlerFactory;
import nl.kb.manifest.ManifestFinalizer;
import nl.kb.manifest.ObjectResource;
import nl.kb.xslt.XsltTransformer;
import org.junit.Test;
import org.mockito.InOrder;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class ObjectHarvesterOperationsTest {


    @Test
    public void getFileStorageHandleShouldReturnAHandleIfAvailable() throws IOException {
        final FileStorage fileStorage = mock(FileStorage.class);
        final FileStorageHandle handle = mock(FileStorageHandle.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(fileStorage, mock(HttpFetcher.class),
                mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));
        final Record oaiRecord = mock(Record.class);
        when(fileStorage.create(oaiRecord.getKbObjId())).thenReturn(handle);

        final Optional<FileStorageHandle> result = instance.getFileStorageHandle(oaiRecord, (errorReport) -> {});

        assertThat(result.isPresent(), is(true));
        assertThat(result.get(), is(handle));
    }

    @Test
    public void getFileStorageHandleShouldEmptyOptionalWhenIOExceptionIsCaught() throws IOException {
        final FileStorage fileStorage = mock(FileStorage.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                fileStorage, mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));
        final Record oaiRecord = mock(Record.class);
        when(fileStorage.create(oaiRecord.getKbObjId())).thenThrow(IOException.class);

        final Optional<FileStorageHandle> result = instance.getFileStorageHandle(oaiRecord, (errorReport) -> {});

        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void downloadMetdataShouldFetchTheMetadataRecord() throws IOException {

        final HttpFetcher httpFetcher = mock(HttpFetcher.class);
        final Repository repository = mock(Repository.class);
        final Record oaiRecord = mock(Record.class);
        final ResponseHandlerFactory responseHandlerFactory = mock(ResponseHandlerFactory.class);
        final HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class), httpFetcher,
                responseHandlerFactory, mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));
        when(oaiRecord.getOaiIdentifier()).thenReturn("identifier");
        when(repository.getUrl()).thenReturn("http://example.com");
        when(repository.getMetadataPrefix()).thenReturn("metadataPrefix");
        when(responseHandlerFactory.getStreamCopyingResponseHandler(any(), any(), any()))
                .thenReturn(responseHandler);
        when(fileStorageHandle.getOutputStream("metadata.xml")).thenReturn(new ByteArrayOutputStream());

        instance.downloadMetadata(fileStorageHandle, oaiRecord, repository, (errorReport) -> {});

        verify(httpFetcher).execute(argThat(allOf(
                hasProperty("host", is("example.com")),
                hasProperty("query", is("verb=GetRecord&metadataPrefix=metadataPrefix&identifier=identifier"))
        )), argThat(is(responseHandler)));
    }


    @Test
    public void downloadMetadataShouldLogAnyExceptionsFromTheResponseHandler() throws IOException {
        final List<ErrorReport> reports = Lists.newArrayList();
        final Consumer<ErrorReport> onError = reports::add;
        final ResponseHandlerFactory responseHandlerFactory = mock(ResponseHandlerFactory.class);
        final HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);
        final List<Exception> returnedReports = Lists.newArrayList(new SAXException("test"), new IOException("test 2"));
        final Record oaiRecord = mock(Record.class);
        final Repository repository = mock(Repository.class);
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), responseHandlerFactory, mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));

        when(responseHandlerFactory.getStreamCopyingResponseHandler(any(), any(), any()))
                .thenReturn(responseHandler);
        when(responseHandler.getExceptions()).thenReturn(returnedReports);
        when(oaiRecord.getKbObjId()).thenReturn("identifier");
        when(repository.getUrl()).thenReturn("http://example.com");
        when(repository.getMetadataPrefix()).thenReturn("metadataPrefix");
        when(fileStorageHandle.getOutputStream("metadata.xml")).thenReturn(new ByteArrayOutputStream());

        instance.downloadMetadata(fileStorageHandle, oaiRecord, repository, onError);

        assertThat(reports, containsInAnyOrder(
                allOf(
                        hasProperty("exception", is(instanceOf(SAXException.class))),
                        hasProperty("errorStatus", is(ErrorStatus.XML_PARSING_ERROR))
                ), allOf(
                        hasProperty("exception", is(instanceOf(IOException.class))),
                        hasProperty("errorStatus", is(ErrorStatus.IO_EXCEPTION))
                )
        ));
    }

    @Test
    public void downloadMetadataShouldReturnTrueWhenThereWereNoExceptionsFromTheResponseHandler() throws IOException {
        final ResponseHandlerFactory responseHandlerFactory = mock(ResponseHandlerFactory.class);
        final HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);
        final Record oaiRecord = mock(Record.class);
        final Repository repository = mock(Repository.class);
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), responseHandlerFactory, mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));

        when(responseHandlerFactory.getStreamCopyingResponseHandler(any(), any(), any()))
                .thenReturn(responseHandler);
        when(oaiRecord.getKbObjId()).thenReturn("identifier");
        when(repository.getUrl()).thenReturn("http://example.com");
        when(repository.getMetadataPrefix()).thenReturn("metadataPrefix");
        when(fileStorageHandle.getOutputStream("metadata.xml")).thenReturn(new ByteArrayOutputStream());

        final Optional<ObjectResource> result = instance.downloadMetadata(fileStorageHandle, oaiRecord, repository,
                (errorReport) -> {});

        assertThat(result.isPresent(), is(true));
    }

    @Test
    public void downloadMetadataShouldReturnFalseWhenThereWereExceptionsFromTheResponseHandler() throws IOException {
        final ResponseHandlerFactory responseHandlerFactory = mock(ResponseHandlerFactory.class);
        final HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);
        final Record oaiRecord = mock(Record.class);
        final Repository repository = mock(Repository.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class), mock(HttpFetcher.class),
                responseHandlerFactory, mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);

        when(responseHandler.getExceptions()).thenReturn(Lists.newArrayList(mock(Exception.class)));
        when(responseHandlerFactory.getStreamCopyingResponseHandler(any(), any(), any()))
                .thenReturn(responseHandler);
        when(oaiRecord.getKbObjId()).thenReturn("identifier");
        when(repository.getUrl()).thenReturn("http://example.com");
        when(repository.getMetadataPrefix()).thenReturn("metadataPrefix");
        when(fileStorageHandle.getOutputStream("metadata.xml")).thenReturn(new ByteArrayOutputStream());

        final Optional<ObjectResource> result = instance.downloadMetadata(fileStorageHandle, oaiRecord, repository,
                (errorReport) -> {});

        assertThat(result.isPresent(), is(false));
    }

    @Test
    public void downloadMetdataShouldLogAnyCaughtIOExceptionAndThenReturnFalse() throws IOException {
        final List<ErrorReport> reports = Lists.newArrayList();
        final Consumer<ErrorReport> onError = reports::add;
        final ResponseHandlerFactory responseHandlerFactory = mock(ResponseHandlerFactory.class);
        final HttpResponseHandler responseHandler = mock(HttpResponseHandler.class);
        final Record oaiRecord = mock(Record.class);
        final Repository repository = mock(Repository.class);
        final FileStorageHandle storageHandle = mock(FileStorageHandle.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class), mock(HttpFetcher.class),
                responseHandlerFactory, mock(XsltTransformer.class), mock(ObjectHarvesterResourceOperations.class),
                mock(ManifestFinalizer.class));

        when(responseHandler.getExceptions()).thenReturn(Lists.newArrayList(mock(Exception.class)));
        when(storageHandle.getOutputStream("metadata.xml")).thenReturn(new ByteArrayOutputStream());

        final Optional<ObjectResource> result = instance.downloadMetadata(storageHandle, oaiRecord, repository, onError);

        assertThat(result.isPresent(), is(false));
        assertThat(reports.get(0), hasProperty("exception", is(instanceOf(IOException.class))));
    }

    @Test
    public void generateManifestShouldTransformTheResponseTheXsltTransformer() throws IOException, TransformerException {
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        final XsltTransformer xsltTransformer = mock(XsltTransformer.class);
        final InputStream in = mock(InputStream.class);
        final OutputStream out = mock(OutputStream.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class),
                mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), xsltTransformer,
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));

        when(fileStorageHandle.getFile("metadata.xml")).thenReturn(in);
        when(fileStorageHandle.getOutputStream("manifest.initial.xml")).thenReturn(out);

        final boolean result = instance.generateManifest(fileStorageHandle, (errorReport) -> {});

        final InOrder inOrder = inOrder(fileStorageHandle, xsltTransformer);
        inOrder.verify(fileStorageHandle).getFile("metadata.xml");
        inOrder.verify(fileStorageHandle).getOutputStream("manifest.initial.xml");
        inOrder.verify(xsltTransformer).transform(argThat(is(in)), argThat(allOf(
                is(instanceOf(StreamResult.class)),
                hasProperty("writer", is(instanceOf(OutputStreamWriter.class))
        ))));

        assertThat(result, is(true));
    }

    @Test
    public void generateManifestShouldLogAnyIoException() throws IOException, TransformerException {
        final List<ErrorReport> reports = Lists.newArrayList();
        final Consumer<ErrorReport> onError = reports::add;
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        final XsltTransformer xsltTransformer = mock(XsltTransformer.class);
        final InputStream in = mock(InputStream.class);
        final OutputStream out = mock(OutputStream.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class),
                mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), xsltTransformer,
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));

        when(fileStorageHandle.getFile("metadata.xml")).thenReturn(in);
        when(fileStorageHandle.getOutputStream("manifest.initial.xml")).thenThrow(IOException.class);

        final boolean result = instance.generateManifest(fileStorageHandle, onError);

        assertThat(result, is(false));
        assertThat(reports.get(0), hasProperty("exception", is(instanceOf(IOException.class))));
    }

    @Test
    public void generateManifestShouldLogAnyTransformerException() throws IOException, TransformerException {
        final List<ErrorReport> reports = Lists.newArrayList();
        final Consumer<ErrorReport> onError = reports::add;
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        final XsltTransformer xsltTransformer = mock(XsltTransformer.class);
        final InputStream in = mock(InputStream.class);
        final OutputStream out = mock(OutputStream.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class),
                mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), xsltTransformer,
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));

        when(fileStorageHandle.getFile("metadata.xml")).thenReturn(in);
        when(fileStorageHandle.getOutputStream("manifest.initial.xml")).thenReturn(out);
        doThrow(TransformerException.class).when(xsltTransformer).transform(any(), any());
        final boolean result = instance.generateManifest(fileStorageHandle, onError);

        assertThat(result, is(false));
        assertThat(reports.get(0), hasProperty("exception", is(instanceOf(TransformerException.class))));
    }


    @Test
    public void collectResourcesShouldReturnTheListOfObjectResourcesInXml() throws FileNotFoundException {
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));
        final FileStorageHandle handle = mock(FileStorageHandle.class);
        when(handle.getFile("manifest.initial.xml")).thenReturn(ObjectHarvesterOperationsTest.class.getResourceAsStream("/oai/manifest.xml"));

        final List<ObjectResource> objectResources = instance.collectResources(handle, (errorReport) -> {});

        assertThat(objectResources.get(0).getXlinkHref(), is("https://openaccess.leidenuniv.nl/bitstream/1887/20432/3/Stellingen%205.pdf"));
        assertThat(objectResources.get(1).getXlinkHref(), is("https://openaccess.leidenuniv.nl/bitstream/1887/20432/4/back.pdf"));
        assertThat(objectResources.get(2).getXlinkHref(), is("https://openaccess.leidenuniv.nl/bitstream/1887/20432/5/samenvatting.pdf"));
        assertThat(objectResources.get(0).getId(), is("FILE_0001"));
        assertThat(objectResources.get(1).getId(), is("FILE_0002"));
        assertThat(objectResources.get(2).getId(), is("FILE_0003"));
    }


    @Test
    public void collectResourcesShouldLogAnErrorWhenASaxExceptionIsCaught() throws FileNotFoundException {
        final List<ErrorReport> errorReports = Lists.newArrayList();
        final InputStream badXml = new ByteArrayInputStream("<invalid></".getBytes(StandardCharsets.UTF_8));
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));
        final FileStorageHandle handle = mock(FileStorageHandle.class);
        when(handle.getFile("manifest.initial.xml")).thenReturn(badXml);

        final List<ObjectResource> objectResources = instance.collectResources(handle, errorReports::add);

        assertThat(objectResources.isEmpty(), is(true));
        assertThat(errorReports.isEmpty(), is(false));
        assertThat(errorReports.get(0), hasProperty("errorStatus", is(ErrorStatus.XML_PARSING_ERROR)));
    }

    @Test
    public void collectResourcesShouldLogAnErrorWhenAnIOExceptionIsCaught() throws FileNotFoundException {
        final List<ErrorReport> errorReports = Lists.newArrayList();
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class), mock(ManifestFinalizer.class));
        final FileStorageHandle handle = mock(FileStorageHandle.class);
        when(handle.getFile("manifest.initial.xml")).thenThrow(IOException.class);

        final List<ObjectResource> objectResources = instance.collectResources(handle, errorReports::add);

        assertThat(objectResources.isEmpty(), is(true));
        assertThat(errorReports.isEmpty(), is(false));
        assertThat(errorReports.get(0), hasProperty("errorStatus", is(ErrorStatus.IO_EXCEPTION)));
    }


    @Test
    public void downloadResourcesShouldDownloadAllObjectResourcesAndReturnTrueUponSuccess() throws IOException, NoSuchAlgorithmException {
        final ObjectHarvesterResourceOperations resourceOperations = mock(ObjectHarvesterResourceOperations.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class), mock(HttpFetcher.class),
                mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                resourceOperations,
                mock(ManifestFinalizer.class));
        final ObjectResource objectResource1 = new ObjectResource();
        final ObjectResource objectResource2 = new ObjectResource();
        final List<ObjectResource> objectResources = Lists.newArrayList(
                objectResource1, objectResource2
        );
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        when(resourceOperations.downloadResource(any(), any())).thenReturn(Lists.newArrayList());

        final boolean success = instance.downloadResources(fileStorageHandle, objectResources, errorReport -> {});

        verify(resourceOperations).downloadResource(objectResource1, fileStorageHandle);
        verify(resourceOperations).downloadResource(objectResource2, fileStorageHandle);
        assertThat(success, is(true));
    }

    @Test
    public void downloadResourcesShouldReturnFalseUponAnyError() throws IOException, NoSuchAlgorithmException {
        final ObjectHarvesterResourceOperations resourceOperations = mock(ObjectHarvesterResourceOperations.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class), mock(HttpFetcher.class),
                mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                resourceOperations,
                mock(ManifestFinalizer.class));
        final ObjectResource objectResource1 = new ObjectResource();
        final ObjectResource objectResource2 = new ObjectResource();
        final List<ObjectResource> objectResources = Lists.newArrayList(
                objectResource1, objectResource2
        );
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        when(resourceOperations.downloadResource(any(), any())).thenReturn(Lists.newArrayList(mock(ErrorReport.class)));

        final boolean success = instance.downloadResources(fileStorageHandle, objectResources, errorReport -> {});

        assertThat(success, is(false));
    }

    @Test
    public void downloadResourcesShouldLogTheFirstDownloadError() throws IOException, NoSuchAlgorithmException {
        final List<ErrorReport> reports = Lists.newArrayList();
        final Consumer<ErrorReport> onError = reports::add;
        final ObjectHarvesterResourceOperations resourceOperations = mock(ObjectHarvesterResourceOperations.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(mock(FileStorage.class), mock(HttpFetcher.class),
                mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                resourceOperations,
                mock(ManifestFinalizer.class));
        final FileStorageHandle fileStorageHandle = mock(FileStorageHandle.class);
        final ErrorReport report1 = mock(ErrorReport.class);
        final ErrorReport report2 = mock(ErrorReport.class);
        final List<ErrorReport> reportedErrors = Lists.newArrayList(
                report1, report2
        );
        when(resourceOperations.downloadResource(any(), any())).thenReturn(reportedErrors);

        instance.downloadResources(fileStorageHandle, Lists.newArrayList(new ObjectResource(), new ObjectResource()), onError);

        assertThat(reports.size(), is(1));
        assertThat(reports.get(0), is(report1));
    }

    @Test
    public void downloadResourcesShouldReturnFalseAndLogAnyCaughtIOException() throws IOException, NoSuchAlgorithmException {
        final List<ErrorReport> reports = Lists.newArrayList();
        final Consumer<ErrorReport> onError = reports::add;
        final ObjectHarvesterResourceOperations resourceOperations = mock(ObjectHarvesterResourceOperations.class);
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class),
                mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                resourceOperations,
                mock(ManifestFinalizer.class));

        when(resourceOperations.downloadResource(any(), any())).thenThrow(IOException.class);

        final boolean success = instance.downloadResources(
                mock(FileStorageHandle.class), Lists.newArrayList(new ObjectResource()), onError);


        assertThat(success, is(false));
        assertThat(reports.size(), is(1));
        assertThat(reports.get(0), allOf(
                hasProperty("exception", is(instanceOf(IOException.class))),
                hasProperty("errorStatus", is(ErrorStatus.IO_EXCEPTION))
        ));
    }

    @Test
    public void writeFilenamesAndChecksumsToMetadataShouldCreateASipFileFromTheMetadataXML() throws IOException, TransformerException, SAXException {
        final List<ErrorReport> errorReports = Lists.newArrayList();
        final InputStream manifest =ObjectHarvesterOperationsTest.class.getResourceAsStream("/oai/manifest.xml");
        final FileStorageHandle handle = mock(FileStorageHandle.class);
        when(handle.getFile("manifest.initial.xml")).thenReturn(manifest);
        final ByteArrayOutputStream sip = new ByteArrayOutputStream();
        when(handle.getOutputStream("manifest.xml")).thenReturn(sip);
        final ManifestFinalizer manifestFinalizer = mock(ManifestFinalizer.class);
        final List<ObjectResource> objectResources = Lists.newArrayList(new ObjectResource());
        final ObjectResource metadataResource = new ObjectResource();

        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), mock(ResponseHandlerFactory.class),
                mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class),
                manifestFinalizer);

        final boolean result = instance.writeFilenamesAndChecksumsToMetadata(handle, objectResources, metadataResource,
                errorReports::add);

        final InOrder inOrder = inOrder(handle, manifestFinalizer);

        inOrder.verify(handle).getFile("manifest.initial.xml");
        inOrder.verify(handle).getOutputStream("manifest.xml");
        inOrder.verify(manifestFinalizer).writeResourcesToManifest(
                argThat(is(metadataResource)),
                argThat(is(objectResources)),
                argThat(is(instanceOf(InputStreamReader.class))),
                argThat(is(instanceOf(OutputStreamWriter.class)))
        );
        verifyNoMoreInteractions(manifestFinalizer);
        verifyNoMoreInteractions(handle);

        assertThat(result, is(true));
        assertThat(errorReports.isEmpty(), is(true));
    }

    @Test
    public void writeFilenamesAndChecksumsToMetadataShouldLogAnyIOException() throws IOException, TransformerException, SAXException {
        final List<ErrorReport> errorReports = Lists.newArrayList();
        final InputStream manifest =ObjectHarvesterOperationsTest.class.getResourceAsStream("/oai/manifest.xml");
        final FileStorageHandle handle = mock(FileStorageHandle.class);
        when(handle.getFile("manifest.initial.xml")).thenReturn(manifest);
        final ByteArrayOutputStream sip = new ByteArrayOutputStream();
        when(handle.getOutputStream("manifest.xml")).thenReturn(sip);
        final ManifestFinalizer manifestFinalizer = mock(ManifestFinalizer.class);
        final ObjectResource metadataResource = mock(ObjectResource.class);
        final List<ObjectResource> objectResources = Lists.newArrayList(new ObjectResource());
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), mock(ResponseHandlerFactory.class),
                mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class),
                manifestFinalizer);

        doThrow(IOException.class).when(manifestFinalizer).writeResourcesToManifest(any(), any(), any(), any());

        final boolean result = instance.writeFilenamesAndChecksumsToMetadata(handle, objectResources, metadataResource,
                errorReports::add);

        assertThat(result, is(false));
        assertThat(errorReports.isEmpty(), is(false));
        assertThat(errorReports.get(0), allOf(
                hasProperty("exception", is(instanceOf(IOException.class))),
                hasProperty("errorStatus", is(ErrorStatus.IO_EXCEPTION))
        ));
    }

    @Test
    public void writeFilenamesAndChecksumsToMetadataShouldLogAnySaxException() throws IOException, TransformerException, SAXException {
        final List<ErrorReport> errorReports = Lists.newArrayList();
        final InputStream manifest =ObjectHarvesterOperationsTest.class.getResourceAsStream("/oai/manifest.xml");
        final FileStorageHandle handle = mock(FileStorageHandle.class);
        when(handle.getFile("manifest.initial.xml")).thenReturn(manifest);
        final ByteArrayOutputStream sip = new ByteArrayOutputStream();
        when(handle.getOutputStream("manifest.xml")).thenReturn(sip);
        final ManifestFinalizer manifestFinalizer = mock(ManifestFinalizer.class);
        final ObjectResource metadataResource = mock(ObjectResource.class);
        final List<ObjectResource> objectResources = Lists.newArrayList(new ObjectResource());
        final ObjectHarvesterOperations instance = new ObjectHarvesterOperations(
                mock(FileStorage.class), mock(HttpFetcher.class), mock(ResponseHandlerFactory.class), mock(XsltTransformer.class),
                mock(ObjectHarvesterResourceOperations.class),
                manifestFinalizer);


        doThrow(SAXException.class).when(manifestFinalizer).writeResourcesToManifest(any(), any(), any(), any());

        final boolean result = instance.writeFilenamesAndChecksumsToMetadata(handle, objectResources, metadataResource,
                errorReports::add);

        assertThat(result, is(false));
        assertThat(errorReports.isEmpty(), is(false));
        assertThat(errorReports.get(0), allOf(
                hasProperty("exception", is(instanceOf(SAXException.class))),
                hasProperty("errorStatus", is(ErrorStatus.XML_PARSING_ERROR))
        ));
    }
}