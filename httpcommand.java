import java.net.ServerSocket;
import java.util.HashMap;

public class httpcommand {

    public static String get(String url) throws Exception {

        System.out.println("url: " + url);

        String response = null, hashnum;

        if (url.equals("/local.html")) {
            response = "HTTP/1.1 200 OK" + '\n';
            
            String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" + '\n'
            		+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">" + '\n'
            		+ "<head>" + '\n' 
            		+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" + '\n' 
            		+ "<title> Local page </title>" + '\n' 
            		+ "</head>" + '\n' 
            		+ "<body>" + '\n'
            		+ "<p> This is the local page on peer server " + " port " + "</p>" + '\n'
            		+ "</body>" + '\n' 
            		+ "</html>";
            
            response = response + content.length() + '\n' + content + '\n';
            
            //System.out.println("content: " + content);
        }
        
        hashnum = hashfunction.md5(url);
        System.out.println("hash: " + hashnum);
        
        /* if h in this peer, 
         *    if not found, send HTTP/1.1 404 Not Found
         *    else send HTTP/1.1 200 OK, content-length, content
         * else
         *    redirect and search for owner for h
         *    send HTTP/1.1 301 Moved Permanently, location
         */
        
        //System.out.println("response: " + response);
        return response;

    }
    
    
    public static void put(String url, String data)
    {
    	
    	
    	//hashmap.hm.put(hashfunction.md5(url), data);
    	
    }
    

}
