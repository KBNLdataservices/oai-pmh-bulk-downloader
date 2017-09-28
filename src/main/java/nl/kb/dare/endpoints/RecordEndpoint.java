package nl.kb.dare.endpoints;

import nl.kb.dare.model.preproces.Record;
import nl.kb.dare.model.preproces.RecordDao;
import nl.kb.dare.model.preproces.RecordReporter;
import nl.kb.dare.model.reporting.ErrorReportDao;
import nl.kb.dare.model.reporting.StoredErrorReport;
import nl.kb.dare.model.statuscodes.ProcessStatus;
import nl.kb.dare.websocket.SocketNotifier;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/records")
public class RecordEndpoint {
    private final RecordDao recordDao;
    private final ErrorReportDao errorReportDao;
    private final RecordReporter recordReporter;
    private final SocketNotifier socketNotifier;

    public RecordEndpoint(RecordDao recordDao, ErrorReportDao errorReportDao,
                          RecordReporter recordReporter, SocketNotifier socketNotifier) {
        this.recordDao = recordDao;
        this.errorReportDao = errorReportDao;
        this.recordReporter = recordReporter;
        this.socketNotifier = socketNotifier;
    }

    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Response find(@QueryParam("q") String query) {
        return Response.ok(recordDao.query("%" + query + "%")).build();
    }



    @PUT
    @Path("/bulk-reset/{repositoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response bulkReset(@PathParam("repositoryId") Integer repositoryId) {

        errorReportDao.bulkDeleteForRepository(ProcessStatus.FAILED.getCode(), repositoryId);
        recordDao.bulkUpdateState(ProcessStatus.FAILED.getCode(), ProcessStatus.PENDING.getCode(),
                repositoryId);

        socketNotifier.notifyUpdate(recordReporter.getStatusUpdate());

        return Response.ok("{}").build();
    }

    @PUT
    @Path("/reset/{ipName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response reset(@PathParam("ipName") String ipName) {
        final Record record = recordDao.findByIpName(ipName);
        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        record.setState(ProcessStatus.PENDING);
        recordDao.updateState(record);
        errorReportDao.deleteForRecordId(record.getId());
        socketNotifier.notifyUpdate(recordReporter.getStatusUpdate());

        return Response.ok("{}").build();
    }

    @GET
    @Path("/status/{ipName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@PathParam("ipName") String ipName) {

        final Record record = recordDao.findByIpName(ipName);

        if (record == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final StoredErrorReport errorReport = errorReportDao.fetchForRecordId(record.getId());
        final HashMap<String, Object> result = new HashMap<>();
        result.put("record", record);
        result.put("errorReport", errorReport);
        return Response.ok(result).build();
    }
}
