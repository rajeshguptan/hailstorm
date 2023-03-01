package app.hailstorm.reporter;

import app.hailstorm.stats.Stat;

// TODO: Auto-generated Javadoc
/**
 * The Interface StatReporter.
 */
public interface StatReporter {
	
	/**
	 * Report running stat.
	 *
	 * @param runningStats the running stats
	 */
	public void reportRunningStat(Stat runningStats);
	
	/**
	 * Report summary stat.
	 *
	 * @param summaryStats the summary stats
	 */
	public void reportSummaryStat(Stat summaryStats);
}
