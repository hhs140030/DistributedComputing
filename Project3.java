import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Queue;
import java.util.Random;

/**
 * This is staring point of project. This class is application part of the
 * project(see description of project file), which does initialization such as
 * creating conenctions with nodes, reading from configuration file etc. and
 * then launches two threads named 'tobSend' and 'tobReceive'. 
 * tobSend: it periodically generates the random message and sends it's for totally ordered
 * broadcast(tob) service provider. 
 * tobReceive: it waits till it hears from totally ordered broadcast service provider.
 */
public class Project3 implements Runnable {
	/* It has all the information from configuration file */
	private Config config;
	/* Port number at which this node listens on */
	private int myPort;
	/* object to totally ordered broadcast service */
	private TOB tob;
	/* Responsible for creating connection with all nodes in network. */
	private Graph graph;
	/* this variable indicates whether stop the running or not. */
	private boolean finished = false;
	/* Output of this node */
	private String fileName;
	/*
	 * it keeps track of randomly generated messages received from different
	 * nodes or itself..
	 */
	private int TotalCount = 0;

	/**
	 * Main method which takes 2 arguments first node id(to specify the node id
	 * of the system) and second configuration file name.
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub

		if (args.length != 2) {
			System.out.println("Usage:\njava Project3 <nodeId> <config_file>");
			return;
		}

		int nodeId = Integer.parseInt(args[0]);
		String configFile = args[1];

		Project3 project = new Project3(nodeId, configFile);

	}

	/**
	 * Constructor.
	 * 
	 * @param nodeId
	 *            node id of the system as supplied when running the program.
	 * 
	 * @param fileName
	 *            path of configuration file.
	 */
	public Project3(int nodeId, String fileName) throws InterruptedException, IOException {
		System.out.println(fileName.split("\\.")[0] + " " + this.fileName);
		this.fileName = fileName.split("\\.")[0] + "-" + nodeId + ".out"; // output
																			// file
																			// will
																			// be
																			// "config-1.out"
																			// if
																			// configuration
																			// file
																			// name
																			// is
																			// config.txt
																			// and
																			// it's
																			// node
																			// id
																			// is
																			// 1.
		System.out.println(this.fileName);
		config = new Config(nodeId, fileName); // call for extracting
												// configuration file details
												// specific to this node.
		myPort = config.getMyPort();
		graph = new Graph(config); // this creates connection with all it's
									// neighboring nodes.
		config.setConnect(graph.createConnection()); // method to get Connection
														// object which contains
														// all
														// connection
														// information.
		tob = new TOB(config); // object to totally ordered broadcast service
								// which internally uses mutual exclusion
								// service.

		// To store the output.
		PrintWriter writer = new PrintWriter(this.fileName);
		writer.print("");
		writer.close();
		Thread t = new Thread(this);
		t.setName("tobReceive");// launches tobReceive thread which waits till
								// it receives any value(blocking call). More
								// information is in project description file.
		t.start();
		t = new Thread(this);
		t.setName("tobSend"); // launches tobSend thread which sends certain
								// randomly generated number
								// periodically(non-blocking call). More
								// information is in project description file.
		t.start();

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run() This implementation has 2 parts, first
	 *      'tobSend' where it keeps on sending randomly generated numbers
	 *      periodically. and second part 'tobReceive' which waits till it has
	 *      received non-empty queue and writes it to output file.
	 */
	public void run() {
		String threadName = Thread.currentThread().getName();
		if (threadName.compareToIgnoreCase("tobSend") == 0) { // tobSend thread.
																// More
																// information
																// is in project
																// description
																// file.
			int numOfMessagesSent = 0;
			while (numOfMessagesSent < config.getNumOfMessages()) { // sending
				// messages with
				// specified
				// delay.
				try {
					Thread.sleep(config.getDelay());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Random rand = new Random();
				int randomNum = rand.nextInt(1000);
				tob.tobSend(Integer.toString(randomNum));
				numOfMessagesSent++;
			}

			System.out.println("Done sending messages #" + config.getNumOfMessages());
		} // tobSend..
		else if (threadName.compareToIgnoreCase("tobReceive") == 0) { // after
																		// receiving
																		// messages
																		// stores
																		// it in
																		// output
																		// file(this.fileName).
																		// and
																		// once
																		// it
																		// reaches
																		// the
																		// expected
																		// count,
																		// this
																		// method
																		// finishes
																		// and
																		// so
																		// does
																		// all
																		// threads.
			int expectedCount = config.getNumOfMessages() * config.getNumOfNodes();
			while (finished == false) {
				Queue<String> queue = tob.tobReceive();
				try {
					System.out.println("!!!!!!!!!!!!!---size=" + queue.size());
					int size = queue.size();
					PrintWriter writer = new PrintWriter(new FileOutputStream(new File(fileName), true));
					for (int i = 0; i < size; i++) {
						System.out.println("from app tobr= " + queue.peek());
						writer.println(queue.poll());
						TotalCount++;
						if (TotalCount == expectedCount) {
							setFinish();
							System.out.println("Program is finished...\n Killing self");
						}
					}

					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} // tobReceive

	}

	/**
	 * This sets boolean variable to true to indicate threads can stop.
	 */
	private void setFinish() {
		finished = true;
	}
}
