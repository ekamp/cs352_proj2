/* Group Members:
 * Jingfei Shi
 * Lindsey Crocker
 * Erik Kamp
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class p2pws implements Runnable {
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
        System.out.println("port number: " + portnum);
    }

    ServerSocket svc = null; // new server socket

    try {
        svc = new ServerSocket(portnum, 5);
    } catch (BindException e) {
        System.out.println(e);
        Ssytem.exit(4);
    }

    for (;;) {


    

}
