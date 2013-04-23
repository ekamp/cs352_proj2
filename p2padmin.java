/* GROUP MEMBERS:
 * Jingfei Shi
 * Erik Kamp
 * Lindsey Crocker
 */

import java.io.*;
import java.net.*;
import java.util.*;

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
		int portnum = 0;

		/* checking arguments for correct format */
		if (args.length == 2) {
			servername = args[0];
			/* convert from string to integer */
			try {
				portnum = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println("ERROR: Port number is not valid, exiting.");
				System.exit(1);
			}
			/* checking range of port number */
			if (portnum < 1024 || portnum > 65535) {
				System.out.println("ERROR: Port " + portnum + " is not in range, exiting.");
				System.exit(2);
			}
		} else if (args.length != 2) {
			System.out.println("ERROR: Invalid number of arguments, exiting.");
			System.exit(3);
		}

		/* initialize socket to connect to the server */
		Socket conn = null;
		try {
			conn = new Socket(servername, portnum);
		} catch (IOException e) {
			System.out.println("ERROR: Please connect to the server first, exiting.");
			System.exit(4);
		} 

		/* initialize BufferedReader & DataOutputStream to use between client and server */
		BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
		DataOutputStream toServer = null;
		try {
			toServer = new DataOutputStream(conn.getOutputStream());
		} catch (IOException e) {
			System.out.println("ERROR: Cannot create new DataOutputStream, exiting.");
			System.exit(5);
		}

		/* spawn a new thread to fetch messages from server */
		new Thread(new p2padmin(conn)).start();
		
		String line; // holds user input to the server
		String command = "", port = "", server = "", request = "";
		int space = 0, colon = 0, slash = 0;

		/* get user commands */
		try {
			while ((line = userdata.readLine()) != null) {

				if (line.isEmpty()) continue; //ignores blank (\n) commands

				/* initial parsing for command */
				try {
					space = line.indexOf(' ');
					command = line.substring(0, space);
				} catch (StringIndexOutOfBoundsException e) {
					System.out.println("ERROR: Incorrect input format, please try again.");
					continue;
				}
				
				/* error checking for mismatching host name and port number in command */
				if ((colon = line.indexOf(':')) == -1) {
					System.out.println("ERROR: Incorrect input format, please try again.");
					continue;					
				} else {
					server = line.substring(space+1, colon);
					if (!(checkServer(servername, server))) {
						System.out.println("ERROR: Invalid server name in command, please try again.");
						continue;
					}
					if (line.indexOf(' ', space+1) == -1 && line.indexOf('/') == -1) {
						port = line.substring(colon+1);
					} else {
						slash = line.indexOf('/');
						port = line.substring(colon+1, slash);
					}
				}
				if (!(checkPort(portnum, port))) {
					System.out.println("ERROR: Invalid port number in command, please try again.");
					continue;
				}

				/* call request method to process the command, error checking */
				try {
					request = request(line, command);
				} catch (Exception e) {
					continue;
				}
				if (request == null) {
					continue;
				}
				
				/* send HTTP request to server */
				try {
					toServer.writeBytes(request);
				} catch (SocketException e) { // in case server dies mid-session
					System.out.println("ERROR: Server closed, exiting client.");
					System.exit(6);
				}
			}
		} catch (IOException e) {
			System.out.println("ERROR: Server closed, exiting client.");
			System.exit(7);
		}

		try {
			conn.close();
		} catch (IOException e) {
			System.out.println("ERROR: Exiting client.");
			System.exit(8);
		}
	}

	/* run: reads and prints out HTTP messages from server
	 * executed by the spawned thread in main
	 */
	public void run() {

		try {
			String result; // messages from server 
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			while ((result = fromServer.readLine()) != null) {
				System.out.println(result);
			}
			conn.close(); // connection will be closed upon ctrl+d
		} catch (IOException e) {
			System.out.println("User closed client, exiting.");
			System.exit(9);
		}
	}
	
	/* checkPort: checks that port number in command is valid 
	 * if port number in the user command matches connection port, return true
	 * else returns false
	 */
	public static boolean checkPort(int localport, String port) {
		
		return (localport == Integer.parseInt(port));
	}
	
	/* checkServer: checks that host name in command is valid 
	 * if host name in the user command matches connection host name, return true
	 * else returns false 
	 */
	public static boolean checkServer(String servername, String host) {
		
		return servername.equals(host);
	}
	
	/* readFile: reads and returns content of file on local machine
	 * utilizes Scanner to read specified local file
	 */
	public static String readFile(String filename) throws FileNotFoundException {

		File localfile = new File(filename);
		String content = "";
		
		Scanner sc = null;
		sc = new Scanner(localfile);

		while (sc.hasNextLine()) {
			content += sc.nextLine();
			if (sc.hasNext() == true) {
				content += "\n";
			}
		}
		return content;
	}
	
	public static String request(String line, String command) throws Exception {
		
		int space = line.indexOf(' '), fileindex = 0, clength = 0;
		String request = "", filename = "", localfile = "", content = "";
		byte[] filecontent;
		
		if (command.equals("delete")) {
			fileindex = line.indexOf('/', space+1);
			filename = line.substring(fileindex);
			request = "DELETE " + filename + " HTTP/1.1" + "\n";
		} else if (command.equals("put")) {
			try {
				fileindex = line.indexOf('/', space+1);
				space = line.indexOf(' ', fileindex);
				filename = line.substring(fileindex, space);
				localfile = line.substring(space+1);
			} catch (StringIndexOutOfBoundsException e) {
				System.out.println("ERROR: Incorrect input format, please try again.");
				return null;
			}
			/*
			System.out.println("Content: " + content);
			System.out.println("put: " + command);
			System.out.println("filename: " + filename);
			System.out.println("localfile: " + localfile);
			*/
			
			//get the content of the file
			try {
				content = readFile(localfile);
			} catch (FileNotFoundException e) {
				System.out.println("ERROR: Local file \"" + localfile + "\" is not found, please try again.");
				return null; 
			}

			filecontent = content.getBytes();
			clength = filecontent.length;

			request = "PUT " + filename + " HTTP/1.1" + "\n"+ "Content-Length: " + clength + "\n\n" + content;
		} else if (command.equals("list")) {
			request = "LIST" + "\n";
		} else {
			System.out.println("ERROR: Incorrect input format, please try again.");
			return null;
		}
		
		//System.out.println("request: " + request);
		return request;
	}
}
