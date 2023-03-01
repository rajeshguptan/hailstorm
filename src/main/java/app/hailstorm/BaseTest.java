/*
 * 
 */
package app.hailstorm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import app.hailstorm.stats.StatCollector;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseTest.
 */
public abstract class BaseTest implements TestCase {
	
	/**
	 * Instantiates a new base test.
	 */
	public BaseTest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/** The step map. */
	private Map<String, StepInfo> stepMap = new LinkedHashMap<>();
	
	/** The scenario sequence number. */
	private Integer scenarioSequenceNumber;
	
	/** The random. */
	ThreadLocalRandom random = ThreadLocalRandom.current();

	/**
	 * The  StepInfo.
	 */
	private record StepInfo(String stepName, Integer pauseSec,
			ExceptionThrowingVoidSupplier supplier) {
	};

	/**
	 * Step.
	 *
	 * @param stepName the step name
	 * @param minSleepSec the min sleep sec
	 * @param maxSleepSec the max sleep sec
	 * @param supplier the supplier
	 */
	protected void step(String stepName, Integer minSleepSec, Integer maxSleepSec,
			ExceptionThrowingVoidSupplier supplier) {
		stepMap.put(stepName, new StepInfo(stepName, minSleepSec, supplier));
	}

	/**
	 * Execute.
	 */
	@Override
	public void execute() {
		for (Map.Entry mapElement : stepMap.entrySet()) {
			String stepName = (String) mapElement.getKey();
			StepInfo stepInfo = (StepInfo) mapElement.getValue();
			ExceptionThrowingVoidSupplier step = stepInfo.supplier;
			try {
				Thread.sleep(random.nextLong(stepInfo.pauseSec));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Instant stepStartTime = Instant.now();
			boolean result = true;
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			try {
				step.execute();
			} catch (Exception e) {
				e.printStackTrace(pw);
				result = false;
			}
			Instant stepEndTime = Instant.now();
			Duration timeTaken = Duration.between(stepStartTime, stepEndTime);

			StatCollector.add(this.getClass().getCanonicalName(), stepName, timeTaken);
			if (!result) {
				// The step had failed.
				String errorString = sw.toString().split("\n")[0];
				if (errorString.length() > 120) {
					errorString = errorString.substring(0, 120);
				}
				StatCollector.reportError(this.getClass().getCanonicalName(), stepName, errorString);
				// Skip executing the remaining steps.
				break;
			}
		}
	}

	/**
	 * Before each.
	 */
	@Override
	public void beforeEach() {
		// NOOP
	}

	/**
	 * Inits the.
	 */
	@Override
	public void init() {
		// NOOP
	}

	/**
	 * After each.
	 */
	@Override
	public void afterEach() {
		// NOOP
	}

	/**
	 * Shutdown.
	 */
	@Override
	public void shutdown() {
		// NOOP
	}

	/**
	 * Gets the scenario sequence number.
	 *
	 * @return the scenario sequence number
	 */
	public Integer getScenarioSequenceNumber() {
		return scenarioSequenceNumber;
	}

	/**
	 * Sets the scenario sequence number.
	 *
	 * @param scenarioSequenceNumber the new scenario sequence number
	 */
	@Override
	public void setScenarioSequenceNumber(Integer scenarioSequenceNumber) {
		this.scenarioSequenceNumber = scenarioSequenceNumber;
	}

}
