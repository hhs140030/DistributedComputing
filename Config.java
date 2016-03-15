
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * This class reads the configuration file and extracts the content for it's
 * node id as per the format given in project description. node id ragnes from
 * [0 totalNumOfNodes-1] where totalNumOfNodes is the total number of nodes in
 * distributed network.
 */
public class Config {
	/* Id of my node */
	private int myNodeId;
	/* Total number of nodes in distributed network */
	private int numOfNodes;
	/* periodic delay for sending the messages */
	private int delay;
	/* Limit to number of messages it can send */
	private int numOfMessages;
	/* hostanme of all the systems in distributed network */
	private ArrayList<String> hostnames;
	/* port at which each node listending */
	private ArrayList<Integer> ports;
	/* my hostname */
	private String myHostname;
	/* port at which this node is listening */
	private int myPort;
	/* Information to connect other nodes. */
	private Connection connect;

	/**
	 * Constructor
	 * 
	 * @param nodeId
	 *            node id of this system in the network
	 * 
	 * @param fileName
	 *            name of configuration file.
	 */
	public Config(int nodeId, String fileName) {
		// TODO Auto-generated constructor stub
		this.myNodeId = nodeId;
		readFile(fileName);
		printEverything();
	}

	/**
	 * This implementation prints everything which is extracted from
	 * configuration file for node with id myNodeId. This is for debugging
	 * purpose.
	 */
	private void printEverything() {
		// TODO Auto-generated method stub
		System.out.println("Mynode id-" + myNodeId);
		System.out.println("Hostname- " + myHostname + "\nPort: " + myPort);
		System.out.println(numOfNodes + ": " + numOfMessages + ": " + delay);
		for (int i = 0; i < hostnames.size(); i++) {
			System.out.println(i + ": " + hostnames.get(i) + ": " + ports.get(i));
		}
	}

	/**
	 * This implementation reads the entire file and extracts the information of
	 * this node(id stored in myNodeId via constructor). More information in
	 * project description on format of the configuration file and which
	 * informatoin is related to a node.
	 */
	private void readFile(String fileName) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			if (reader == null) {
				System.out.println("can't read file: " + fileName);
				return;
			}
			boolean first = true;
			int count = 0;
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.split("#")[0]; // in case line contains comments as
											// in " 1 2 3 #something"
				line = line.trim();
				if (line.isEmpty() || line.charAt(0) == '#')
					continue; // in case line starts with # which needs to
								// excluded as comments.

				// valid lines;
				if (first) {// first part of configuration file
					String tokens[] = line.split("\\s+");
					if (tokens.length != 3) {
						System.out.println("config files is not as per input format...\nExiting...");
						return;
					}
					numOfNodes = Integer.parseInt(tokens[0]);
					numOfMessages = Integer.parseInt(tokens[1]);
					delay = Integer.parseInt(tokens[2]);
					hostnames = new ArrayList<String>(numOfNodes);
					ports = new ArrayList<Integer>(numOfNodes);
					first = false;
				} else { // second part of configuration file
					String tokens[] = line.split("\\s+");
					if (tokens.length != 3) {
						System.out.println(
								"config files is not as per input format...\nNode id hostname port\nExiting...");
						return;
					}
					int index = Integer.parseInt(tokens[0]);
					if (index == myNodeId) {
						myHostname = tokens[1];
						myPort = Integer.parseInt(tokens[2]);
					}

					hostnames.add(index, tokens[1]);
					ports.add(index, Integer.parseInt(tokens[2]));
					count++;
					if (count == numOfNodes)
						break;
				}

			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}// end of readFile method.

	/**
	 * @return the myNodeId
	 */
	public int getMyNodeId() {
		return myNodeId;
	}

	/**
	 * @return the numOfNodes
	 */
	public int getNumOfNodes() {
		return numOfNodes;
	}

	/**
	 * @return the delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * @return the numOfMessages
	 */
	public int getNumOfMessages() {
		return numOfMessages;
	}

	/**
	 * @return the hostnames
	 */
	public ArrayList<String> getHostnames() {
		return hostnames;
	}

	/**
	 * @return the ports
	 */
	public ArrayList<Integer> getPorts() {
		return ports;
	}

	/**
	 * @return the myHostname
	 */
	public String getMyHostname() {
		return myHostname;
	}

	/**
	 * @return the myPort
	 */
	public int getMyPort() {
		return myPort;
	}

	/**
	 * @return the connect
	 */
	public Connection getConnect() {
		return connect;
	}

	/**
	 * @param connect
	 *            setter for Connection.
	 */
	public void setConnect(Connection connect) {
		this.connect = connect;
	}
}// end of Class
