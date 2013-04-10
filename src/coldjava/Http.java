package coldjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class Http implements Protocol {
    public String doProtocol(String Uri) {
        // Adapted from http://www.vogella.com/articles/JavaNetworking/article.html#javanetwork_example_readpage
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
	try {
	  URL url = new URL(Uri);
	  in = new BufferedReader(new InputStreamReader(url.openStream()));

	  String inputLine;
	  while ((inputLine = in.readLine()) != null) {
		result.append(inputLine);
	  }
          return result.toString();
	} catch (Exception e) {
	  e.printStackTrace();
	} finally {
	  if (in != null) {
		try {
		  in.close();
                  return result.toString();
		} catch (IOException e) {
		  e.printStackTrace();
		}
	  }
          return result.toString();
	}

    }
}