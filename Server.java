import java.net.*;	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
	
public class Server implements DNSlookup {
	
    private static String filename, mode;    
    private static int id, port;
    private LinkedList<DNSreply> cache = new LinkedList<DNSreply>();
    
    public Server() {}

    public DNSreply lookup (String hostname)
    {
        String line;
	    String part1, part2, address;
	    int index;  
	    boolean app = true;
	    DNSreply result = new DNSreply("Not Found", hostname);
	    
	    // parse hostname
	    index = hostname.lastIndexOf('.');
	    part1 = hostname.substring(0, index);
	    part2 = hostname.substring(index+1);
    
        
        try (BufferedReader bread = new BufferedReader(new FileReader(filename));
            BufferedWriter bwrite = new BufferedWriter(new FileWriter("cache.log", app)))
	    {	
		    while ((line = bread.readLine()) != null)
		    {	
                if (id == 1)
                {
                    if ((index = line.indexOf(part2)) != -1)
                    {
                        address = line.substring(index+part2.length());
                        address = address.trim();
                        
                        if (mode.equals("R"))
                        {
                            int i;
                            for (i = 0; i < cache.size(); i++)
                            {
                                if (cache.get(i).getName().equals(hostname))
                                {   
				                    result = cache.get(i); 
                                    String s = "[" + result.getName() + "\t" + result.getIP() + "\tHIT]\n";
                                    bwrite.write(s);   
                               	    break;
                                }
                            }
                            
                            if (i == cache.size())
                            {   try {
	                                Registry registry = LocateRegistry.getRegistry(address, port);
	                                DNSlookup stub = (DNSlookup) registry.lookup("DNSlookup");
	                                DNSreply response = stub.lookup(hostname);
	                                result = new DNSreply(response.getIP(), hostname);
	                                if (!response.getIP().equals("Not Found"))
	                                {   cache.add(result);
	                                    if (cache.size() > 32)
	                                        cache.removeFirst();
	                                }
	                            } catch (Exception e) {
	                                System.err.println("server1 recursive exception: " + e.toString());
	                                e.printStackTrace();
	                            }
                            }
                            break;
                            
                        }
                        else if (mode.equals("I"))
                        {
                            result = new DNSreply(address, part2);
                            break;
                        }
                        else
                            break;
                    }
                }
                
                
                if (id == 2)
                {
                    if ((index = line.indexOf(part1)) != -1)
                    {
                        address = line.substring(index+part1.length());
                        address = address.trim();
                        result = new DNSreply(address, part1);
                        break;
                    }
                }
 
            }
	    } catch(IOException exc){
	    	System.err.println("Error dealing with file.");
	    } 
	    
//	    System.out.println("done with lookup.");

        return result;

    }
	
	
    public static void main(String args[]) {
    
    if (args.length != 4)
		System.out.println("Usage: java server I/R fileLookup id port.");
		
    filename = args[1];
    id = Integer.valueOf(args[2]);
    mode = args[0];
    port = Integer.valueOf(args[3]);
    
	try {
            Socket s = new Socket("google.com", 80);
            System.setProperty("java.rmi.server.hostname",s.getLocalAddress().getHostAddress());
            s.close();
	    Server obj = new Server();
	    DNSlookup stub = (DNSlookup) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(port);
	    registry.bind("DNSlookup", stub);

	    System.err.println("Server ready");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }
}

