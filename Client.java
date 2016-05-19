
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.*;


public class Client {

    private Client() {}

    public static void main(String[] args) {

	String line;
	String result;
	boolean app = true;
	String server1, server2;
	int port;
	
	if (args.length != 4)
		System.out.println("Usage: java Client server1 server2 port inputfile.");
		
	server1 = args[0];	
	server2 = args[1];
	port = Integer.valueOf(args[2]);
		
	try (BufferedReader bread = new BufferedReader(new FileReader(args[3]));
		BufferedWriter bwrite = new BufferedWriter(new FileWriter("dlout.txt", app)))
	{	
		while ((line = bread.readLine()) != null)
		{	line = line.trim();
            result = line + "\t\t\t\t" + "Not Found\n";

            try {
	            Registry registry = LocateRegistry.getRegistry(server1, port);
        	    DNSlookup stub = (DNSlookup) registry.lookup("DNSlookup");
	    
	            DNSreply response = stub.lookup(line);
	            //System.out.println(response.getName() + "\t\t" + response.getIP());
	            
	            if (!response.getIP().equals("Not Found"))
	            {
	                if (!response.getName().equals(line))
	                {   // Iterative
	                    try {
	                    Registry registry2 = LocateRegistry.getRegistry(server2, port);
        	            DNSlookup stub2 = (DNSlookup) registry2.lookup("DNSlookup");
	                    DNSreply response2 = stub2.lookup(line);
	                    //System.out.println(response2.getName() + "\t\t" + response2.getIP());
	                    result = line + "\t\t\t\t" + response2.getIP() + "\n";
	                    } catch (Exception e) {
	                        System.err.println("Client Iterative exception: " + e.toString());
	                        e.printStackTrace();
	                    }
	                    
	                }
	                else
	                    // recursive
	                    result = line + "\t\t\t\t" + response.getIP() + "\n";
	            }
	          
	            //System.out.println(result);
	            bwrite.write(result);
	            	            
	        } catch (Exception e) {
	            System.err.println("Client exception: " + e.toString());
	            e.printStackTrace();
	        }
		}
	} catch(IOException exc)
	{	System.err.println("Error dealing with file.");
	} 

	System.out.println("done.");
    }
}

