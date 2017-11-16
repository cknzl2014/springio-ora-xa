package example.test;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;

public abstract class QueueInitializer {

	@Autowired
	@Qualifier("amqJmsTemplate")
	protected JmsTemplate amqJmsTemplate;

	@Autowired
	@Qualifier("oaqJmsTemplate")
	protected JmsTemplate oaqJmsTemplate;

	@PostConstruct
	abstract public void init();

	protected void emptyAmqQueue(String queueName) {
		emptyQueue(amqJmsTemplate, queueName);
	}

	protected void emptyOaqQueue(String queueName) {
		emptyQueue(oaqJmsTemplate, queueName);
	}

	private void emptyQueue(JmsTemplate jmsTemplate, String queueName) {
		try {
			jmsTemplate.setReceiveTimeout(1000);
			while (true) {
				if (jmsTemplate.receive(queueName) == null) {
					break;
				}
			}
		} catch (Exception e) {
		}
	}
}
