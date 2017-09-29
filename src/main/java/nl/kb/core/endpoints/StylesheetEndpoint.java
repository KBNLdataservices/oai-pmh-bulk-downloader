package nl.kb.core.endpoints;

import nl.kb.core.model.stylesheet.StylesheetDao;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("/stylesheets")
public class StylesheetEndpoint {
    private final StylesheetDao stylesheetDao;

    public StylesheetEndpoint(StylesheetDao stylesheetDao) {
        this.stylesheetDao = stylesheetDao;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(stylesheetDao.list()).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        final String name = fileDetail.getFileName();
        if (stylesheetDao.list().stream().anyMatch(stylesheet -> stylesheet.getName().equalsIgnoreCase(name))) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(
                    new ErrorResponse(String.format("Stylesheet with name %s already exists", name),
                            Response.Status.BAD_REQUEST.getStatusCode())
                ).build();

        }

        return Response.ok("{}").build();
    }
}
