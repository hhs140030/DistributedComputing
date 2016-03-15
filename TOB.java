
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is implementation of totally ordered broadcast service. It
 * provides 2 methods tobSend() : to send messages from application. and another
 * is tobReceive() : returns the messages received to the application.
 */
public class TOB implements Runnable {

	private Config config; // stores information from configuration file.
	private Queue<String> queue; // place where messages received from
									// applications are stored.
	private Queue<String> resultQueue; // queue when acquired lock to csEnter
										// sends this queue to all neighbors
										// including itself.
	private Connection connect; // maintains communication information of other
								// nodes in the distributed network.
	private ConcurrentLinkedQueue<Message> messageQueue; // keeps the track of
															// incoming messages
															// from other nodes
															// in network and
															// processes them in
															// FIFO order.
	private boolean finished = false; // indicates whether execution has
										// finished.
	private MutualProvider mutual; // to request permission to enter critical
									// section through csEnter and csLeave
									// methods.
	private boolean sent = false; // keeps track of whether received messages
									// from other nodes are sent to application
									// or not.

	/**
	 * Constructor.
	 * 
	 * @param config
	 *            information of configuration file.
	 */
	public TOB(Config config) {
		this.config = config;
		// System.out.println("tob here");
		queue = new LinkedList<String>();
		this.connect = config.getConnect();
		messageQueue = new ConcurrentLinkedQueue<Message>();
		mutual = new MutualProvider(config);
		MessageHandler messageHandler = new MessageHandler(config, mutual, this);
		Thread t1 = new Thread(messageHandler);
		t1.setName("messageHandler");
		t1.start();
		Thread t = new Thread(this);
		t.setName("csEnter");
		t.start();

		t = new Thread(this);
		t.setName("messageQueue");
		t.start();
	}

	/**
	 * API to application for sending ramdomly generated messages.
	 * 
	 * @param message
	 *            message from application.
	 */
	public void tobSend(String message) {
		System.out.println("Adding message" + message);
		queue.add(message);
		return;
	}

	/**
	 * This implementation simply adds incoming messages from other node in
	 * network to internal queue to ensure FIFO processing.
	 * 
	 * @param message
	 *            incomfing message from other node.
	 */
	public void addMessage(Message message) {
		messageQueue.add(message);
	}

	/**
	 * 
	 */
	public void run() {
		String threadName = Thread.currentThread().getName();
		if (threadName.compareToIgnoreCase("csEnter") == 0) {
			while (finished == false) {
				try {
					// System.out.println("Before cs------" + queue.size());
					Thread.sleep(100);
				} catch (InterruptedException ex) {
					Logger.getLogger(TOB.class.getName()).log(Level.SEVERE, null, ex);
				}
				if (queue.size() > 0) {
					// System.out.println("Before cs");
					mutual.csEnter();
					// broadcase queue to all....
					System.out.println("Entered in cs");
					try {
						sendMessageToAll("tob");
						Thread.sleep(2000);
						mutual.csLeave();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} // csEnter
		else if (threadName.compareToIgnoreCase("messageQueue") == 0) {
			while (finished == false) {
				if (messageQueue.size() > 0) {
					Message m = messageQueue.poll();

					if (m.getType().compareToIgnoreCase("tob") == 0) {
						while (sent == true) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException ex) {
								Logger.getLogger(TOB.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
						System.out.println("Sender: " + m.getSender() + "$$$$$$$$$$$$$$$$$$" + m.getQueue());
						resultQueue = m.getQueue();
						sent = true;
					}
				}
			}
		}
	}

	/**
	 * This implementation is invoked upon receiving lock to csEnter so that it
	 * can sends it's randomly generated queue to all nodes in the network.
	 * 
	 * @param messageType
	 *            type of the message to send to all neighbors in the network.
	 * @throws IOException
	 */
	private void sendMessageToAll(String messageType) throws IOException {
		Message message = new Message(config.getMyNodeId(), 0, messageType, 0);
		Queue<String> temp = queue;
		queue = new LinkedList<String>();

		message.setQueue(temp);

		for (int i = 0; i < config.getNumOfNodes(); i++) {
			message.setReceiver(i);
			if (i != config.getMyNodeId()) {
				config.getConnect().getTobConnectOutputStream().get(i).flush();
				config.getConnect().getTobConnectOutputStream().get(i).writeObject(message);
				config.getConnect().getTobConnectOutputStream().get(i).flush();
			}

		}
		messageQueue.add(message);
	}

	/**
	 * This method indicates to stop the execution.
	 */
	public void setFisnished() {
		finished = true;
		mutual.setFinished();
	}

	/**
	 * API to application(blocking method call) which returns as soon as it
	 * receives message from other node or itself whoever gets the lock to enter
	 * critical section.
	 * 
	 * @return queue to messages received the node which has entered critical
	 *         section.
	 */
	public Queue<String> tobReceive() {
		// TODO Auto-generated method stub
		try {
			sent = false;
			while (sent == false) {
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(" Queue : " + resultQueue);
		return resultQueue;

	}
}
