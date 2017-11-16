package example.test;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jms.core.JmsTemplate;

@TestConfiguration
// @ComponentScan("example")
public class BaseTestConfiguration {

	@Autowired
	@Qualifier("ActiveMQConnectionFactory")
	@Lazy
	ConnectionFactory amqConnectionFactory;

	@Autowired
	@Qualifier("OracleAQConnectionFactory")
	@Lazy
	ConnectionFactory oaqConnectionFactory;

	@Bean
	public JmsTemplate amqJmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate(amqConnectionFactory);
		return jmsTemplate;
	}

	@Bean
	public JmsTemplate oaqJmsTemplate() {
		JmsTemplate jmsTemplate = new JmsTemplate(oaqConnectionFactory);
		return jmsTemplate;
	}

	@Bean
	@ConditionalOnMissingBean(search = SearchStrategy.CURRENT)
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
