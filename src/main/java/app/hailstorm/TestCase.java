package app.hailstorm;

public interface TestCase {
	public void beforeEach();
	public void init();
	public void afterEach();
	public void shutdown();
	
	public void execute();
	public void define();
	void setScenarioSequenceNumber(Integer scenarioSequenceNumber);
}
