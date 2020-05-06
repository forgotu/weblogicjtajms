package test.t;

import java.sql.Statement;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.jms.MessageProducer;
import com.founder.weblogic.jms.client.JMSIUtilAccessor;
import com.founder.weblogic.jms.client.JMSUtil;

public class FounderBusinessLogicDemo extends JMSIUtilAccessor {
	public FounderBusinessLogicDemo(Context ct) {
		super();
		this.ct = ct;
	}

	Context ct;

	@Override
	public void execute() throws Exception {

		Connection conn = JMSUtil.getJMSConnection(ct);
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest = JMSUtil.lookUp(ct,"dest_queue");
      
		MessageProducer producer = session.createProducer(dest);
		Message message1 = session.createTextMessage("Hi world");
		System.out.println("the first send:" + new java.util.Date());
		producer.send(message1);
		
		Destination datasource = JMSUtil.lookUp(ct,"jdbc/fcoltp01FCBDataSource");
		DataSource dataSource2 =(DataSource) datasource;
		java.sql.Connection connection = dataSource2.getConnection();
		Statement createStatement = connection.createStatement();
		createStatement.execute("insert into B values('xqs','0507')");
		System.out.println("the second send:" + new java.util.Date());
		close(conn,session,producer);

	}

	public static void main(String[] args) throws NamingException {
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.PROVIDER_URL, "t3://192.168.1.22:8001");
		environment.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
		Context ct=new InitialContext(environment);
		new FounderBusinessLogicDemo(ct).run();
	}
}
