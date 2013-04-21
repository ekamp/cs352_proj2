/* Group Members:
 * Jingfei Shi
 * Lindsey Crocker
 * Erik Kamp
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class p2pws implements Runnable {

	Socket conn;

	p2pws (Socket sock) {
		this.conn = sock;
	}

	static HashMap<String, String> filemem = new HashMap<String, String>(); //global hashmap

	public static void main(String args[]) {
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
			} catch (IOException e) {
				System.out.println(e);
				System.exit(4);
			}

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


	public void run() {

		String line; //user input line
		String command = "", file = "";
		int count = -1;

		try {
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());

			while ((line = fromClient.readLine()) != null) {

				if (line.contains("HTTP/1.1")) {

					int space = line.indexOf(' '); //first instance of space
					command = line.substring(0, space); 
					int filename = line.indexOf(' ', space+1);
					file = line.substring(space+1, filename);

					System.out.println("command: " + command);
					System.out.println("file: " + file);

				}

				if(command.equals("GET")) {
					//System.out.println(get(file));
					toClient.writeBytes(get(file));
					//System.out.println("wrote to client");
					break;
				} else if (command.equals("PUT")) {

					if (line.contains("Content-Length:")) {
						count = Integer.parseInt(line.substring(line.indexOf(' ') + 1));
						System.out.println("count: " + count);
					}

					if (line.isEmpty()) {
						
						byte[] content = new byte[count];
						for (int i = 0; i < count; i++) {
							content[i] = (byte)fromClient.read();
						}
						toClient.writeBytes(put(file, count, content));
						break;
					}
				}
			}

			System.out.println("Client exited.");
			conn.close();
			System.out.println("Connection closed.");
		} catch (IOException e) {
			System.out.println(e);
		}
		
	}

	public HashMap<String, String> create() {
		
		HashMap<String, String> filemem = new HashMap<String, String>();
		/*
		String content1 = "Once upon a time, there was a fair maiden by the name of Vicki. " +
				"She loved polishing her computers, and therefore, her gadgets are always very clean.\n";

		String content2 = "Seal seal seal! Anemone.\n";

		String content3 = "The fair maiden from content1 loves drinking water. Her Brita filter is her best friend.\n";
		
		String content4 = "hello!\n";
		
		try {
			filemem.put(hashfunction.md5("/vicki.html"), content1);
			filemem.put(hashfunction.md5("/anemone.html"), content2);
			filemem.put(hashfunction.md5("/water.html"), content3);
			filemem.put(hashfunction.md5("/hello.html"), content4);
		} catch (Exception e) {
			System.out.println(e);
		}
		
		//PUT test
		try {
			System.out.println("before: " + filemem.get(hashfunction.md5("/hello.html")));
		} catch (Exception e) {
			System.out.println(e);
		}
		 */
		
		return filemem;	

	}

	public String get(String url) {

		System.out.println("url: " + url);

		String response = "";
		String content = "";
		String clength = "";
		String ret = "";
		String hash = "";

		if (url.equals("/local.html")) {

			response = "HTTP/1.1 200 OK" + "\n";

			content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + "\n"
					+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" + "\n"
					+ "<head>" + "\n" 
					+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" + "\n"
					+ "<title> Local page </title>" + "\n" 
					+ "</head>" + "\n" 
					+ "<body>" + "\n"
					+ "<p> This is the local page on peer server " + conn.getLocalAddress() + " port " + conn.getLocalPort() + "</p>" + "\n"
					+ "</body>" + "\n" 
					+ "</html>";

			// not sure if conn.getLocalAddress() is actually correct, double check on this
			clength = "Content-Length: " + content.length() + "\n";
			ret = response + clength + "\n" + content;

		} else if (url.equals("/favicon.ico")) {
			//nothing
		} else {

			try {
				hash = hashfunction.md5(url);
			} catch (Exception e) {
				System.out.println(e);
			}
			System.out.println("hash: " + hash);

			System.out.println("contains key: " + filemem.containsKey(hash));
			
			if (!(filemem.containsKey(hash))) {
				response = "HTTP/1.1 404 Not Found" + "\n";
				ret = response;
			} else if (filemem.containsKey(hash)){
				response = "HTTP/1.1 200 OK" + "\n";
				content = filemem.get(hash);
				clength = "Content-Length: " + content.length() + "\n";
				ret = response + clength + "\n" + content;
			}	
		}

		return ret;

	}

	public String put(String url, int count, byte[] data) {

		System.out.println("url & count: " + url + ": " + count);
		String content = new String(data);
		//System.out.println("content: " + content);
		
		String hash = "";
		try {
			hash = hashfunction.md5(url);
			System.out.println("hash: " + hash);
		} catch (Exception e) {
			System.out.println(e);
		}
		filemem.put(hash, content);
		
		/* PUT test 
		try {
			System.out.println("hello.html: " + filemem.get(hash));
		} catch (Exception e) {
			System.out.println(e);
		}
		*/
		
		String ret = "HTTP/1.1 200 OK" + "\n";

		return ret;

	}


}
