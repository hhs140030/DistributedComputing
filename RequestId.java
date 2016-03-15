/**
 * This class represents the timestamp of mutual exclusion request. Here, 2
 * different timestamps are distinguished in following way. 1st timestamp is
 * lower than other if first timestamp has lower processId or if there is a tie
 * then lower clock value.
 */
public class RequestId {
	/*
	 * process id of the process which is requesting the mutual exclusion
	 * service.
	 */
	private int processId;
	/* logical clock value at the time this request is generated. */
	private int clock;

	/**
	 * Constructor.
	 * 
	 * @param processId
	 *            process id of the process which is requesting the mutual
	 *            exclusion service.
	 * @param clock
	 *            logical clock value at the time this request is generated.
	 */
	public RequestId(int processId, int clock) {
		this.processId = processId;
		this.clock = clock;
	}

	/**
	 * Getter for processId.
	 */
	public int getProcessId() {
		return processId;
	}

	/**
	 * Setter for processId.
	 */
	public void setProcessId(int processId) {
		this.processId = processId;
	}

	/**
	 * Getter for logical clock value.
	 */
	public int getClock() {
		return clock;
	}

	/**
	 * Setter for current logical clock value.
	 */
	public void setClock(int clock) {
		this.clock = clock;
	}
}
