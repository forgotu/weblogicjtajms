package com.founder.weblogic.jms.client;

public interface FounderTransation {

	public void startTransation();

	public void execute() throws Exception;

	public void commitOrRollbackTransation(Boolean commit);

	

}
