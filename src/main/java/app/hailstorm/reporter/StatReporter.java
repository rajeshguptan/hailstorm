package app.hailstorm.reporter;

import app.hailstorm.stats.Stat;

public interface StatReporter {
	public void reportRunningStat(Stat runningStats);
	public void reportSummaryStat(Stat summaryStats);
}
