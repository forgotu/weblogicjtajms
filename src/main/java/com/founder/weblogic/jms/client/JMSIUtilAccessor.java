package com.founder.weblogic.jms.client;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.JMSException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JMSIUtilAccessor implements FounderTransation {
	protected final static Logger logger = LoggerFactory.getLogger(JMSIUtilAccessor.class);
	public final static String SESSION = "session";
	public final static String JMS_CONNECTION = "jms_connection";
	public final static String DATABASE_CONNECTION = "database_connection";
	public final static String DATABASE_STATEMENT = "database_statement";
	public final static String MessageProducer = "messageproducer";
	public final static String CONTEXT = "CONTEXT";
	public final static String TRANSACTION_TIMEOUT = "20000";

	private static final Properties props = new Properties();
	private  Context context;
	public  Context getContext() {
		return context;
	}

	private UserTransaction xact;

	public JMSIUtilAccessor() {
		
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.PROVIDER_URL, props.getProperty(Context.PROVIDER_URL));
		environment.put(Context.INITIAL_CONTEXT_FACTORY, props.getProperty(Context.INITIAL_CONTEXT_FACTORY));
		try {
			context = new InitialContext(environment);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
   
	public JMSIUtilAccessor(Context context) {
		this.context=context;
	}
	
	static{
		try {
			props.load(JMSIUtilAccessor.class.getResourceAsStream("jms.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void startTransation() {


		try {
			xact = (UserTransaction) context.lookup("javax.transaction.UserTransaction");
		} catch (NamingException e) {
			logger.error("NamingException start transation fail", e);
			throw new RuntimeException(e);
		}
		
		try {
			xact.begin();
		} catch (NotSupportedException | SystemException e) {
			logger.error("NotSupportedException", e);
			throw new RuntimeException(e);
		}

	}
	
	public void startTransation(Context context) {


		try {
			xact = (UserTransaction) context.lookup("javax.transaction.UserTransaction");
			try {
				xact.setTransactionTimeout(Integer.parseInt(props.getProperty("TransactionTimeout",TRANSACTION_TIMEOUT)));
			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NamingException e) {
			logger.error("NamingException start transation fail", e);
			throw new RuntimeException(e);
		}
		
		try {
			xact.begin();
		} catch (NotSupportedException | SystemException e) {
			logger.error("NotSupportedException", e);
			throw new RuntimeException(e);
		}

	}

	public abstract void execute() throws Exception;

	public void run() {
		startTransation();
		try {
			execute();
		} catch (Throwable e) {
			commitOrRollbackTransation(false);
			logger.error(" xa transation rollbak ", e);
			throw new RuntimeException(e);
		}
		commitOrRollbackTransation(true);
	}

	
	
	public void commitOrRollbackTransation(Boolean commit) {
		if (commit) {
			try {
				xact.commit();
			} catch (SecurityException | IllegalStateException | RollbackException | HeuristicMixedException
					| HeuristicRollbackException | SystemException e) {
				logger.error(" xa commit fail", e);
				throw new RuntimeException(e);
			}
		} else {
			try {
				xact.rollback();
			} catch (IllegalStateException | SecurityException | SystemException e) {
				logger.error(" xa rollback fail", e);
				throw new RuntimeException(e);
			}
			;
		}

	}

	public static void closeSession(Session session) {
		if (session != null) {
			try {
				session.close();
			} catch (JMSException ex) {
				logger.error("Could not close JMS Session", ex);
			} catch (Throwable ex) {
				logger.error("Unexpected exception on closing JMS Session", ex);
			}
		}
	}

	public static  void close(Object... obj) {
		HashMap<String, Object> map = new HashMap<String, Object>();

		for (Object object : obj) {
			if (object != null) {
				if (object instanceof Session) {
					map.put(SESSION, object);
					continue;
				}
				if (object instanceof java.sql.Connection) {
					map.put(DATABASE_CONNECTION, object);
					continue;
				}
				if (object instanceof Connection) {
					map.put(JMS_CONNECTION, object);
					continue;
				}
				if (object instanceof Statement) {
					map.put(DATABASE_STATEMENT, object);
					continue;
				}
				if (object instanceof MessageProducer ) {
					map.put(MessageProducer, object);
					continue;
				}
				if (object instanceof Context ) {
					map.put(CONTEXT, object);
					continue;
				}

			}
		}
		Object object = null;
		object = map.get(DATABASE_STATEMENT);
		if (object != null) {
			Statement Statement = (Statement) object;
			try {
				Statement.close();
			} catch (SQLException e) {
				logger.error("Unexpected exception on closing JMS Session", e);
			}

		}
		object = map.get(SESSION);
		if (object != null) {
		}
		object = map.get(JMS_CONNECTION);
		if (object != null) {
			releaseConnection((Connection) object);
		}
		object = map.get(DATABASE_CONNECTION);
		if (object != null) {
			releaseConnection((java.sql.Connection) object);
		}
		object = map.get(MessageProducer);
		if (object != null) {
			MessageProducer send =(javax.jms.MessageProducer) object;
			try {
				send.close();
			} catch (JMSException e) {
				logger.error("Could not close JMS Connection", e);
				throw new RuntimeException(e);
			}
		}
		object = map.get(CONTEXT);
		if (object != null) {
			Context context = (Context) object;
			try {
				context.close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Could Close the Context", e);
				throw new RuntimeException(e);
			}

		}

	}

	public static void releaseConnection(Connection con) {
		if (con == null) {
			return;
		}
		try {
			con.close();
		} catch (Throwable ex) {
			logger.error("Could not close JMS Connection", ex);
		}
	}

	public static void releaseConnection(java.sql.Connection con) {
		if (con == null) {
			return;
		}
		try {
			con.close();
		} catch (Throwable ex) {
			logger.error("Could not close JMS Connection", ex);
			throw new RuntimeException(ex);
		}
	}


}
