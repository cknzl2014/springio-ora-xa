package example.test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class TestBase {

	static final Logger log = LoggerFactory.getLogger(TestBase.class);

	protected static final String TEST_PROPERTY_VALUE = "TESTVALUE";
	protected static final String TEST_PROPERTY_KEY = "TESTPROPERTY";
	protected static final String TEST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" //
			+ "<Document>" //
			+ "  <Element>Hallo äöü</Element>" //
			+ "</Document>";

	@Autowired
	@Qualifier("amqJmsTemplate")
	protected JmsTemplate amqJmsTemplate;

	@Autowired
	@Qualifier("oaqJmsTemplate")
	protected JmsTemplate oaqJmsTemplate;

	@Autowired
	@Qualifier("oracleDataSource")
	@Lazy
	protected DataSource oraclerDataSource;

	protected void sendAmqMessage(String queueName, final String xml, final boolean compressed) {
		sendMessage(this.amqJmsTemplate, queueName, xml, compressed, null);
	}

	protected String getAmqMessage(String queueName) {
		return getMessage(this.amqJmsTemplate, queueName);

	}

	protected void sendAmqMessage(String queueName, final String xml, final boolean compressed,
			Map<String, Object> headerProperties) {
		sendMessage(this.amqJmsTemplate, queueName, xml, compressed, headerProperties);
	}

	protected void sendOaqMessage(String queueName, final String xml, final boolean compressed) {
		sendMessage(this.oaqJmsTemplate, queueName, xml, compressed, null);
	}

	protected void sendOaqMessage(String queueName, final String xml, final boolean compressed,
			Map<String, Object> headerProperties) {
		sendMessage(this.oaqJmsTemplate, queueName, xml, compressed, headerProperties);
	}

	protected void sendMessage(JmsTemplate jmsTemplate, String queueName, final String xml,
			final boolean compressed, Map<String, Object> headerProperties) {
		log.info(">>>>>>>>>>>>>>");
		jmsTemplate.send(queueName, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message;
				if (compressed) {
					message = session.createBytesMessage();
					((BytesMessage) message).writeBytes(StringCompressor.compress(xml));
					message.setStringProperty("COMPRESSED", "true");
				} else {
					if (jmsTemplate == oaqJmsTemplate) {
						message = session.createBytesMessage();
						((BytesMessage) message).writeBytes(xml.getBytes(StandardCharsets.UTF_8));
					} else {
						message = session.createTextMessage();
						((TextMessage) message).setText(xml);
					}
				}
				message.setStringProperty(TEST_PROPERTY_KEY, TEST_PROPERTY_VALUE);
				if (headerProperties != null) {
					for (Map.Entry<String, Object> entry : headerProperties.entrySet()) {
						message.setObjectProperty(entry.getKey(), entry.getValue());
					}
				}
				return message;
			}

		});
	}

	protected String getMessage(JmsTemplate jmsTemplate, String queueName) {
		jmsTemplate.setReceiveTimeout(5000L);
		String messageAsString = "";
		try {
			TextMessage textMessage = (TextMessage) jmsTemplate.receive(queueName);
			messageAsString = textMessage.getText();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return messageAsString;
	}
}