package coldjava;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class HTTPClassLoader extends ClassLoader {

    private String host;
    private int port;
    private String classRootDir;

    public HTTPClassLoader(String host, int port, String classRootDir) throws ClassNotFoundException {
        super();
        this.host = host;
        this.port = port;
        this.classRootDir = classRootDir;
    }

    public Class findClass(String className) throws ClassNotFoundException {
        if (className == null) {
            throw new ClassNotFoundException();
        }
        String directoryResource = className.replace(".", "/") + ".class";
        byte[] classBytes;
        try {
            classBytes = getClassBytes(directoryResource);
        } catch (IOException e) {
            throw new ClassNotFoundException(className + "could not be found at " + host);
        }
        return defineClass(className, classBytes, 0, classBytes.length);
    }

    private byte[] getClassBytes(String directoryResource) throws UnknownHostException, IOException, ClassNotFoundException {
        Socket socket = new Socket(host, port);
        PrintWriter output = new PrintWriter(socket.getOutputStream());
        output.println("GET " + classRootDir + "/" + directoryResource + " HTTP/1.1");
        output.println("Host:" + host);
        output.println("");
        output.flush();

        ByteArrayOutputStream data = new ByteArrayOutputStream();

        InputStream stream = socket.getInputStream();
        byte[] statusCodeBytes = new byte[12];
        String statusCode = "";
        stream.read(statusCodeBytes);
        for (int i = 0; i < statusCodeBytes.length; i++) {
            statusCode += (char) statusCodeBytes[i];
        }
        statusCode = statusCode.split(" ")[1];
        if (statusCode.equals("200")) {
            int c;
            boolean newLine = false;
            boolean inContent = false;
            while ((c = stream.read()) != -1) {
                if (inContent) {
                    data.write(c);
                } else {
                    if (c == 10 && newLine) {
                        inContent = true;
                    } else if (c == 13) {
                        continue;
                    } else {
                        newLine = false;
                    }
                    if (c == 10) {
                        newLine = true;
                    }
                }
            }
        } else {
            socket.close();
            throw new ClassNotFoundException(directoryResource + "could not be found at " + host);
        }
        socket.close();

        return data.toByteArray();
    }
}
