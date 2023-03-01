package app.hailstorm.reporter;

import java.lang.reflect.Type;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import app.hailstorm.stats.Stat;

// TODO: Auto-generated Javadoc
/**
 * The Class ConsoleJsonReporter.
 */
public class ConsoleJsonReporter implements StatReporter {
	
	/**
	 * Instantiates a new console json reporter.
	 */
	public ConsoleJsonReporter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * The Class SynchronizedSummaryStatisticsSerializer.
	 */
	private class SynchronizedSummaryStatisticsSerializer implements JsonSerializer<SynchronizedSummaryStatistics> {
		
		/**
		 * Serialize.
		 *
		 * @param src the src
		 * @param typeOfSrc the type of src
		 * @param context the context
		 * @return the json element
		 */
		@Override
		public JsonElement serialize(SynchronizedSummaryStatistics src, Type typeOfSrc,
				JsonSerializationContext context) {
			
			JsonObject obj = new JsonObject();
			obj.addProperty("count", src.getN());
			obj.addProperty("mean", String.format("%.2f", src.getMean()));
			
			return obj;
			
		}
	}

	/**
	 * Report running stat.
	 *
	 * @param runningStats the running stats
	 */
	@Override
	public void reportRunningStat(Stat runningStats) {
		if (runningStats.stepStats().size() == 0)
			return;

		GsonBuilder gson = new GsonBuilder();

		gson.registerTypeAdapter(SynchronizedSummaryStatistics.class, new SynchronizedSummaryStatisticsSerializer());
		String json = gson.setPrettyPrinting().create().toJson(runningStats);
		System.out.println(json);

	}

	/**
	 * Report summary stat.
	 *
	 * @param summaryStats the summary stats
	 */
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
