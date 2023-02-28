package app.hailstorm.stats;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

public class StatCollector {
	private static Stat runningStats = new Stat(new HashMap<>(),new HashMap<>(), new HashMap<>(), new HashMap<>());
	private static Stat summaryStats = new Stat(new HashMap<>(),new HashMap<>(), new HashMap<>(), new HashMap<>());
	
	public static Stat getRunningStats() {
		return runningStats;
	}

	private static AtomicInteger getCounter(String className) {
		AtomicInteger counter = runningStats.scenarioCounter().get(className);
		if(counter == null) {
			counter = new AtomicInteger();
			runningStats.scenarioCounter().put(className,counter);
		}
		return counter;
	}
	
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

	private static AtomicInteger getFailureCounter(String className, String stepName) {
		runningStats.failureCounter().putIfAbsent(className, new HashMap<>());
		Map<String,AtomicInteger> stepMap = runningStats.failureCounter().get(className);
		
		stepMap.putIfAbsent(stepName, new AtomicInteger());
		AtomicInteger counter = stepMap.get(stepName);
		
		return counter;
	}

	public static void add(String className, Duration timeTaken) {
		// getCounter(className).incrementAndGet();
	}
	public static void add(String className) {
		getCounter(className).incrementAndGet();
	}
	public static void add(String className, String stepName, Duration timeTaken) {
		getStats(className, stepName).addValue(timeTaken.toMillis());
	}
	public static void reportError(String canonicalName, String stepName, String errorString) {
		getFailureCounter(canonicalName,stepName).incrementAndGet();
		runningStats.errorString().get(canonicalName).get(stepName).get(errorString).incrementAndGet();
	}
	public static void resetRunningStats() {
		runningStats = new Stat(new HashMap<>(),new HashMap<>(), new HashMap<>(), new HashMap<>());
	}
	public static Stat getSummaryStats() {
		return summaryStats;
	}
}
