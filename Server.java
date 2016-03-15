import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * This class based on incoming connection request(launches a thread for each
 * incoming reuqest), adds the connection information to corresponding service
 * provider's HashMap object.
 */
public class Server implements Runnable {

	/* socket information of incoming connection request. */
	private Socket socket;
	/* place where all connection information is stored. */
	private Connection connect;

	/**
	 * Constructor.
	 * 
	 * @param socket
	 *            information related to incoming connection request.
	 * @param connect
	 *            stores all incoming outgoing stream information for each
	 *            requests.
	 */
	public Server(Socket socket, Connection connect) {
		this.socket = socket;
		this.connect = connect;
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() this implementation based on incoming
	 *      connection request message type, adds the connection information to
	 *      corresponding Connection's HashMap object. Message type 'connect1' :
	 *      for totally ordered broadcast service provider. Message type
	 *      'coonect2' : for mutual exclusion service provider.
	 */
	public void run() {
		ObjectInputStream in = null;
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			Message m = (Message) in.readObject();
			if (m.getType().compareToIgnoreCase("connect1") == 0) { // for
																	// totally
																	// ordered
																	// broadcast
																	// service
																	// provider.
				connect.getTobConnectInputStream().put(m.getSender(), in);
				connect.getTobConnectOutputStream().put(m.getSender(), out);
			} else if (m.getType().compareToIgnoreCase("connect2") == 0) { // for
																			// mutual
																			// exclusion
																			// service
																			// provider.
				connect.getMutualConnectInputStream().put(m.getSender(), in);
				connect.getMutualConnectOutputStream().put(m.getSender(), out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
