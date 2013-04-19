/* Group Members:
 * Jingfei Shi
 * Lindsey Crocker
 * Erik Kamp
 */

import java.io.*;
import java.net.*;
import java.util.HashMap;


public class p2pws implements Runnable {

	Socket conn;

	p2pws (Socket sock) {
		this.conn = sock;
	}

	public static void main(String args[]) throws Exception {
		//Only takes one arg the port number if more return error

		int portnum = 12345; // default port number

		/* checking arguments for correct format */
		if (args.length == 1) {
			try { /* convert portnum to integer */
				portnum = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println(e);
				System.exit(1);
			}
			/* check for port number range */ 
			if (portnum < 1024 || portnum > 63335) {
				System.out.println("Port is not in range, exiting.");
				System.exit(2);
			}
		} else if (args.length > 1) {
			System.out.println("Too many arguments, exiting.");
			System.exit(3);
		}

		ServerSocket svc = null; // new server socket

		try {
			svc = new ServerSocket(portnum, 5);
		} catch (BindException e) {
			System.out.println(e);
			System.exit(4);
		}

		for (;;) {
			Socket conn = svc.accept();
			new Thread(new p2pws(conn)).start();
		}
	}

	public void run() {

		String line; //user input line
		String command = "", file = "";



		try {
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());

			//HashMap<String, String> hm = create();


			while ((line = fromClient.readLine()) != null) {

				System.out.println("first line: " + line);
				int space = line.indexOf(' '); //first instance of space
				command = line.substring(0, space); 
				int filename = line.indexOf(' ', space+1);
				file = line.substring(space+1, filename);

				System.out.println("command: " + command);
				System.out.println("file: " + file);

				if (command.equals("GET")) {
					//System.out.println(get(file));
					toClient.writeBytes(get(file));
					System.out.println("wrote to client");
				} else if (command.equals("PUT")) {
					System.out.println("put");
				} else if (command.equals("DELETE")) {
					System.out.println("delete");
				} else if (command.equals("LIST")) {
					System.out.println("list");
				} else if (command.equals("PEERS")) {
					System.out.println("peers");
				} else if (command.equals("REMOVE")) {
					System.out.println("remove");
				} else if (command.equals("ADD")) {
					System.out.println("add");
				}
				break;
			}

			System.out.println("Client exited.");
			conn.close();
			System.out.println("Connection closed.");
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/*
	public HashMap create() {
		HashMap<String, String> hm = new HashMap<String, String>();
		return hm;	

	}
	 */

	public String get(String url) {

		//System.out.println("url: " + url);

		String ret = "";
		String hashnum = "";

		if (url.equals("/local.html")) {
			
			String response = "HTTP/1.1 200 OK" + "\n";

			String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + "\n"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" + "\n"
					+ "<head>" + "\n" 
					+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" + "\n"
					+ "<title> Local page </title>" + "\n" 
					+ "</head>" + "\n" 
					+ "<body>" + "\n"
					+ "<p> This is the local page on peer server " + conn.getLocalAddress() + " port " + conn.getLocalPort() + "</p>" + "\n"
					+ "</body>" + "\n" 
					+ "</html>";

			String clength = "Content-Length: " + content.length() + "\n";
			ret = response + clength + "\n" + content;

			try {
				hashnum = hashfunction.md5(url);
			} catch (Exception e) {
				System.out.println(e);
			}
			System.out.println("hash: " + hashnum);

		} else if (url.equals("/favicon.ico")) {
			//nothing
		}

		/* if h in this peer, 
		 *    if not found, send HTTP/1.1 404 Not Found
		 *    else send HTTP/1.1 200 OK, content-length, content
		 * else
		 *    redirect and search for owner for h
		 *    send HTTP/1.1 301 Moved Permanently, location
		 */

		return ret;

	}




}
