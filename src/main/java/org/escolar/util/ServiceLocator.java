/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.escolar.util;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.escolar.service.AlunoService;
import org.escolar.service.FinanceiroService;
import org.escolar.service.LocationRotaService;
import org.escolar.service.LocationService;
import org.escolar.service.MemberRegistration;


/**
 *
 * @author Abimael Fidencio
 */
public class ServiceLocator {

    private InitialContext jndiContext;
    private static ServiceLocator instance;

    private ServiceLocator() {
        try {
            
            Properties props = new Properties();


            jndiContext = new InitialContext(props);
            
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public static ServiceLocator getInstance() {
        if (instance == null || instance.jndiContext == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }

    public MemberRegistration getEjb(String simpleNameImpl, String nameInterface) throws NamingException {
    		Constant c = new Constant();
    	
    		Object ejbHome2 = (Object) jndiContext.lookup(c.getContextoGlobalEJB() + c.getBarra() 
                                + c.getProjeto() + c.getBarra() 
                                + simpleNameImpl +"!"  + nameInterface);

            return (MemberRegistration) ejbHome2;
        }
    
    public Object getEjb2(String simpleNameImpl, String nameInterface) throws NamingException {
		Constant c = new Constant();
	
		Object ejbHome2 = (Object) jndiContext.lookup(Constant.ContextoGlobalEJB + Constant.barra
                            + Constant.Projeto + Constant.barra 
                            + simpleNameImpl +"!"  + nameInterface);

        return ejbHome2;
 }
    
    public LocationService getLocationService(String simpleNameImpl, String nameInterface) throws NamingException {
		Constant c = new Constant();
	
		Object ejbHome2 = (Object) jndiContext.lookup(c.getContextoGlobalEJB() + c.getBarra() 
                            + c.getProjeto() + c.getBarra() 
                            + simpleNameImpl +"!"  + nameInterface);

        return (LocationService) ejbHome2;
    }
    
    public LocationRotaService getLocationRotaService(String simpleNameImpl, String nameInterface) throws NamingException {
		Constant c = new Constant();
	
		Object ejbHome2 = (Object) jndiContext.lookup(c.getContextoGlobalEJB() + c.getBarra() 
                            + c.getProjeto() + c.getBarra() 
                            + simpleNameImpl +"!"  + nameInterface);

        return (LocationRotaService) ejbHome2;
    }
    
    public AlunoService getAlunoService(String simpleNameImpl, String nameInterface) throws NamingException {
		Constant c = new Constant();
	
		Object ejbHome2 = (Object) jndiContext.lookup(c.getContextoGlobalEJB() + c.getBarra() 
                            + c.getProjeto() + c.getBarra() 
                            + simpleNameImpl +"!"  + nameInterface);

        return (AlunoService) ejbHome2;
    }
    
    public FinanceiroService getFinanceiroService(String simpleNameImpl, String nameInterface) throws NamingException {
		Constant c = new Constant();
	
		Object ejbHome2 = (Object) jndiContext.lookup(c.getContextoGlobalEJB() + c.getBarra() 
                            + c.getProjeto() + c.getBarra() 
                            + simpleNameImpl +"!"  + nameInterface);

        return (FinanceiroService) ejbHome2;
    }
    
    public Object getEjbGeneric(String simpleNameImpl, String nameInterface) throws NamingException {
		Constant c = new Constant();
	
		Object ejbHome2 = (Object) jndiContext.lookup(
							Constant.ContextoGlobalEJB 
							+ Constant.barra
                            + Constant.Projeto + Constant.barra 
                            + simpleNameImpl +"!"  + nameInterface);

        return ejbHome2;
 }
}
