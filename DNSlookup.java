
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;

public interface DNSlookup extends Remote {
	
    DNSreply lookup (String hostname) throws RemoteException;

}
