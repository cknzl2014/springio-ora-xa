package example;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class Debugger {

	@Transformer
	public Message<?> debug(Message<?> msg) {
		return msg;
	}
}
