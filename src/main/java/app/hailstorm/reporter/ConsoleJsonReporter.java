package app.hailstorm.reporter;

import java.lang.reflect.Type;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import app.hailstorm.stats.Stat;

public class ConsoleJsonReporter implements StatReporter {
	private class SynchronizedSummaryStatisticsSerializer implements JsonSerializer<SynchronizedSummaryStatistics> {
		@Override
		public JsonElement serialize(SynchronizedSummaryStatistics src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			JsonObject obj = new JsonObject();
			obj.addProperty("count", src.getN());
			obj.addProperty("mean", String.format("%.2f", src.getMean()));
			
			return obj;
			
		}
	}

	@Override
	public void reportRunningStat(Stat runningStats) {
		if (runningStats.stepStats().size() == 0)
			return;

		GsonBuilder gson = new GsonBuilder();

		gson.registerTypeAdapter(SynchronizedSummaryStatistics.class, new SynchronizedSummaryStatisticsSerializer());
		String json = gson.setPrettyPrinting().create().toJson(runningStats);
		System.out.println(json);

	}

	@Override
	public void reportSummaryStat(Stat summaryStats) {
		if (summaryStats.stepStats().size() == 0)
			return;

		GsonBuilder gson = new GsonBuilder();

		gson.registerTypeAdapter(SynchronizedSummaryStatistics.class, new SynchronizedSummaryStatisticsSerializer());
		String json = gson.setPrettyPrinting().create().toJson(summaryStats);
		System.out.println(json);
	}

}
