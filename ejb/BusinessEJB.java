package monitoring.ejb;

/**
 *
 * @author LENOVO
 */

import monitoring.entities.Alerte;
import monitoring.entities.Correspondance;
import jakarta.ejb.Stateless;
import jakarta.faces.annotation.RequestMap;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Stateless
public class BusinessEJB {

    @PersistenceContext
    private EntityManager em;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void registerAlert(@RequestMap Alerte alerte) {
        em.persist(alerte);
    }

    public void updateAlert(String id, Alerte updatedAlerte) {
        Alerte alerte = em.find(Alerte.class, id);
        if (alerte != null) {
            alerte.setRaiseTime(updatedAlerte.getRaiseTime());
            alerte.setPlatform(updatedAlerte.getPlatform());
            alerte.setTarget(updatedAlerte.getTarget());
            alerte.setSource(updatedAlerte.getSource());
            alerte.setSendTime(updatedAlerte.getSendTime());
            alerte.setCode(updatedAlerte.getCode());
            alerte.setDesc(updatedAlerte.getDesc());
            alerte.setStatut(updatedAlerte.getStatut());
            em.merge(alerte);
        }
        else {
            throw new NoResultException("Alert with ID " + id + " not found.");
        }
    }

    public List<Alerte> listAlerts(String platform, Date start, Date end) {
        // Log the parameters for debugging
        System.out.println("listAlerts called with platform: " + platform + ", start: " + start + ", end: " + end);

        List<Alerte> alerts = em.createQuery("SELECT a FROM Alerte a WHERE a.platform = :platform AND a.raiseTime BETWEEN :start AND :end", Alerte.class)
                .setParameter("platform", platform)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();

        // Log the number of results found
        System.out.println("Number of alerts found: " + alerts.size());

        return alerts;
    }
    
    public void reassignAlert(Alerte alerte) {
        Correspondance correspondance = em.find(Correspondance.class, alerte.getCode());
        if (correspondance != null) {
            alerte.setTarget(correspondance.getTarget());
        }
    }
}
