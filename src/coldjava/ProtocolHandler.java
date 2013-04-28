package coldjava;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

public class ProtocolHandler {

    static HashMap<String, Protocol> cache = new HashMap<String, Protocol>();

    public ProtocolHandler() {
        if (!cache.containsKey("http")) {
            cache.put("http", new Http());
        }
        if (!cache.containsKey("ns")) {
            cache.put("ns",new NotSupported());
        }
    }

    public Protocol getProtocol(String Uri) {
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        String address = "";
        System.out.println(Uri);
        Protocol testProto = null;

        try {
            String protocol;
            if (Uri.indexOf(':') == Uri.length() - 1) {
                protocol = Uri.substring(0, Uri.length() - 1);
            } else {
                URI uri = new URI(Uri);
                protocol = uri.getScheme();
            }
            if (cache.containsKey(protocol)) {
                return cache.get(protocol);
            }

            Socket sock = new Socket("localhost", 23657);                                             //Opens the socket to the server

            PrintWriter printOut = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader readIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            printOut.println("CLASS " + protocol + " HTTP/1.0");
            printOut.println("");

            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String serverInput, userInput;

            serverInput = readIn.readLine();
            int code = Integer.valueOf(serverInput.split(" ")[1]);
            if (code == 501) {
                return cache.get("ns");
            }

            while (serverInput != null) {
                System.out.println(serverInput);
                address = serverInput;
                serverInput = readIn.readLine();

            }

            System.out.println(address);                                                              //Retrieves the address of the class file

            HTTPClassLoader load = new HTTPClassLoader("localhost", 23657, "");

            Class protoClass = load.findClass(address);
            testProto = (Protocol) protoClass.newInstance();
            cache.put(protocol, testProto);

            printOut.close();
            readIn.close();
            stdIn.close();
            sock.close();

        } catch (Exception e) {
            System.out.println("unknown protocol");
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return testProto;
    }
    
    public String sendProtocol(String filePath, String protocolName) {
        if (!filePath.endsWith(".java")) {
            return "ERROR: The file cannot be identified as java source.";
        }
        String packageName = "";
        StringBuilder source = new StringBuilder();
        
        // The file now at least pretends to be java source.
        try {
            File sourceFile = new File(filePath);
            BufferedReader fileIn = new BufferedReader(new FileReader(new File(filePath)));
            
            String currentLine = fileIn.readLine();
            
            while (currentLine != null) {
                if (currentLine.startsWith("package ")) {
                    if (packageName.equals("")) {
                        packageName = currentLine.substring(currentLine.indexOf("package ") + "package ".length(), currentLine.indexOf(";"));
                    }
                }
                
                source.append(currentLine).append("\n");
                currentLine = fileIn.readLine();
            }
            if (!packageName.equals("")) packageName += ".";
            System.out.println(sourceFile.getName());
            packageName += sourceFile.getName().substring(0, sourceFile.getName().indexOf(".java"));
        }
        catch (IOException e) {
            return "ERROR: There was an error while reading in the file";
        }
        
        // With the file stored and ready to send, let's contact the server
        Socket sock = null; 
        PrintWriter printOut = null;
        BufferedReader readIn = null;
        try {
            sock = new Socket("localhost", 23657);
            printOut = new PrintWriter(sock.getOutputStream(), true);
            readIn = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            
            printOut.println("UPLOAD " + packageName + " HTTP/1.1");
            printOut.println("Content-Length: " + source.length());
            printOut.println("protocol: " + protocolName);
            printOut.write(source.toString());
            printOut.flush();
            //printOut.close();
            
            String response = readIn.readLine();
            int responseCode = 0;
            if (response != null && response.length() > 0)responseCode = Integer.valueOf(response.split(" ")[1]);
            
            readIn.close();
            
            if (responseCode == 200) {
                return "The protocol was installed successfully.";
            }
            if (responseCode == 500) {
                return "ERROR: The server reported an error while installing the protocol.";
            }
            return "ERROR: An unknown error occurred.";
            
        }
        catch (IOException e) {
            printOut.close();
            try { readIn.close(); } catch (Exception ex) { }
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "ERROR: An unknown error occurred.";
        }
        
    }
    
}