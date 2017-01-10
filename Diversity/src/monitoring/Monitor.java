package monitoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * The Class Monitor.
 */
public class Monitor {

	/**
	 * Update.
	 *
	 * @param uri the uri from the remote API to get posts
	 */
	public static void update(String uri) {
		/*String[] urilists = uri.split(";");
		String account, source, url;
		url ="http://diversity.euprojects.net/socialfeedbackextraction/registerSFE?accounts[]=\"";
		
		for(int i = 0 ; i < urilists.length ; i++){
			source = urilists[i].split(",")[0];	
			account = urilists[i].split(",")[1];
			url += account +"\"&type[]=\""+ source + "\"&" ;
		}
		url = url.substring(0, url.length()-1);
		//System.out.println("****TESTE****" + url +   "  ****TESTE****\n");
		
        /*URL registeraccount;
		try {
			registeraccount = new URL(url);

        BufferedReader in = new BufferedReader(new InputStreamReader(registeraccount.openStream()));

        String status;
        while ((status = in.readLine()) != null)
            System.out.println(status);
        in.close();
        
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
	}
	
	/**
	 * Delete.
	 *
	 * @param uri the uri
	 */
	public static void delete(String uri) {
		// TODO By Francisco Silva
		
	}

}
