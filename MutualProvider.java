
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is the implementation of Lamport's mutual exclusion algorithm.
 * This class provides 2 methods csEnter and csLeave for requesting and exiting
 * mutual exclusion service. wiki for algorithm: http://tinyurl.com/h9275jq
 */
public class MutualProvider implements Runnable {

	/* stores the neighbor's connection information input stream and out stream. */
	private Connection connect;
	/* maintains configuration file information. */
	private Config config;
	/* logical clock value */
	private int clock;
	/* to indicate whether program has finished or not. */
	private boolean finished = false;
	/* Gives lowest requestId in case of multiple requests. */
	private Queue<RequestId> priorityQueue;
	/* keeps track of replies received for mutual exclusion service requested */
	private int replyReceived;
	/* Keeps track of message received and maintains FIFO order. */
	private ConcurrentLinkedQueue<Message> messageQueue;
	/* Time stamp of the moment request is generated. */
	private RequestId requestTimeStamp;

	/**
	 * Constructor.
	 */
	public MutualProvider(Config config) {
		System.out.println("In mutual...");
		this.config = config;
		this.connect = config.getConnect();
		clock = 0;
		replyReceived = 0;
		Comparator<RequestId> compare = new TimeStampComparator();
		priorityQueue = new PriorityQueue<RequestId>(config.getNumOfNodes(), compare);
		messageQueue = new ConcurrentLinkedQueue<Message>();
		Thread t = new Thread(this);
		t.start();
	}

	/**
	 * @param message
	 *            adds incoming messages to the queue so that they can be
	 *            processed in FIFO order.
	 */
	public void addMessage(Message message) {
		messageQueue.add(message);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() This thread implementation processes the
	 *      messages in FIFO order.
	 */
	public void run() {
		// System.out.println("Launching message queue mutual...");
		while (finished == false) {
			if (messageQueue.size() > 0) {
				Message m = messageQueue.poll();
				try {
					processMessage(m);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * to indicate that execution has ended.
	 */
	public void setFinished() {
		finished = true;
	}

	/**
	 * This method processes the incoming message based on type of the message.
	 */
	private void processMessage(Message m) throws IOException {
		// TODO Auto-generated method stub
		String messageType = m.getType();
		updateClock(m.getClock());
		printMessage(m);
		if (messageType.compareToIgnoreCase("remove") == 0) { // it implies to
																// remove top
																// requestId
																// from priority
																// queue so that
																// next
																// requestId can
																// get access to
																// mutual
																// exclusion
																// service.
			addToPriorityQueue(null, false);
		} else if (messageType.compareToIgnoreCase("reply") == 0) { // upon
																	// sending
																	// request
																	// each
																	// process
																	// receives
																	// the
																	// 'reply'
																	// with
																	// greater
																	// logical
																	// clock
																	// value.
			updateReplyCount(m.getClock()); // to update the clock value
											// received from the message.
		} else if (messageType.compareToIgnoreCase("request") == 0) { // This is
																		// the
																		// request
																		// messages
																		// and
																		// upon
																		// receiving
																		// it,
																		// each
																		// node
																		// add
																		// to
																		// it's
																		// priority
																		// queue
																		// and
																		// then
																		// whoever
																		// has
																		// the
																		// lowest
																		// requestId
																		// gets
																		// lock
																		// for
																		// entering
																		// critical
																		// section.
			addToPriorityQueue(new RequestId(m.getSender(), m.getClock()), true);
			Message message = new Message(config.getMyNodeId(), m.getSender(), "reply", updateClock(0));
			config.getConnect().getMutualConnectOutputStream().get(m.getSender()).flush();
			config.getConnect().getMutualConnectOutputStream().get(m.getSender()).writeObject(message);
			config.getConnect().getMutualConnectOutputStream().get(m.getSender()).flush();
		}
	}

	/**
	 * This implementation prints the incoming message.
	 */
	private void printMessage(Message m) {
		// TODO Auto-generated method stub
		System.out.println("Receive msg : " + m.getType() + " clock" + m.getClock() + " sender=" + m.getSender());
	}

	/**
	 * This implementation updates replies received on sending the reuqests.
	 */
	private synchronized void updateReplyCount(int receivedTimeStamp) {
		// if (receivedTimeStamp > requestTimeStamp.clock) {
		replyReceived++;
		// }
	}

	/**
	 * This implementation provides a way to acquire the lock to enter critical
	 * section. This is blocking funtion call.
	 */
	public void csEnter() {
		replyReceived = 0;
		requestTimeStamp = new RequestId(config.getMyNodeId(), updateClock(0)); // generating
		// requestId
		// (timestamp)
		System.out.println("in CS");
		processCSEnter();
		return;
	}

	/**
	 * Upon acquiring lock to enter critical section, this implementation helps
	 * release the lock.
	 */
	public void csLeave() throws IOException {
		addToPriorityQueue(null, false);// priorityQueue.poll();
		sendMessageToAll("Remove");
	}

	/**
	 * This implementation sends the message to all the neighbors.
	 * 
	 * @param messageType
	 *            type of the message to broadcast.
	 */
	private void sendMessageToAll(String messageType) throws IOException {
		Message message = new Message(config.getMyNodeId(), 0, messageType, 0);
		int timeStamp = requestTimeStamp.getClock();
		message.setClock(timeStamp);
		for (int i = 0; i < config.getNumOfNodes(); i++) {
			message.setReceiver(i);
			if (i != config.getMyNodeId()) {
				config.getConnect().getMutualConnectOutputStream().get(i).flush();
				config.getConnect().getMutualConnectOutputStream().get(i).writeObject(message);
				config.getConnect().getMutualConnectOutputStream().get(i).flush();
			}
		}

	}

	/**
	 * This implementation updates the clock. if it is 0 then simple update my
	 * clock otherwise, check if my logical clock value is lower and if it is
	 * then update it with received clock value from neighbor + 1.
	 * 
	 * @param i
	 *            logical clock value.
	 */
	private synchronized int updateClock(int i) {// if 0 then just increatement
													// currrent logical clock
													// value
		if (i == 0) {
			clock++;
		} else if (i > clock) { // else check if my clock values is less than
								// the one received from other nodes if yes,
								// then copy that clock value + 1 to my clock
								// value.
			clock = i + 1;
		}
		return clock;
	}

	/**
	 * This method either adds reuqestId to priority queue or removes the lowest
	 * one.
	 */
	public synchronized void addToPriorityQueue(RequestId res, boolean add) {
		if (add)
			priorityQueue.add(res);
		else {
			priorityQueue.poll();
		}
	}

	/**
	 * This implementation actually checks if my reuqetsId matches with lowest
	 * reqeustId from priorityQueue and if it does, then this nodes acquires the
	 * lock to enter critical section.
	 */
	private void processCSEnter() {
		// TODO Auto-generated method stub
		try {
			addToPriorityQueue(requestTimeStamp, true);
			sendMessageToAll("request");
			while (replyReceived != config.getNumOfNodes() - 1
					|| priorityQueue.peek().getProcessId() != config.getMyNodeId()) {

				Thread.sleep(10);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

/**
 * Comparator class to override compare method such that o1 is lower than o2 if
 * and only if processId of o1 is lower than o2's. If there is a tie then it
 * checks for their logical clock value.
 */
class TimeStampComparator implements Comparator<RequestId> {

	@Override
	public int compare(RequestId o1, RequestId o2) {
		// TODO Auto-generated method stub
		if (o1.getClock() < o2.getClock()) {
			return -1;
		} else if (o2.getClock() < o1.getClock()) {
			return 1;
		} else if (o1.getClock() == o2.getClock() && o1.getProcessId() < o2.getProcessId()) {
			return -1;
		} else {
			return 1;
		}
	}

}
