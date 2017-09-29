package nl.kb.core.endpoints;

import nl.kb.core.model.stylesheet.Stylesheet;
import nl.kb.core.model.stylesheet.StylesheetManager;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

@Path("/stylesheets")
public class StylesheetEndpoint {
    private final StylesheetManager stylesheetManager;

    public StylesheetEndpoint(StylesheetManager stylesheetManager) {
        this.stylesheetManager = stylesheetManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(stylesheetManager.list()).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(
            @FormDataParam("file") InputStream data,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        final String name = fileDetail.getFileName();
        if (stylesheetManager.exists(name)) {
            return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(
                    new ErrorResponse(String.format("Stylesheet with name %s already exists", name),
                            Response.Status.BAD_REQUEST.getStatusCode())
                ).build();

        }

        try {
            final Stylesheet stylesheet = stylesheetManager.create(name, data);
            return Response.ok(stylesheet).build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{name}")
    public Response update(
            @FormDataParam("file") InputStream data,
            @PathParam("name") String name) {

        if (!stylesheetManager.exists(name)) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(
                            new ErrorResponse(String.format("Stylesheet with name %s doest not exist", name),
                                    Response.Status.BAD_REQUEST.getStatusCode())
                    ).build();

        }

        try {
            final Stylesheet stylesheet = stylesheetManager.update(name, data);
            return Response.ok(stylesheet).build();

        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                    .build();
        }
    }
}
