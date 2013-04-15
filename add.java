import java.io.*;
import java.net.*;
import java.util.*;



public class add{
		//Function / class that will add a new peer to the network
		public static void main (String args[])
		{
				//Takes in three arguments IP address, port number, peer that will add new peer? 
				int portnum = 12345; // default port number
				String IPaddress = "192.168.1.1"; //default IP
				int[] ip;
				/* checking arguments for correct format */
				if (args.length == 2) {
						try { /* convert portnum to integer */
								portnum = Integer.parseInt(args[1]);
						} catch (NumberFormatException e) {
								System.out.println(e);
								System.exit(1);
						}
						/* check for port number range */ 
						if (portnum < 1024 || portnum > 63335) {
								System.out.println("Port is not in range, exiting.");
								System.exit(2);
						}
				} else if (args.length > 2) {
						System.out.println("Too many arguments, exiting.");
						System.exit(3);
				}
				ip = ipresolver(IPaddress);
				printIP(ip);
				System.out.println("IP address is : " + IPaddress); 
				System.out.println("port number: " + portnum);
		}
		//should really make this a data structure, basicallt separates the IP into its parts and returns them as an int array
		private static int[] ipresolver (String ipaddress)
		{
		    int i = 0 ;
			int temp;
			int[] parsedIP = new int[4];
			StringTokenizer tok = new StringTokenizer(ipaddress, ".");
			while (tok.hasMoreTokens() && i < 4)
			{
				temp = Integer.parseInt(tok.nextToken());
				if (temp > 255 || temp < 0)
				{
						System.out.println("IP address is incorrect");
						System.exit(4);
				}
				parsedIP[i] = temp;
				i++;
			}
			return parsedIP;
		}


//Checker to print the IP array
		private static void printIP (int[] array)
		{
			for (int i = 0 ; i<4 ; i++)
			{
				System.out.println( array[i] );	
			}
		}
}
