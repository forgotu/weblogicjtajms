package com.founder.weblogic.jms.client;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
/**just for demo*****/
@Deprecated
public class JMSUtilOld{
	private static Context context;
    private static final Properties props=new Properties();
    private static ConnectionFactory connectionFactory=null;
    private static final String ConnectionFactoryConfig="weblogic.jms.XAConnectionFactory";
    static{
    	try {
			props.load(JMSUtilOld.class.getResourceAsStream("jms.properties"));
			Hashtable<String, String> environment =new Hashtable<String, String>();
			environment.put(Context.PROVIDER_URL, props.getProperty(Context.PROVIDER_URL));
			environment.put(Context.INITIAL_CONTEXT_FACTORY, props.getProperty(Context.INITIAL_CONTEXT_FACTORY));
			context=new InitialContext(environment);
			connectionFactory=(ConnectionFactory) context.lookup(ConnectionFactoryConfig);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    public static Connection getJMSConnection() throws JMSException{
      return connectionFactory.createConnection();
    }
    
    @SuppressWarnings("unchecked")
	public static  <T> T lookUp(String jndiName) throws JMSException, NamingException{
        return (T) context.lookup(jndiName);
      }
    
    public static  <T> T lookUp(Context context,String jndiName) throws JMSException, NamingException{
        return (T) context.lookup(jndiName);
      }
    
    
	
	
	
}
