package app.hailstorm.stats;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

public record Stat(
		Map<String, AtomicInteger> scenarioCounter,
		Map<String, Map<String, SynchronizedSummaryStatistics>> stepStats, 
		Map<String, Map<String, AtomicInteger>> failureCounter,
		Map<String, Map<String, Map<String,AtomicInteger>>> errorString
		
		) {

}
