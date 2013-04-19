import java.security.*;
import java.io.*;

public class hashfunction {

    /*
    public static void main(String args[]) throws Exception {

        String file = "/test/index.html";
        System.out.println(md5(file));
    }
    */
    public static String md5(String filename) throws Exception {

        String digest = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(filename.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder(2*hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b&0xff));
            }

            digest = sb.toString();
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }

        String hash = digest.substring(digest.length()-4, digest.length());
        return hash;
    }
}


