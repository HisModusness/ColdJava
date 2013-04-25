package coldjava;

public class NotSupported implements Protocol {
    public String doProtocol(String Uri) {
        return "This protocol is not supported.";
    }
}