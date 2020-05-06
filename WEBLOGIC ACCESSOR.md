#          WEBLOGIC ACCESSOR 

说明：可以通过编写简单的java application来通过jndi访问weblogic的数据源、JMS目标、连接工厂、用户事务等。

1. Demo

```java
package com.founder.jmsjta.test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.founder.weblogic.jms.client.JMSUtil;

public class WeblogicSendMsg {

	public static void main(String[] args) throws JMSException, NamingException, NotSupportedException, SystemException,
			SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, InterruptedException, SQLException {
		Connection conn = JMSUtil.getJMSConnection();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		/***WEBLOGIC分布式事务管理器JNDI****/
		UserTransaction xact = JMSUtil.lookUp("javax.transaction.UserTransaction");

		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.PROVIDER_URL, "t3://192.168.1.22:8005");
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		environment.put(Context.SECURITY_PRINCIPAL, "weblogic");
		environment.put(Context.SECURITY_CREDENTIALS, "founder123");
		Context context = new InitialContext(environment);
		javax.sql.DataSource dataSource = (javax.sql.DataSource) JMSUtil.lookUp(context, "jeecgboot");
		System.out.println("dataSource:" + dataSource);
		java.sql.Connection myconn = null;

		System.out.println("myconn:");
		myconn = dataSource.getConnection();

		context.close();
        /**开启分布式事务**/
		xact.begin();
		Statement stmt = myconn.createStatement();
		stmt.execute("insert into A VALUES(11,'C')");

		Destination dest = JMSUtil.lookUp("dest_queue");
		MessageProducer producer = session.createProducer(dest);
		Message message = session.createTextMessage("Hello");
		Message message1 = session.createTextMessage("Hi");
		producer.send(message);
		System.out.println("the first send:" + new java.util.Date());
		// Thread.sleep(100000);
		producer.send(message1);
		System.out.println("the second send:" + new java.util.Date());
		/**提交分布式事务***/
		xact.commit();
		System.out.println("commit:" + new java.util.Date());
		producer.close();
		session.close();
		conn.close();
		stmt.close();
		myconn.close();
		System.out.println("finish:");
		;

	}

}

```

```java
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

public class JMSUtil{
	private static Context context;
    private static final Properties props=new Properties();
    private static ConnectionFactory connectionFactory=null;
    private static final String ConnectionFactoryConfig="weblogic.jms.XAConnectionFactory";
    static{
    	try {
			props.load(JMSUtil.class.getResourceAsStream("jms.properties"));
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

```

```java
java.naming.provider.url=t3://192.168.1.22:8001
java.naming.factory.initial=weblogic.jndi.WLInitialContextFactory
```

2. 运行

   如果单单运行weblogic jms，则仅仅需要依赖wlclient.jar和wljmsclient.jar；

   如果需要分布式事务，比如jms+sql，则需要依赖wlfullclient.jar和ojdbc6.jar