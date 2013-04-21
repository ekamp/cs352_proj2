/* Group Members:
 * Jingfei Shi
 * Lindsey Crocker
 */

import java.io.*;
import java.net.*;

public class p2padmin implements Runnable {

	Socket conn; // client socket

	p2padmin(Socket sock) {
		this.conn = sock; // store socket in the connection
	}

	/* main:
	 * - checks for valid command line parameters
	 * - sets up new socket connection
	 * - original thread reads and writes user inputs to server 
	 * - spawns new thread that will read and print out messages sent from the server	 
	 */
	public static void main(String args[]) {

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

		try {
			conn = new Socket(servername, portnum);
		} catch (IOException e) {
			System.out.println("Please connect to the server first, exiting. " + e);
			System.exit(6);
		} 

		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream toServer = null;

		try {
			toServer = new DataOutputStream(conn.getOutputStream());
		} catch (IOException e) {
			System.out.println("data output stream : " + e);
		}

		new Thread(new p2padmin(conn)).start(); // spawn a new thread 
		String line; // holds user input to the server

		String command = "", filename = "", localfile = "", content = "", request = "";
		int space, fileindex, clength;
		byte[] filecontent;

		try {
			while ((line = userdata.readLine()) != null) {
				
				if (line.isEmpty()) continue;

				System.out.println("client typed: " + line);
				space = line.indexOf(' ');
				command = line.substring(0, space);

				if (command.equals("delete")) {
					fileindex = line.indexOf('/', space+1);
					filename = line.substring(fileindex);
					request = "DELETE " + filename + " HTTP/1.1" + "\n";
				} else if (command.equals("put")) {
					fileindex = line.indexOf('/', space+1);
					space = line.indexOf(' ', fileindex);
					filename = line.substring(fileindex, space);
					localfile = line.substring(space+1);
					//get the content of the file
					BufferedReader br = new BufferedReader(new FileReader(localfile));
					try {
						StringBuilder sb = new StringBuilder();
						String theline = br.readLine();

						while (theline != null) {
							sb.append(theline);
							sb.append("\n");
							theline = br.readLine();
						}
						content = sb.toString();
					} finally {
						br.close();
					}	

					filecontent = content.getBytes();
					clength = filecontent.length;

					request = "PUT " + filename + " HTTP/1.1" + "\n"+ "Content-Length: " + clength + "\n\n" + content;

					System.out.println("Content: " + content);
					System.out.println("put: " + command);
					System.out.println("filename: " + filename);
					System.out.println("localfile: " + localfile);
				} else if (command.equals("list")) {
					request = "LIST";
				}

				System.out.println("request: " + request);


				/*
				try {
					toServer.writeBytes(request + '\n');
				} catch (SocketException e) { // in case server dies mid-session
					System.out.println("Socket closed: " + e);
					//System.exit(7);
				}
				*/
				
				toServer.writeBytes(request + "\n");
			}
		} catch (IOException e) {
			System.out.println("while loop: " + e);
		}

		try {
			conn.close();
		} catch (IOException e) {
			System.out.println("conn close " + e);
		}
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
		} catch (IOException e) {
			System.out.println("run() " + e);
		}
	}
}
