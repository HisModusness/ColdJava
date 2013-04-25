package coldjava;

import java.io.BufferedReader;
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
}