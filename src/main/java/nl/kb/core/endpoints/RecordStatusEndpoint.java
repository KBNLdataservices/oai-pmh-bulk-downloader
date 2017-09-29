package nl.kb.core.endpoints;

import nl.kb.core.model.record.RecordReporter;
import nl.kb.core.model.reporting.ErrorReporter;
import nl.kb.core.model.reporting.ExcelReportBuilder;
import nl.kb.core.model.reporting.ExcelReportDao;
import nl.kb.core.model.statuscodes.ErrorStatus;
import nl.kb.core.model.statuscodes.ProcessStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Path("/record-status")
public class RecordStatusEndpoint {
    private final RecordReporter recordReporter;
    private final ErrorReporter errorReporter;
    private final ExcelReportDao excelReportDao;
    private final ExcelReportBuilder excelReportBuilder;

    public RecordStatusEndpoint(RecordReporter recordReporter, ErrorReporter errorReporter,
                                ExcelReportDao excelReportDao, ExcelReportBuilder excelReportBuilder) {
        this.recordReporter = recordReporter;
        this.errorReporter = errorReporter;
        this.excelReportDao = excelReportDao;
        this.excelReportBuilder = excelReportBuilder;
    }

    @GET
    @Produces("application/json")
    public Response getStatus() {

        return Response.ok(recordReporter.getStatusUpdate().getData()).build();
    }

    @GET
    @Produces("application/json")
    @Path("/errors")
    public Response getErrorStatus() {

        return Response.ok(errorReporter.getStatusUpdate().getData()).build();
    }

    @GET
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @Path("/errors/{repositoryId}/{sheetName}.xlsx")
    public Response getErrorReport(@PathParam("repositoryId") Integer repositoryId,
                                   @PathParam("sheetName") String sheetName) {
        final StreamingOutput output = out ->
                excelReportBuilder.build(sheetName, excelReportDao.getExcelForRepository(repositoryId), out);

        return Response.ok(output).build();
    }

    @GET
    @Produces("application/json")
    @Path("/status-codes")
    public Response getStatusCodes() {

        final Map<String, Map<Integer, String>> statusCodes = new HashMap<>();

        statusCodes.put("errorStatuses",
                Stream.of(ErrorStatus.values()).collect(toMap(ErrorStatus::getCode, ErrorStatus::getStatus)));

        statusCodes.put("processStatuses",
                Stream.of(ProcessStatus.values()).collect(toMap(ProcessStatus::getCode, ProcessStatus::getStatus)));

        return Response
                .ok(statusCodes)
                .build();
    }
}
