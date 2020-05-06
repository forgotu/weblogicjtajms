package com.founder.domain.demo;
import java.sql.Statement;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.jms.MessageProducer;
import com.founder.weblogic.jms.client.JMSIUtilAccessor;
import com.founder.weblogic.jms.client.JMSUtil;

public class FounderBusinessLogicDemoDatabaseAndJms extends JMSIUtilAccessor {

	public FounderBusinessLogicDemoDatabaseAndJms(Context ct) {
		super(ct);
	}

	@Override
	public void execute() throws Exception {
		Connection conn = JMSUtil.getJMSConnection(getContext());
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = JMSUtil.lookUp(getContext(),"dest_queue");
		MessageProducer producer = session.createProducer(dest);
		Message message = session.createTextMessage("20210509");
		producer.send(message);
		System.out.println("the first send:" + new java.util.Date());
		DataSource dataSource = JMSUtil.lookUp(getContext(),"jdbc/fcoltp01FCBDataSource");
		
		java.sql.Connection connection = dataSource.getConnection();
		Statement createStatement = connection.createStatement();
		createStatement.execute("insert into B values('xqs','sysdate')");
		
		
		//Thread.sleep(100000);

		System.out.println("insert into jms:" + new java.util.Date());
		close(conn,session,producer,getContext(),connection,createStatement);

	}

	public static void main(String[] args) throws NamingException {
		
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.PROVIDER_URL, "t3://192.168.1.22:8001");
		environment.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
		Context ct=new InitialContext(environment);

		new FounderBusinessLogicDemoDatabaseAndJms(ct).run();
		
	}
}
