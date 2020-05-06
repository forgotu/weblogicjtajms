package com.founder.weblogic.jms.client;

import java.sql.Statement;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.NamingException;

public class JMSUtil {

	private static final String ConnectionFactoryConfig = "weblogic.jms.XAConnectionFactory";

	
	
	public static Connection getJMSConnection(Context context) throws JMSException, NamingException {
		return ((ConnectionFactory) context.lookup(ConnectionFactoryConfig)).createConnection();
	}

	
	@SuppressWarnings("unchecked")
	public static <T> T lookUp(Context context, String jndiName) throws JMSException, NamingException {
		return (T) context.lookup(jndiName);
	}

	public static Session getSession(Connection connection) throws JMSException, NamingException {
		return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	public static Session getSession(Connection connection, boolean transacted) throws JMSException, NamingException {
		return connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
	}

	public static Session getSession(Connection connection, boolean transacted, int acknowledge)
			throws JMSException, NamingException {
		return connection.createSession(transacted, acknowledge);
	}
	
	public static MessageProducer getProducer(Session session, Destination dest)
			throws JMSException, NamingException {
		
		return session.createProducer(dest);
	}
	
	
	
	public static MessageProducer getProducer(Session session,Context context, String dest)
			throws JMSException, NamingException {
		Destination destion = lookUp(context,dest);
		return session.createProducer(destion);
	}
	
	public static void close(Session session) {
		JMSIUtilAccessor.close(session);
	}
	
	public static void close(Session session,MessageProducer messageProducer) {
		JMSIUtilAccessor.close(session,messageProducer);
	}
	public static void close(Session session,MessageProducer messageProducer,Connection connection) {
		JMSIUtilAccessor.close(session,messageProducer,connection);
	}
	
	public static void close(Session session,MessageProducer messageProducer,Connection connection,Statement stmt) {
		JMSIUtilAccessor.close(session,messageProducer,connection,stmt);
	}
	
	public static void close(Session session,MessageProducer messageProducer,Connection connection,Statement stmt,java.sql.Connection con) {
		JMSIUtilAccessor.close(session,messageProducer,connection,stmt,con);
	}


}
