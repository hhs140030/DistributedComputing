
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class creates the socket connection with all it's neighbors.
 */
public class Graph implements Runnable {

	/* Maintains socket connection information with neighbors. */
	private Connection connect;
	/* Keeps the information of configuration file. */
	private Config config;
	/* This indicates whether all connections are established or not. */
	private boolean finished = false;
	/* to check if connection is successful or not. */
	private boolean isServerUp = false;
	/* It maintains socket information. */
	private Socket socket;
	/* to indicate application in debug mode. */
	private boolean debug = false;

	/**
	 * Constructor
	 * 
	 * @param config
	 *            maintains configuration information of this node.
	 */
	public Graph(Config config) {
		this.config = config;
		connect = new Connection();

	}

	/**
	 * This implementation launches two threads; first 'server' which waits for
	 * connection request from node whose id is lower than this nodes id. second
	 * 'client': it initiates connection with nodes in the network with id's
	 * greater than it's own.
	 * 
	 * @return connection information.
	 * @throws InterruptedException
	 */
	public Connection createConnection() throws InterruptedException {
		try {
			Thread t = new Thread(this);
			t.setName("server");
			t.start();
			t = new Thread(this);
			t.setName("client");
			t.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// wating till all connections are established.
		while (connect.getMutualConnectInputStream().size() != config.getNumOfNodes() - 1
				|| connect.getMutualConnectOutputStream().size() != config.getNumOfNodes() - 1
				|| connect.getTobConnectInputStream().size() != config.getNumOfNodes() - 1
				|| connect.getTobConnectOutputStream().size() != config.getNumOfNodes() - 1) {
			Thread.sleep(100);
		}
		finished = true;
		return connect;
	}

	/**
	 * It has two parts first for thread name 'server' and another for thread
	 * name 'client'. 
	 * 'server' : which waits for connection request from node
	 * whose id is lower than this nodes id. 
	 * 'client': it initiates connection
	 * with nodes in the network with id's greater than it's own.
	 */
	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		if (threadName.compareToIgnoreCase("server") == 0) {  //server thread part.
			if (config.getMyNodeId() == 0) {
				return;
			}
			try {
				// TODO Auto-generated method stub
				ServerSocket socket = new ServerSocket(config.getMyPort());
				int count = 2 * (config.getMyNodeId());
				while (count != 0) {
					try {
						new Server(socket.accept(), connect); //wating to accept the conenction.
						count--;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (IOException ex) {
				Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else if (threadName.compareToIgnoreCase("client") == 0) { //client thread part.
			for (int i = config.getMyNodeId() + 1; i < config.getNumOfNodes(); i++) {
				try {
					isServerUp = false;
					while (isServerUp == false) {
						System.out.println("Trying to connect1 to " + i);
						isConnected(config.getHostnames().get(i), config.getPorts().get(i));
						if (!isServerUp) {
							Thread.sleep(2000);
						}
					}
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					Message m = new Message(config.getMyNodeId(), i, "connect1", 0);
					out.writeObject(m);
					connect.getTobConnectInputStream().put(i, in);
					connect.getTobConnectOutputStream().put(i, out);
					isServerUp = false;
					while (isServerUp == false) { //checking if connections is establised otherwise retry after 2 seconds.
						System.out.println("Trying to connect2 to " + i);
						isConnected(config.getHostnames().get(i), config.getPorts().get(i));
						if (!isServerUp) {
							Thread.sleep(2000);
						}
					}
					in = new ObjectInputStream(socket.getInputStream());
					out = new ObjectOutputStream(socket.getOutputStream());
					m = new Message(config.getMyNodeId(), i, "connect2", 0);
					out.writeObject(m);
					connect.getMutualConnectInputStream().put(i, in);
					connect.getMutualConnectOutputStream().put(i, out);
					isServerUp = false;
				} catch (IOException ex) {
					Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
				} catch (InterruptedException ex) {
					Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	/**
	 * This implementation attempts to make connection with node 'hostname'.
	 * 
	 * @param hostname
	 *            host name of the node with which it trying to conenct to.
	 * @param port
	 *            port at which other node listens at.
	 */
	public void isConnected(String hostname, int port) {
		try {
			if (debug) {
				socket = new Socket("localhost", port);
			} else {
				socket = new Socket(hostname, port);
			}
			isServerUp = true;
		} catch (Exception e) {
			socket = null;
			isServerUp = false;
			// e.printStackTrace();
			if (debug) {
				System.out.println("failed in client client 2");
			}
		}
	}
}
