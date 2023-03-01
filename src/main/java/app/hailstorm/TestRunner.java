package app.hailstorm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.reflections.Reflections;

import app.hailstorm.annotation.HailTest;
import app.hailstorm.reporter.StatReporter;
import app.hailstorm.stats.StatCollector;

// TODO: Auto-generated Javadoc
/**
 * The Class TestRunner.
 */
public class TestRunner {
	
	/**
	 * Instantiates a new test runner.
	 */
	public TestRunner() {
		super();
		// TODO Auto-generated constructor stub
	}

	/** The config. */
	private static Configuration config;
	
	/** The scenario initialized. */
	private static Map<String, AtomicBoolean> scenarioInitialized = new HashMap<>();
	
	/** The cli options. */
	private static CLIOptions cliOptions;
	
	/** The reporters. */
	private static List<StatReporter> reporters = new ArrayList<>();

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws InterruptedException the interrupted exception
	 * @throws ConfigurationException the configuration exception
	 * @throws SecurityException the security exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			InterruptedException, ConfigurationException, SecurityException, ClassNotFoundException, IOException {
		cliOptions = CLIOptions.getConfiguration(args);
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(cliOptions.configFile);
		String configFile = TestRunner.class.getClassLoader().getResource(cliOptions.configFile).getPath();
		System.out.println("Config FIle = " + configFile);
		PropertiesConfiguration config = new PropertiesConfiguration();
		config.read(new InputStreamReader(is));
		
		String[] reporterClass = config.getString("reporters").split(",");
		for (String className : reporterClass) {
			reporters.add((StatReporter) Class.forName(className).getConstructors()[0].newInstance());
		}
		
		List<VUser> userList = new ArrayList<>();

		String testPackage = config.getString("testPackage");
		Reflections reflections = new Reflections(testPackage);
		Set<Class<?>> hailTestClasses = reflections.getTypesAnnotatedWith(HailTest.class);
		Integer totalVUCount = 0;
		for (Class<?> testClass : hailTestClasses) {
			Integer vusers = config.getInt(testClass.getCanonicalName() + ".vusers",0);
			System.out.println(testClass.getCanonicalName() + ".vusers="+vusers);
			totalVUCount += vusers;
			String className = testClass.getCanonicalName();
			if (cliOptions.functionalMode)
				vusers = 1;
			System.out.println("DEBUG " + className + ",VUSERS=" + vusers);
			Constructor<TestCase> c = (Constructor<TestCase>) testClass.getConstructors()[0];
			for (int index = 0; index < vusers; index++) {
				TestCase test = c.newInstance();

				test.define();
				scenarioInitialized.putIfAbsent(className, new AtomicBoolean(false));
				if (!scenarioInitialized.get(className).get()) {
					System.out.println("Initializing class " + className);
					test.init();
					scenarioInitialized.get(className).set(true);
				}

				VUser vuser = new VUser(test);
				if (cliOptions.functionalMode) vuser.iteration = 1;
				vuser.testEndTime = Instant.now().plusSeconds(config.getInt("durationSec"));
				userList.add(vuser);
				
			}
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(totalVUCount);
		userList.forEach((vUser) -> {
			executor.submit(vUser);
		});
		
		try {			
			while (!executor.isShutdown()) {
				Thread.sleep(10000);
				boolean allStopped = true;
				for(VUser vuser : userList) {
					if(!vuser.stopped) {
						allStopped = false;;
					}
				}
				if(allStopped) {
					executor.shutdown();
				}
				reporters.forEach(r -> r.reportRunningStat(StatCollector.getRunningStats()));
				StatCollector.resetRunningStats();
			}
			reporters.forEach(r -> r.reportSummaryStat(StatCollector.getSummaryStats()));
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

	}

}
