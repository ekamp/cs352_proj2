/* Group Members:
 * Jingfei Shi
 * Lindsey Crocker
 */

import java.io.*;
import java.net.*;

public class CalcClient implements Runnable {

	Socket conn; // client socket

	CalcClient(Socket sock) {
		this.conn = sock; // store socket in the connection
	}

	/* main:
	 * - checks for valid command line parameters
	 * - sets up new socket connection
	 * - original thread reads and writes user inputs to server 
	 * - spawns new thread that will read and print out messages sent from the server	 
	 */
	public static void main(String args[]) throws Exception {

		String servername = "localhost"; // default server name
		int portnum = 12345; // default port number

		/* checking arguments for correct format */
		if (args.length == 1) {
			servername = args[0];
		} else if (args.length == 2) {
			servername = args[0];
			try { // convert portnum string to int
				portnum = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println(e.toString());
				System.exit(1);
			}
			// checking range of port number
			if (portnum < 1024 || portnum > 65535) {
				System.out.println("Port " + portnum + " is not in range, exiting.");
				System.exit(2);
			}
		} else if (args.length > 2) {
			System.out.println("Too many arguments, exiting.");
			System.exit(3);
		}

		Socket conn = null; // new socket

		try { // client cannot be used without the server
			try {
                conn = new Socket(servername, portnum);
            } catch (SocketException e) {
                System.out.println(e);
                System.exit(5);
            }
		} catch (ConnectException e) {
			System.out.println("Please connect to the server first, exiting. " + e);
			System.exit(6);
		}

		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream toServer = new DataOutputStream(conn.getOutputStream());

		new Thread(new CalcClient(conn)).start(); // spawn a new thread 
		String line; // holds user input to the server
		
		while ((line = userdata.readLine()) != null) {
			try {
                toServer.writeBytes(line + '\n');
            } catch (SocketException e) { // in case server dies mid-session
                System.out.println(e);
                System.exit(7);
            }
		}

        conn.close();
	}

	/* run: reads and prints out messages from server
	 * executed by the spawned thread in main
	 */
	public void run() {
		
		try {
			String result; // messages from server 
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((result = fromServer.readLine()) != null) {
				System.out.println("=> " + result);
			}
			conn.close(); // connection will be closed upon ctrl+d
		} catch (SocketException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
