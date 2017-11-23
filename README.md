# ActiveMQ to OracleAQ using Atomikos XA transaction manager
## goal of this project
The goal of this project is to get a configuration that provides a fast throughput using XA.
## dependencies
You need
* connection to a Oracle 11+ database
* connection to a ActiveMQ instance
## problem
The initial problem was a very low throughput of about 1 to 2 messages per second.
When profiling we see that for every message a new physical connection is established, and the Oracle metadata query is issued.
We used the ojdbc6.jar at that time.
## solution
## Step 1: use latest oracle client libraries
The first step was to use the latest Oracle client libraries.
Download the latest Oracle 12 client libraries and set ORACLE_HOME.
This increased the throughput to 10 msgs/s.
Profiling revealed that there were optimizations getting the metadata (they use a stored procedure now).
## Step 2: use latest oracle client libraries
The second step was improving the connection pooling according to article  (http://thinkfunctional.blogspot.ch/2012/05/atomikos-and-oracle-aq-pooling-problem.html):

	<bean id="oraXaDataSource" primary="true"
		class="oracle.jdbc.xa.client.OracleXADataSource" destroy-method="close">
		<property name="URL" value="${oracle.url}" />
		<property name="user" value="${oracle.username}" />
		<property name="password" value="${oracle.password}" />
	</bean>
	
	<bean id="atomikosOraclaDataSource"
		class="org.springframework.boot.jta.atomikos.AtomikosDataSourceBean">
		<property name="uniqueResourceName" value="xaOracleAQ" />
		<property name="xaDataSource" ref="oraXaDataSource" />
		<property name="poolSize" value="5" />
	</bean>

	<bean id="OracleAQConnectionFactory" class="oracle.jms.AQjmsFactory" factory-method="getConnectionFactory">
		<constructor-arg ref="atomikosOraclaDataSource" />
	</bean>

This configuration alone resultet in exceptions because of 'auto-commit' of the Oracle connection.
## Step 3: set autoCommit to false
The third step was to set the following java system property (see https://docs.oracle.com/database/121/JAJDB/oracle/jdbc/OracleConnection.html#CONNECTION_PROPERTY_AUTOCOMMIT):
`-DautoCommit=false`
But then the throughput went down to 1 to 2 msg/s again.
## Step 4: set oracle.jdbc.autoCommitSpecCompliant to false
The last step was to set the following java system property (see https://docs.oracle.com/database/121/JAJDB/oracle/jdbc/OracleConnection.html#CONNECTION_PROPERTY_AUTO_COMMIT_SPEC_COMPLIANT):
`-Doracle.jdbc.autoCommitSpecCompliant=false`
Now we get a throughput of 80 msgs/s.

# Conclusion
The setting of `oracle.jdbc.autoCommitSpecCompliant` to false is not elegant, but solved the problem.
We have to investigate further to see how we can get around this problem without setting `oracle.jdbc.autoCommitSpecCompliant` to false.
