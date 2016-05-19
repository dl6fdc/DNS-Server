JCC = javac

default: Client.class Server.class

Client.class: Client.java
	$(JCC) Client.java

Server.class: Server.java
	$(JCC) Server.java

clean:
	$(RM) *.class
	
