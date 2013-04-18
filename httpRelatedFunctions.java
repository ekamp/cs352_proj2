public class httpRelatedFunctions
{
/***Need to create a peer object in order to store peer data and references ...***/

	/*Adds new content to the server */
	/*Sends certain data to a certain peer which will be contacted through reference*/

	private String put (String data, String URL)
	{
		// grab the headers. Ignore all headers except for the pathname and Content-Length.
		// After the headers, read in Content-Length bytes of the content.
		String path = pathname(URL);
		double hashnum = hash(path);
		//Check if peer has this URL?
		if (/*hashnum is in peer*/)
		{
			//Store path in the peer content
			return "HTTP/1.1 200 OK";
		}
		else
		{
			//find the owner of the hash
			//Connect to the peer
			//send a put command for the requested URL and content
			return "HTTP/1.1 200 OK";

		}
	}

	/*returns data associated with the cooresponding URL, sends data in URL reference form */
	private String get (String URL)
	{
		double hashnum;
		int lookupnum;
		String path = pathname(URL);
		if( path.equals("local.html"))
		{
			//Include Content-length
			//Close connection
			return "HTTP/1.1 200 OK"
		}

		hashnum = hash(path);
		if (/*hash in peer */)
		{
			if (lookupnum = lookup(path))
			{
				return "HTTP/1.1 404 NOT FOUND"
			}
			else
			{
				return "HTTP/1.1 200 OK Content-Length"
			}
		}
		else
		{
				//Redirect and find the owner of hash
				return "HTTP/1.1 301 Moved Permanenty Location: http://newhost:/new.port/path"
		}

		//can be redirected
		return null;
	}

	/*Opposite of PUT , basically deletes certain data from the server */
	/* Again here the data is the data and the reference is the reference to the server where you want to delete data from */
	private boolean delete (String data , Peer peerReference, String URL)
	{
		//Grap the pathname from the header
		String pathname = pathname(URL);
		double hashnum = hash(path);
		
		if (/*hash is in peer??*/)
		{
			if(/*pathname is present*/)
			{
				remove(pathname);
				return "HTTP/1.1 200 OK;
			}
			else
			{
				return "HTTP/1.1 400 Not Found"
			}
		}
		else
		{
			//forward the delete
			//Find the owner of the hashnum 
			//Connect to the peer
			return delete(data,owner,URL);
		}


		return true;
	}

	/*Text dump of all data stored at a specified peer */
	private String[] list (Peer peerReference)
	{
		//  -_-  :/ :) ^_^ :p
		return null;
	}

	/*Text dump of all peer information that one peer has on other peers */
	private String[] (Peer peerReference)
	{
		//Basically loop through all peer information and either print to screen or dump in an array of some kind
	}

	/*Removes a peer from a group */
	/***Have to create datastructure for the peer groups ***/
	private boolean remove (Peer peerReference,PeerStructureGroup group)
	{
	/*loop or hash to find the correct peer and remove it from the data structure */
		
		//For each item stored within the removed peer send it to the successor via PUT command
		//delete stored content
		//remove peer no need for loop if using a hash
	}


	/*Gets the hash number associated with a certain pathname */
	private double hashnum(String pathname)
	{
			return 0.0;
	}


	/*Gets the pathname for a given URL */
	private String pathname (String URL)
	{ 
			return "URL";
	}
}
