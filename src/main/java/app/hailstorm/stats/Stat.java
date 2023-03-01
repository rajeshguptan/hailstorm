/*
 * 
 */
package app.hailstorm.stats;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

/**
 * The  Stat.
 * @param scenarioCounter TODO
 * @param stepStats TODO
 * @param failureCounter TODO
 * @param errorString TODO
 */
public record Stat(
		
		/** The scenario counter. 
		 *  Map<String, AtomicInteger>
		 * */
		Map<String, AtomicInteger> scenarioCounter,
		
		/** The step stats. */
		Map<String, Map<String, SynchronizedSummaryStatistics>> stepStats, 
		
		/** The failure counter. */
		Map<String, Map<String, AtomicInteger>> failureCounter,
		
		/** The error string. */
		Map<String, Map<String, Map<String,AtomicInteger>>> errorString
		
		) {

}
