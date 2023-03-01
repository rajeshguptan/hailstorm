package app.hailstorm.stats;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

// TODO: Auto-generated Javadoc
/**
 * The Class StatCollector.
 */
public class StatCollector {
	
	/**
	 * Instantiates a new stat collector.
	 */
	public StatCollector() {
		super();
		// TODO Auto-generated constructor stub
	}

	/** The running stats. */
	private static Stat runningStats = new Stat(new HashMap<>(),new HashMap<>(), new HashMap<>(), new HashMap<>());
	
	/** The summary stats. */
	private static Stat summaryStats = new Stat(new HashMap<>(),new HashMap<>(), new HashMap<>(), new HashMap<>());
	
	/**
	 * Gets the running stats.
	 *
	 * @return the running stats
	 */
	public static Stat getRunningStats() {
		return runningStats;
	}

	/**
	 * Gets the counter.
	 *
	 * @param className the class name
	 * @return the counter
	 */
	private static AtomicInteger getCounter(String className) {
		AtomicInteger counter = runningStats.scenarioCounter().get(className);
		if(counter == null) {
			counter = new AtomicInteger();
			runningStats.scenarioCounter().put(className,counter);
		}
		return counter;
	}
	
	/**
	 * Gets the stats.
	 *
	 * @param className the class name
	 * @param stepName the step name
	 * @return the stats
	 */
	private static SynchronizedSummaryStatistics getStats(String className, String stepName) {
		Map<String, SynchronizedSummaryStatistics> runningStat = runningStats.stepStats().get(className);

		if (runningStat == null) {
			runningStat = new HashMap<String, SynchronizedSummaryStatistics>();
			runningStats.stepStats().put(className, runningStat);
		}

		SynchronizedSummaryStatistics runningStepStat = runningStat.get(stepName);
		if (runningStepStat == null) {
			runningStepStat = new SynchronizedSummaryStatistics();
			runningStat.put(stepName, runningStepStat);
		}

		return runningStepStat;
	}

	/**
	 * Gets the failure counter.
	 *
	 * @param className the class name
	 * @param stepName the step name
	 * @return the failure counter
	 */
	private static AtomicInteger getFailureCounter(String className, String stepName) {
		runningStats.failureCounter().putIfAbsent(className, new HashMap<>());
		Map<String,AtomicInteger> stepMap = runningStats.failureCounter().get(className);
		
		stepMap.putIfAbsent(stepName, new AtomicInteger());
		AtomicInteger counter = stepMap.get(stepName);
		
		return counter;
	}

	/**
	 * Adds the.
	 *
	 * @param className the class name
	 * @param timeTaken the time taken
	 */
	public static void add(String className, Duration timeTaken) {
		// getCounter(className).incrementAndGet();
	}
	
	/**
	 * Adds the.
	 *
	 * @param className the class name
	 */
	public static void add(String className) {
		getCounter(className).incrementAndGet();
	}
	
	/**
	 * Adds the.
	 *
	 * @param className the class name
	 * @param stepName the step name
	 * @param timeTaken the time taken
	 */
	public static void add(String className, String stepName, Duration timeTaken) {
		getStats(className, stepName).addValue(timeTaken.toMillis());
	}
	
	/**
	 * Report error.
	 *
	 * @param canonicalName the canonical name
	 * @param stepName the step name
	 * @param errorString the error string
	 */
	public static void reportError(String canonicalName, String stepName, String errorString) {
		getFailureCounter(canonicalName,stepName).incrementAndGet();
		runningStats.errorString().get(canonicalName).get(stepName).get(errorString).incrementAndGet();
	}
	
	/**
	 * Reset running stats.
	 */
	public static void resetRunningStats() {
		runningStats = new Stat(new HashMap<>(),new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	
	/**
	 * Gets the summary stats.
	 *
	 * @return the summary stats
	 */
	public static Stat getSummaryStats() {
		return summaryStats;
	}
}
