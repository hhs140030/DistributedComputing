import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * This class contains socket information for connection it's neighbour nodes in distributed network. 
 */
public class Connection {
	/**
	 * This HashMaps contains the information related to inputStream and outputStream socket connection for totally ordered broadcast service.
	 * Here Integer(key) represents nodeId in network and values is it's corresponding stream.   
	 */
	private HashMap<Integer, ObjectInputStream>tobConnectInputStream;
	private HashMap<Integer, ObjectOutputStream> tobConnectOutputStream;
	
	/**
	 * This HashMaps contains the information related to inputStream and outputStream socket connection for mutually exclusion service.
	 * Here Integer(key) represents nodeId in network and values is it's corresponding stream. 
	 */
	private HashMap<Integer, ObjectInputStream>mutualConnectInputStream;
	private HashMap<Integer, ObjectOutputStream> mutualConnectOutputStream;
	
	/**
	 * Constructor.
	 */
	public Connection(){
		tobConnectInputStream = new HashMap<Integer, ObjectInputStream>();
		mutualConnectInputStream = new HashMap<Integer, ObjectInputStream>();
		tobConnectOutputStream = new HashMap<Integer, ObjectOutputStream>();
		mutualConnectOutputStream = new HashMap<Integer, ObjectOutputStream>(); 
	}

	/**
	 * @return the tobConnectInputStream
	 */
	public HashMap<Integer, ObjectInputStream> getTobConnectInputStream() {
		return tobConnectInputStream;
	}

	/**
	 * @return the tobConnectOutputStream
	 */
	public HashMap<Integer, ObjectOutputStream> getTobConnectOutputStream() {
		return tobConnectOutputStream;
	}

	/**
	 * @return the mutualConnectInputStream
	 */
	public HashMap<Integer, ObjectInputStream> getMutualConnectInputStream() {
		return mutualConnectInputStream;
	}

	/**
	 * @return the mutualConnectOutputStream
	 */
	public HashMap<Integer, ObjectOutputStream> getMutualConnectOutputStream() {
		return mutualConnectOutputStream;
	}

}
