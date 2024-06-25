/**
 *
 * @author LENOVO
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monitoring.rest;

import monitoring.ejb.BusinessEJB;
import monitoring.entities.Alerte;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Service REST pour les alertes
 */
@Path("/alert")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AlertService {

    private static final String JDNI_NAME = "java:global/your-app-name/BusinessEJB";

    @EJB
    private BusinessEJB businessEJB;

    @POST
    @Path("/register")
    public void registerAlert(Alerte alerte) {
        businessEJB.registerAlert(alerte);
    }

    @POST
    @Path("/update/{id}")
    public void updateAlert(@PathParam("id") String id, Alerte alerte) {
        businessEJB.updateAlert(id, alerte);
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAlerts(@QueryParam("platform") String platform,
                               @QueryParam("start") @DefaultValue("") String start,
                               @QueryParam("end") @DefaultValue("") String end) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // Parse the start and end dates
            Date startDate = start.isEmpty() ? null : formatter.parse(start);
            Date endDate = end.isEmpty() ? new Date() : formatter.parse(end);
            System.out.println("start => " + startDate);

            if (businessEJB == null) {
                try {
                    businessEJB = (BusinessEJB) new InitialContext().lookup(JDNI_NAME);
                } catch (NamingException ex) {
                    Logger.getLogger(AlertService.class.getName()).log(Level.SEVERE, null, ex);
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Map.of("error", true, "message", "EJB lookup failed")).build();
                }
            }

            // Call the listAlerts method of BusinessEJB
            List<Alerte> alerts = businessEJB.listAlerts(platform, startDate, endDate);

            return Response.ok(Map.of("success", true, "data", alerts)).build();
        } catch (ParseException e) {
            System.out.println("Parse Error => " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", true, "message", "Date format should be yyyy-MM-dd")).build();
        } catch (Exception e) {
            System.out.println("Error => " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(Map.of("error", true, "message", e.getMessage())).build();
        }
    }
}
