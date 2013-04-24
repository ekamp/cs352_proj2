/* GROUP MEMBERS:
 * Jingfei Shi
 * Erik Kamp
 * Lindsey Crocker
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class p2pws implements Runnable {

	Socket conn;

	/* constructor */
	p2pws (Socket sock) {
		this.conn = sock;
	}

	/* global hashmap, each server is assigned one and responsible for files stored in it 
	 * key: String type hash, produced from the md5 hash function
	 * value: fileInfo object type, containing the file names and content of each file
	 */
	static HashMap<String, fileInfo> filemem = new HashMap<String, fileInfo>();

	/* main():
	 * - checks for valid command line arguments
	 * 		- user required to supply both server name and port number
	 * - sets up new server connection
	 * - spawns new thread for each connecting client
	 * - server will exit upon user command (ctrl+c)
	 */
	public static void main(String args[]) {

		int portnum = 0; // port number

		/* checking arguments for correct format */
		if (args.length == 1) {
			/* convert from string to integer */
			try {
				portnum = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("ERROR: Port number is not valid, server exiting.");
				System.exit(1);
			}
			/* check for port number range */ 
			if (portnum < 1024 || portnum > 63335) {
				System.out.println("ERROR: Port is not in range (1024-63335), server exiting.");
				System.exit(2);
			}
		} else if (args.length != 1) {
			System.out.println("ERROR: Port " + portnum + " is not in range, server exiting.");
			System.exit(3);
		}

		/* initialize new server socket */
		ServerSocket svc = null;
		try {
			svc = new ServerSocket(portnum, 5);
		} catch (IOException e) {
			System.out.println("ERROR: Cannot establish socket, server exiting.");
			System.exit(4);
		}

		/* multithreading: spawns new thread for each client connection */
		for (;;) {
			Socket conn = null;
			try {
				conn = svc.accept();
			} catch (IOException e) {
				System.out.println(e);
			}
			new Thread(new p2pws(conn)).start();
		}
	}

	/* run():
	 * - each spawned thread (client) executes this method
	 * - server receives lines of requests from the client
	 * - parses the given lines and determines the command
	 * - calls the command's respective method
	 * - sends HTTP response back to the client
	 */
	public void run() {

		/* initialization */
		String line; //user input line
		String command = "", file = "";
		int space, filename, count = -1;

		try {
			/* sets up BufferedReader and DataOutputStream to communicate to clients */
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());

			/* for each line for command that the client sends, parse and calls respective methods */
			while ((line = fromClient.readLine()) != null) {

				/* grabs first line of HTTP request header */
				if (line.contains("HTTP/1.1")) {
					space = line.indexOf(' ');
					command = line.substring(0, space); // get command
					filename = line.indexOf(' ', space+1); 
					file = line.substring(space+1, filename); // get file name in header
				} else if (line.contains("LIST")) { 
					/* format of LIST is different than the other ones, directly assign */
					command = "LIST";
				}

				/* call responsible methods depending on the command */
				if(command.equals("GET")) {
					toClient.writeBytes(get(file)); // call get()
					break;
				} else if (command.equals("PUT")) {
					if (line.contains("Content-Length:")) {
						count = Integer.parseInt(line.substring(line.indexOf(' ') + 1));
					}
					/* content starts after the new line */
					if (line.isEmpty()) {
						byte[] content = new byte[count];
						for (int i = 0; i < count; i++) {
							content[i] = (byte)fromClient.read();
						}
						toClient.writeBytes(put(file, count, content)); // call put()
					}
				} else if (command.equals("DELETE")) {
					toClient.writeBytes(delete(file)); // call delete()
				} else if (command.equals("LIST")) {
					toClient.writeBytes(list()); // call list()
				}
			} 
			conn.close(); // closing the client connection
			//System.out.println("Client exited, closing the client connection.");
		} catch (IOException e) {
			System.out.println("ERROR: Client unexpectedly exited, closing connection.");
		}

	}

	/* get(): HTTP GET request
	 * - "/local.html": send default content, return 200
	 * - "/favicon.ico": ignore
	 * - other files: goes to hashmap, look for file
	 * 		- if found, return content with 200
	 * 		- else, return 404
	 */
	public String get(String url) {

		/* initializations */
		String response = "", filecontent = "", clength = "", ret = "", hash = "";
		byte[] content; 

		if (url.equals("/local.html")) {
			/* special url: default content, 200 */
			response = "HTTP/1.1 200 OK" + "\n";
			filecontent = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + "\n"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" + "\n"
					+ "<head>" + "\n" 
					+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" + "\n"
					+ "<title> Local page </title>" + "\n" 
					+ "</head>" + "\n" 
					+ "<body>" + "\n"
					+ "<p> This is the local page on peer server " + conn.getLocalAddress() + " port " + conn.getLocalPort() + "</p>" + "\n"
					+ "</body>" + "\n" 
					+ "</html>" + "\n";
			content = filecontent.getBytes();
			clength = "Content-Length: " + content.length + "\n";
			ret = response + clength + "\n" + filecontent;

		} else if (url.equals("/favicon.ico")) {
			/* ignore favicon.ico request, returns nothing */
		} else { 
			/* any other file */
			try {
				hash = hashfunction.md5(url); // md5 hash function returns hash of file name
			} catch (Exception e) {
				System.out.println("ERROR: Unsuccessful hashing.");
			}

			/* based on the hash value (key), check to see if requested file is in the hashmap */
			if (!(filemem.containsKey(hash))) {
				response = "HTTP/1.1 404 Not Found" + "\n";
				ret = response;
			} else if (filemem.containsKey(hash)){
				response = "HTTP/1.1 200 OK" + "\n";
				filecontent = (filemem.get(hash).content);
				content = filecontent.getBytes();
				clength = "Content-Length: " + content.length + "\n";
				ret = response + clength + "\n" + filecontent;
			}	
		}
		return ret;
	}
	
	/* put(): HTTP PUT request
	 * - parameters include file name, content length, and content of file
	 * - run the file name through the md5 hash function
	 * - create a byte array of size equal to content length 
	 * - store key (hash) and content into the hashmap
	 * - returns PUT 200 OK response after file is stored
	 */
	public String put(String url, int count, byte[] data) {

		/* initializations */
		String content = new String(data);
		String hash = "", ret = "";

		/* put given file name through md5 hash function */
		try {
			hash = hashfunction.md5(url);
		} catch (Exception e) {
			System.out.println("ERROR: Unsuccessful hashing.");
		}

		/* insert key and value into hashmap, return 200 */
		filemem.put(hash, new fileInfo(url, content));
		ret = "HTTP/1.1 200 OK" + "\n" + "Content-Length: 0" + "\n\n";
		return ret;
	}

	/* delete(): HTTP DELETE request
	 * - run the file name through the md5 hash function
	 * - retrieve file (key) from the hashmap
	 * - if file exists, delete and return 200
	 * - else return 404
	 */
	public String delete(String url) {
		
		/* initializations */
		String response = "", clength = "", ret = "", hash = "";

		/* put given file name through md5 hash function */
		try {
			hash = hashfunction.md5(url);
		} catch (Exception e) {
			System.out.println("ERROR: Unsuccessful hashing.");
		}

		/* find file in hashmap, if exists, delete and return 200; otherwise return 404 */ 
		if (!(filemem.containsKey(hash))) {
			response = "HTTP/1.1 404 Not Found" + "\n";
			clength = "Content-Length: 0" + "\n\n";
			ret = response + clength;
		} else if (filemem.containsKey(hash)){
			filemem.remove(hash);
			response = "HTTP/1.1 200 OK" + "\n";
			clength = "Content-Length: 0" + "\n\n";
			ret = response + clength;
		}
		return ret;
	}

	/* list(): LIST command, part of peer-to-peer extensions
	 * - puts hashmap of files into an ArrayList
	 * - turns the ArrayList into a fileInfo array
	 * - iterate through the fileInfo array and append each entry into the StringBuilder 
	 * - return the String of the StringBuilder 
	 */
	public String list() {

		/* put hashmap into ArrayList */
		ArrayList<fileInfo> list = new ArrayList<fileInfo>(filemem.values());
		
		/* turned the ArrayList into a fileInfo array */
		fileInfo[] fi = list.toArray(new fileInfo[0]);
		
		/* initialization of the String containing the list of files */
		StringBuilder filelist = new StringBuilder();
		
		/* iterate through the fileInfo array and append each entry into the StringBuilder */
		for (int i = 0; i < fi.length; i++) {
			filelist.append(fi[i].filename + "\n");
		}
		return filelist.toString();
	}


}
