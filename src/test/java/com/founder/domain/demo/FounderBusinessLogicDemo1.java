package com.founder.domain.demo;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.MessageProducer;
import com.founder.weblogic.jms.client.JMSIUtilAccessor;
import com.founder.weblogic.jms.client.JMSUtil;

public class FounderBusinessLogicDemo1 extends JMSIUtilAccessor {

	public FounderBusinessLogicDemo1(Context ct) {
		super(ct);
	}

	@Override
	public void execute() throws Exception {

		Connection conn = JMSUtil.getJMSConnection(getContext());
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = JMSUtil.lookUp(getContext(),"DistributedQueue-0");
      
		MessageProducer producer = session.createProducer(dest);
		Message message = session.createTextMessage("20200509");
		Message message1 = session.createTextMessage("My world2");
		producer.send(message);
		System.out.println("the first send:" + new java.util.Date());
		//Thread.sleep(100000);
		producer.send(message1);
		System.out.println("the second send:" + new java.util.Date());
		close(conn,session,producer,getContext());

	}

	public static void main(String[] args) throws NamingException {
		
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.PROVIDER_URL, "t3://192.168.1.22:8001");
		environment.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
		Context ct=new InitialContext(environment);

		new FounderBusinessLogicDemo1(ct).run();
		
	}
}
