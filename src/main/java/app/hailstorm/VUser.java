package app.hailstorm;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import app.hailstorm.stats.StatCollector;

class VUser implements Runnable {

	public boolean stopped = false;
	public Instant testStartTime = Instant.now();
	public Instant testEndTime;
	public Integer iteration = 0;

	private final TestCase testCase;
	private static Map<String, AtomicInteger> scenarioCounter = new HashMap<>();
	
	public VUser(TestCase testCase) {
		this.testCase = testCase;
	}

	public void run() {
		while (true) {
			String className = testCase.getClass().getCanonicalName();
			scenarioCounter.putIfAbsent(className, new AtomicInteger());
			testCase.setScenarioSequenceNumber(scenarioCounter.get(className).incrementAndGet());

			testCase.beforeEach();
			testCase.execute();

			testCase.afterEach();

			StatCollector.add(className);
			if (Instant.now().isAfter(testEndTime)) {
				stopped = true;
				break;				
			}
			if(iteration > 0) {
				stopped = true;
				break;
			}
		}

	}
}