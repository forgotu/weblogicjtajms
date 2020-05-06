package com.founder.weblogic.jms.client;
/**对weblogic jta 事务进行封装*/
public interface FounderTransation {

	public void startTransation();

	public void execute() throws Exception;

	public void commitOrRollbackTransation(Boolean commit);

	

}
