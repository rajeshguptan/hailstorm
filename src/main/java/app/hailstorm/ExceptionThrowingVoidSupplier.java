package app.hailstorm;

@FunctionalInterface
public interface ExceptionThrowingVoidSupplier {

	/**
	 * Gets a result.
	 *
	 * @return a result
	 */
	void execute() throws Exception;
}
