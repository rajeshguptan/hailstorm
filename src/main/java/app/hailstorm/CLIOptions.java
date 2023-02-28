package app.hailstorm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

class CLIOptions {
	public String configFile = "";
	public int duration;
	public boolean functionalMode = false;
	public boolean verbose = false;
	public String outputFolder;
	public Instant testEndTime;
	public Instant testStartTime = Instant.now();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Configuration [").append(", duration=").append(duration).append(", functionalMode=")
				.append(functionalMode).append(", verbose=").append(verbose).append(", outputFolder=")
				.append(outputFolder).append("]");
		return builder.toString();
	}

	public static CLIOptions getConfiguration(String[] args) {
		CLIOptions config = new CLIOptions();
		Options options = new Options();

		options.addOption(new Option("c", "configFile", true, "Execution configuration file"));
		options.addOption(new Option("d", "duration", true, "Test duration in seconds, overrides configFile value"));
		options.addOption(new Option("o", "outputFolder", true, "Output Folder"));
		options.addOption(new Option("f", "functionalMode", false, "Run Test in functional mode"));
		options.addOption(new Option("h", "help", false, "Print Help"));
		options.addOption(new Option("x", "verbose", false, "Verbose Output for debugging"));

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("h")) {
				throw new ParseException("Invoked in Help Mode");
			}
			if (cmd.hasOption("x")) {
				config.verbose = true;
			}
			if (cmd.hasOption("f")) {
				config.functionalMode = true;
			}
			config.duration = Integer.parseInt(cmd.getOptionValue("duration", "0"));
			config.outputFolder = cmd.getOptionValue("outputFolder", "report");
			config.configFile = cmd.getOptionValue("configFile");
			if (config.duration > 0) {
				config.testEndTime = config.testStartTime.plusSeconds(config.duration);
			} else {
				config.testEndTime = config.testStartTime.plus(10, ChronoUnit.DAYS);
			}

		} catch (ParseException e) {
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("TestBench", options);
			System.exit(1);
		}
		return config;
	}
}