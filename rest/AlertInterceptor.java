/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package monitoring.rest;

import monitoring.ejb.BusinessEJB;
import monitoring.entities.Alerte;
import jakarta.ejb.EJB;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

/**
 *
 * @author LENOVO
 */
@Interceptor
public class AlertInterceptor {
    @EJB
    private BusinessEJB businessEJB;

    @AroundInvoke
    public Object reassignAlert(InvocationContext context) throws Exception {
        if (context.getMethod().getName().equals("registerAlert")) {
            Alerte alerte = (Alerte) context.getParameters()[0];
            businessEJB.reassignAlert(alerte);
        }
        return context.proceed();
    }
}
