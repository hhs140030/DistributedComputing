import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is format of the message exchanged between different nodes in the
 * network.
 */
public class Message implements Serializable {
	/* serial version id */
	private final int serialVersionId = 1;
	/* id of the sender. */
	private int sender;
	/* id of the receiver. */
	private int receiver;
	/* clock value */
	private int clock;
	/* queue of randomly generated messages */
	private Queue<String> queue;
	/*
	 * type of the messgae it could be "connect", "remove", "reply", "request"
	 */
	private String type;

	/**
	 * Constructor.
	 * 
	 * @param sender
	 *            unique id of the sender
	 * @param receiver
	 *            unique id of the receiver
	 * @param type
	 *            type of the message.
	 * @param clock
	 *            clock value at that instant.
	 */
	public Message(int sender, int receiver, String type, int clock) {
		this.sender = sender;
		this.receiver = receiver;
		this.type = type;
		this.clock = clock;
		this.queue = null;
	}

	/**
	 * Getter for sender.
	 */
	public int getSender() {
		return sender;
	}

	/**
	 * Setter for sender.
	 */
	public void setSender(int sender) {
		this.sender = sender;
	}

	/**
	 * Getter for receiver.
	 */
	public int getReceiver() {
		return receiver;
	}

	/**
	 * Setter for receiver.
	 */
	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	/**
	 * Getter for clock value.
	 */
	public int getClock() {
		return clock;
	}

	/**
	 * Setter for clock at that instant.
	 */
	public void setClock(int clock) {
		this.clock = clock;
	}

	/**
	 * Getter for queue of randomly generated messages.
	 */
	public Queue<String> getQueue() {
		return queue;
	}

	/**
	 * Setter for randomly generated messages queue.
	 */
	public void setQueue(Queue<String> queue) {
		this.queue = queue;
	}

	/**
	 * Getter for message type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Setter for message type.
	 */
	public void setType(String type) {
		this.type = type;
	}

}
