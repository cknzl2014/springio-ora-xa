package example;

import org.springframework.context.SmartLifecycle;

/**
 * Active this Service if you want to force a kill after 2 seconds
 */
// @Service
public class Killer implements SmartLifecycle {

	private boolean running = false;

	@Override
	public void start() {
		System.out.println("***** Started Killer");
		this.running = true;
		Runnable task = () -> {
			try {
				Thread.sleep(2000);
				System.out.println("***** Killing Application");
				Runtime.getRuntime().halt(1);
			} catch (InterruptedException e) {
			}
		};
		Thread thread = new Thread(task);
		thread.start();
	}

	@Override
	public void stop() {
		this.running = false;
	}

	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
	}

}
