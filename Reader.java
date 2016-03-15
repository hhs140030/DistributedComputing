
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for reading incoming messages from it's neighboring
 * nodes. Each node has separate TCP connection between them.
 */
public class Reader implements Runnable {

	/* object input stream reader */
	private ObjectInputStream in;
	/* configuration file information. */
	private Config config;
	/* mutual exclusion provider. */
	private MutualProvider mutualProvider;
	/* totally ordered broadcast provider. */
	private TOB tob;

	/**
	 * Constructor to read messages for totally ordered broadcast service.
	 */
	public Reader(Config config, ObjectInputStream in, TOB tob) {
		this.config = config;
		this.in = in;
		this.tob = tob;
		mutualProvider = null;
	}

	/**
	 * Constructor to read messages for mutual exclusion service.
	 */
	public Reader(Config config, ObjectInputStream in, MutualProvider mutualProvider) {
		this.config = config;
		this.in = in;
		this.mutualProvider = mutualProvider;
		this.tob = null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() This implementation simply reads the object
	 *      and add to it's corresponding service queue.
	 */
	public void run() {
		// System.out.println("launching reader "+);
		while (true) {
			try {
				synchronized (this) {
					Message m = (Message) in.readObject();
					if (mutualProvider == null) { // for totally ordered
													// service.
						tob.addMessage(m);
					} else { // for mutual exclusion provider.
						mutualProvider.addMessage(m);
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
