package coldjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.io.*;
import java.net.*;

public class Http implements Protocol {
    
    Protocol testProto;
    
    
    public String doProtocol(String Uri){
        // Adapted from http://www.vogella.com/articles/JavaNetworking/article.html#javanetwork_example_readpage
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        String address = "";
        System.out.println(Uri);
        Protocol testProto;
        
	try {
	  URI uri = new URI(Uri);
        
          if(uri.getRawSchemeSpecificPart().equals("http")){ //HTTP is already known, so it can run
              URL url = new URL(Uri);
            in = new BufferedReader(new InputStreamReader(url.openStream()));

	  String inputLine;
	  while ((inputLine = in.readLine()) != null) {
		result.append(inputLine);
	  }
          return result.toString();
        }
          else{
              Socket sock = new Socket("localhost", 23657);                                             //Opens the socket to the server
              
              PrintWriter printOut = new PrintWriter(sock.getOutputStream(), true);
              BufferedReader readIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
              printOut.println("CLASS time HTTP/1.0");
              printOut.println("");
              
              BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));              
              String serverInput, userInput;
              
              serverInput = readIn.readLine();
              
              
              while(serverInput != null){
                  System.out.println(serverInput);
                  address = serverInput;
                  serverInput = readIn.readLine();
                  
              }
              
              System.out.println(address);                                                              //Retrieves the address of the class file
              
              HTTPClassLoader load = new HTTPClassLoader("localhost", 23657, "ccoldjava");
              
              Class time = load.findClass("Time");
              testProto = (Protocol)time.newInstance();
              
              
              
              
              
              printOut.close();
              readIn.close();
              stdIn.close();
              sock.close();
                  
              
              
          }
	
        }
        catch (Exception e) {
            System.out.println("unknown protocol");
	  e.printStackTrace();
	}
        
        finally {
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
    
    public Protocol getProtocol(String location){
        return testProto;
   } 
}
