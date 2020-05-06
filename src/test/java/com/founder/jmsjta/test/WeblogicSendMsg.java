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

import com.founder.weblogic.jms.client.JMSUtilOld;

public class WeblogicSendMsg {

	public static void main(String[] args) throws JMSException, NamingException, NotSupportedException, SystemException,
			SecurityException, IllegalStateException, RollbackException, HeuristicMixedException,
			HeuristicRollbackException, InterruptedException, SQLException {
		Connection conn = JMSUtilOld.getJMSConnection();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		/***WEBLOGIC分布式事务管理器JNDI****/
		UserTransaction xact = JMSUtilOld.lookUp("javax.transaction.UserTransaction");

		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.PROVIDER_URL, "t3://192.168.1.22:8005");
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
		environment.put(Context.SECURITY_PRINCIPAL, "weblogic");
		environment.put(Context.SECURITY_CREDENTIALS, "founder123");
		Context context = new InitialContext(environment);
		javax.sql.DataSource dataSource = (javax.sql.DataSource) JMSUtilOld.lookUp(context, "jeecgboot");
		System.out.println("dataSource:" + dataSource);
		java.sql.Connection myconn = null;

		System.out.println("myconn:");
		myconn = dataSource.getConnection();

		context.close();
        /**开启分布式事务**/
		xact.begin();
		Statement stmt = myconn.createStatement();
		stmt.execute("insert into A VALUES(11,'C')");

		Destination dest = JMSUtilOld.lookUp("dest_queue");
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
