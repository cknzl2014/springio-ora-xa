package example.test;

import java.io.IOException;

import javax.jms.JMSException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import example.test.LoadTestAmqToOaq.MyConfiguration;

@RunWith(SpringRunner.class)
@Transactional
@DirtiesContext
@ContextConfiguration(classes = MyConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
public class LoadTestAmqToOaq extends TestBase {

	private static String IN_AMQ_QUEUE_NAME = "TEST.IN1";

	@TestConfiguration
	@Import({ BaseTestConfiguration.class })
	@ImportResource({ "config/test-applicationContext.xml" })
	public static class MyConfiguration {
	}

	@Test
	public void testSendAmqMessages()
			throws InterruptedException, JMSException, IOException, TransformerException {
		for (int i = 0; i < 1000; i++) {
			sendAmqMessage(IN_AMQ_QUEUE_NAME, TEST_XML, true);
		}
	}
}
