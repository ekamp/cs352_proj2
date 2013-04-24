/* GROUP MEMBERS:
 * Jingfei Shi
 * Erik Kamp
 * Lindsey Crocker
 */

import java.security.*;

public class hashfunction {

	/* md5()
	 * - take the file name (path) and outputs the md5 hash representation
	 */
    public static String md5(String filename) throws Exception {

        String digest = null, hashval = "";
        byte[] hash;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hash = md.digest(filename.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder(2*hash.length);
            
            /* for each byte in hash, bitwise and with hexadecimal,
             * turns the single byte into two-digit hex string,
             * append to StringBuilder sb  */
            for (byte b : hash) {
                sb.append(String.format("%02x", b&0xff));
            }
            digest = sb.toString(); // convert to String representation
        } catch (Exception e) {
            System.out.println("ERROR: Unable to hash into md5.");
        }

        /* get last four characters of md5 hash */
        hashval = digest.substring(digest.length()-4, digest.length());
        return hashval;
    }
}


