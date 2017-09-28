package nl.kb.dare.endpoints;

import nl.kb.dare.scheduledjobs.ObjectHarvestSchedulerDaemon;
import nl.kb.dare.websocket.socketupdate.ObjectHarvesterRunstateUpdate;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/workers")
public class ObjectHarvesterEndpoint {
    private final ObjectHarvestSchedulerDaemon objectHarvestSchedulerDaemon;

    public ObjectHarvesterEndpoint(ObjectHarvestSchedulerDaemon objectHarvestSchedulerDaemon) {

        this.objectHarvestSchedulerDaemon = objectHarvestSchedulerDaemon;
    }

    @PUT
    @Path("/start")
    @Produces(MediaType.APPLICATION_JSON)
    public Response start() {
        objectHarvestSchedulerDaemon.enable();
        return Response.ok("{}").build();
    }

    @PUT
    @Path("/disable")
    @Produces(MediaType.APPLICATION_JSON)
    public Response disable() {

        objectHarvestSchedulerDaemon.disable();
        return Response.ok("{}").build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {

        return Response.ok((new ObjectHarvesterRunstateUpdate(objectHarvestSchedulerDaemon.getRunState()))).build();
    }
}
