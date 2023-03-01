package app.hailstorm;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import app.hailstorm.stats.StatCollector;

// TODO: Auto-generated Javadoc
/**
 * The Class VUser.
 */
class VUser implements Runnable {

	/** The stopped. */
	public boolean stopped = false;
	
	/** The test start time. */
	public Instant testStartTime = Instant.now();
	
	/** The test end time. */
	public Instant testEndTime;
	
	/** The iteration. */
	public Integer iteration = 0;

	/** The test case. */
	private final TestCase testCase;
	
	/** The scenario counter. */
	private static Map<String, AtomicInteger> scenarioCounter = new HashMap<>();
	
	/**
	 * Instantiates a new v user.
	 *
	 * @param testCase the test case
	 */
	public VUser(TestCase testCase) {
		this.testCase = testCase;
	}

	/**
	 * Run.
	 */
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