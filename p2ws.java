import java.io.*;
import java.net.*;
import java.util.*;

public class p2ws {



    public static void main(String args[]) {

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
}
