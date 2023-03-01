package app.hailstorm;

// TODO: Auto-generated Javadoc
/**
 * The Interface TestCase.
 */
public interface TestCase {
	
	/**
	 * Before each.
	 */
	public void beforeEach();
	
	/**
	 * Inits the.
	 */
	public void init();
	
	/**
	 * After each.
	 */
	public void afterEach();
	
	/**
	 * Shutdown.
	 */
	public void shutdown();
	
	/**
	 * Execute.
	 */
	public void execute();
	
	/**
	 * Define.
	 */
	public void define();
	
	/**
	 * Sets the scenario sequence number.
	 *
	 * @param scenarioSequenceNumber the new scenario sequence number
	 */
	void setScenarioSequenceNumber(Integer scenarioSequenceNumber);
}
