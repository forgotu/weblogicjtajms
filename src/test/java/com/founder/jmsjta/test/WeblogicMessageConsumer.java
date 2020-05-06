package com.founder.jmsjta.test;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.NamingException;

import com.founder.weblogic.jms.client.JMSUtilOld;

public class WeblogicMessageConsumer{
	MessageConsumer consumer ;
	 Connection conn;
	 Session session;
	public WeblogicMessageConsumer() throws JMSException, NamingException{
		conn= JMSUtilOld.getJMSConnection();
		session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		 Destination dest = JMSUtilOld.lookUp("dest_queue");
		 
		consumer= session.createConsumer(dest);
		consumer.setMessageListener(new MessageListener(){

			public void onMessage(Message arg0) {
				System.out.println(arg0.toString());
				
			}});
		conn.start();
	}

   public static void main(String[] args) throws JMSException, NamingException {
	   WeblogicMessageConsumer c= new WeblogicMessageConsumer();
	  
}
}
