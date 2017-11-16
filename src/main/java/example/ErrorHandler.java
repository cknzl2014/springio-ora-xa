package example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.support.ErrorMessage;

public class ErrorHandler {
	private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

	@Autowired
	Environment env;

	private boolean logStackTrace = false;

	@ServiceActivator
	public void handleError(ErrorMessage message) {
		Throwable t = message.getPayload();
		if (t != null && logStackTrace) {
			log.error("Error: {}\n{}", message, t);
		} else {
			log.error("Error: {}", message);
		}
	}

	public boolean isLogStackTrace() {
		return logStackTrace;
	}

	public void setLogStackTrace(boolean logStackTrace) {
		this.logStackTrace = logStackTrace;
	}
}
