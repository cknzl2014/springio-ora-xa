package example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQSession;
import org.springframework.jms.support.destination.CachingDestinationResolver;
import org.springframework.jms.support.destination.DestinationResolutionException;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.Assert;

import oracle.jms.AQjmsSession;

public class CachingJmsDestinationResolver implements CachingDestinationResolver {

	private DestinationResolver dynamicDestinationResolver = new DynamicDestinationResolver();

	private final Map<DestinationKey, Destination> destinationCache = new ConcurrentHashMap<DestinationKey, Destination>(
			50);

	@Override
	public Destination resolveDestinationName(Session session, String destinationName,
			boolean pubSubDomain) throws JMSException {
		Assert.notNull(destinationName, "Destination name must not be null");
		DestinationKey destKey = null;
		if (session instanceof ActiveMQSession) {
			destKey = new DestinationKey(QueueType.ActiveMQ, destinationName);
		} else if (session instanceof AQjmsSession) {
			destKey = new DestinationKey(QueueType.OracleAQ, destinationName);
		} else {
			throw new DestinationResolutionException(
					"Unsupported Session type [" + session.getClass().getName() + "]");
		}
		Destination dest = this.destinationCache.get(destKey);
		if (dest != null) {
			validateDestination(dest, destinationName, pubSubDomain);
		} else {
			dest = this.dynamicDestinationResolver.resolveDestinationName(session, destinationName,
					pubSubDomain);
			this.destinationCache.put(destKey, dest);
		}
		return dest;
	}

	@Override
	public void removeFromCache(String destinationName) {
		for (QueueType queueType : QueueType.values()) {
			this.destinationCache.remove(new DestinationKey(queueType, destinationName));
		}
	}

	@Override
	public void clearCache() {
		this.destinationCache.clear();
	}

	/**
	 * Validate the given Destination object, checking whether it matches the
	 * expected type.
	 * 
	 * @param destination
	 *            the Destination object to validate
	 * @param destinationName
	 *            the name of the destination
	 * @param pubSubDomain
	 *            {@code true} if a Topic is expected, {@code false} in case of a
	 *            Queue
	 */
	protected void validateDestination(Destination destination, String destinationName,
			boolean pubSubDomain) {
		Class<?> targetClass = Queue.class;
		if (pubSubDomain) {
			targetClass = Topic.class;
		}
		if (!targetClass.isInstance(destination)) {
			throw new DestinationResolutionException("Destination [" + destinationName
					+ "] is not of expected type [" + targetClass.getName() + "]");
		}
	}

	private class DestinationKey {
		private QueueType queueType;
		private String destination;

		public DestinationKey(QueueType queueType, String destination) {
			this.queueType = queueType;
			this.destination = destination;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((destination == null) ? 0 : destination.hashCode());
			result = prime * result + ((queueType == null) ? 0 : queueType.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DestinationKey other = (DestinationKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (destination == null) {
				if (other.destination != null)
					return false;
			} else if (!destination.equals(other.destination))
				return false;
			if (queueType != other.queueType)
				return false;
			return true;
		}

		private CachingJmsDestinationResolver getOuterType() {
			return CachingJmsDestinationResolver.this;
		}
	}

}
