/* Group Members:
 * Jingfei Shi
 * Lindsey Crocker
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class CalcServer implements Runnable {

    Socket conn;

    CalcServer (Socket sock) {
        this.conn = sock;
    }

	/* main:
	 * - checks for valid command line parameters
	 * - create ServerSocket
	 * - reads input from the client
     * - math operations
     * - sends result back to client
	 */
	public static void main(String args[]) throws Exception {

		int portnum = 12345; // default port number

		/* checking arguments for correct format */
		if (args.length == 1) {
			try { // convert portnum string to int
				portnum = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.out.println(e);
				System.exit(1);
			}
			// checking range of port number
			if (portnum < 1024 || portnum > 65535) {
				System.out.println("Port " + portnum + " is not in range, exiting.");
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
            new Thread(new CalcServer(conn)).start();
        }
    }

    public void run() {

        LinkedList<Double> head = new LinkedList<Double>(); //stack for calculator
        String line; // user input line

        try {

			BufferedReader fromClient = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			DataOutputStream toClient = new DataOutputStream(conn.getOutputStream());

			while ((line = fromClient.readLine()) != null) {
				
				if (line.equals("show")) { // "show" command invokes the Iterator
					ListIterator<Double> itr = head.listIterator();
					while (itr.hasNext()) {
						toClient.writeBytes(itr.next() + "\n");
					}
				} else { // math operation, calls calc() method
					toClient.writeBytes(calc(line, head));
				}
			}
            System.out.println("Client exited.");
			conn.close(); // close connection
		} catch (IOException e) {
            System.out.println(e);
        }
	}

	public static String calc(String line, LinkedList<Double> stack) {

		double num = 0;
		String result = ""; // message to be sent back to the client

		line = line.trim(); // trims the input line for white space

		/* input is either a number or an operation request */
		if (!checkOp(line)) { // check for valid input, stores number in stacks
			try {
				num = Double.parseDouble(line);
				stack.push(num); // push number into the stack
			} catch (NumberFormatException e) { 
				System.out.println("Unknown command. " + e);
				result = "?\n"; // sends ? message back to client
			}
		}
		else { // operation request
			double tmp1 = 0, tmp2;
            boolean empty = false; // check if stack is empty
			try{
				if (stack.peek() == null) {
                    empty = true; // stack is empty
                }
                tmp1 = stack.pop();
				tmp2 = stack.pop();
				double res = doOperation(line, tmp1, tmp2);
				stack.push(res);
				result = res + "\n";
			} catch (NoSuchElementException e) {
				/* if stack is empty and exception is thrown, default
                 * tmp1 = 0 value will not appear in stack */
                if (empty != true) {
                    stack.push(tmp1); 
                }
				System.out.println("not enough numbers on the stack! " + e);
				result = "not enough numbers on the stack!\n";
			}
		}
		return result;
	}

	/* checkOp: checks potential operators to make sure they are valid
	 * input: line sent from the client
	 * output: return true if line is a valid command, else false
	 */
	public static boolean checkOp(String line) {

		if (line.equals("*") || line.equals("/") || line.equals("+") || line.equals("-") || line.equals("show")) {
			return true;
		}
		return false;
	}

	/* doOperation: executes math operation according to the given operator
	 * input: valid operator, number 1, number 2
	 * output: returns result of one of the four mathematical operations
	 */
	public static double doOperation(String op, double tmp1, double tmp2) {

		double res = -1;
		if (op.equals("+")) {
			res = tmp1 + tmp2;
		} else if (op.equals("-")) {
			res = tmp2 - tmp1;
		} else if (op.equals("*")) {
			res = tmp1 * tmp2;
		} else if (op.equals("/")) {
			res = tmp2 / tmp1;
		}
		return res;
	}
}

