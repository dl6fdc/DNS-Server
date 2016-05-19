
import java.io.Serializable;

public class DNSreply implements Serializable {

    final String address;

    final String hostname;

    public DNSreply (String address, String hostname) {
        this.address = address;
        this.hostname = hostname;
    }

    public String getIP() {
        return address;
    }

    public String getName() {
        return hostname;
    }

}
