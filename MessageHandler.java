
/**
 * This class is responsible for creating threads to creates the connections
 * with it's neighboring node and keeping it alive till the end of the
 * execution. 2 connections are established with the each neighboring node, one
 * for mutual exclusion service and another for totally ordered broadcast
 * service to hide the implementation details with each other.
 */
public class MessageHandler implements Runnable {

	private MutualProvider mutualProvider;
	private Config config;
	private TOB tob;

	/**
	 * Constructor.
	 * 
	 * @param config keeps the information which is extracted from configuration
	 * file.
	 * 
	 * @param mutualProvider object of the class which provides mutual exclusion
	 * service.
	 * 
	 * @param tob it provides totally ordered services.
	 */
	public MessageHandler(Config config, MutualProvider mutualProvider, TOB tob) {
		this.mutualProvider = mutualProvider;
		this.config = config;
		this.tob = tob;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() This implementation creates 2
	 * connections(threads) with each nodes, one for mutual exclusion service
	 * and another for totally ordered broadcast service to hide the
	 * implementation details with each other.
	 */
	public void run() {
		for (int i = 0; i < config.getNumOfNodes(); i++) {
			if (i != config.getMyNodeId()) {
				Reader r = new Reader(config, config.getConnect().getTobConnectInputStream().get(i), tob);
				new Thread(r).start();
			}
		}
		for (int i = 0; i < config.getNumOfNodes(); i++) {
			if (i != config.getMyNodeId()) {
				Reader r = new Reader(config, config.getConnect().getMutualConnectInputStream().get(i), mutualProvider);
				new Thread(r).start();
			}
		}
	}
}
