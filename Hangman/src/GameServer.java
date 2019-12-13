import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class GameServer {
	
	ArrayList<String> loginNames;
	ArrayList<Socket> clientSockets;
	
	public class Client extends Thread {
		
		Socket clientSocket;

		DataInputStream in;
		DataOutputStream out;
		int pos = 0;
		int i = 0;
		 
		public Client(Socket client) throws IOException {
			clientSocket = client;
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());
			
			String loginName = in.readUTF();
			loginNames.add(loginName);
			for(String lName : loginNames) {
				out.writeUTF("LOGIN "+lName);
			}
			
			clientSockets.add(clientSocket);

			start();
		}
		
		public void run() {
			while(true) {
				try {
					String msgFrmClient = in.readUTF();
					StringTokenizer msgParts = new StringTokenizer(msgFrmClient);
					String msgType = msgParts.nextToken();
					String name = msgParts.nextToken();
					
					
					switch(msgType) {
					case "LOGIN":
						clientSockets.forEach(socket -> {
							notifyLogin(socket, name);
						});
						break;	
					case "WORD":
						String word = msgParts.nextToken();
						System.out.println("Server received "+word);
						for(int i=0; i<loginNames.size(); i++) {
							if(name.compareTo(loginNames.get(i)) == 0) {
								sendWord(clientSockets.get(i), name, word);
							}
						}
						break;
					case "LOGOUT":
						clientSockets.forEach(socket -> {
							if(name.equals(loginNames.get(i++)))
								pos = i-1;
							performLogout(socket,name);
							
						});
						
						loginNames.remove(pos);
						clientSockets.remove(pos);
						break;
					}
				
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	GameServer() throws IOException {
		ServerSocket server = new ServerSocket(5215);
		loginNames = new ArrayList<String>();
		clientSockets = new ArrayList<Socket>();
		
		while (true) {
			Socket clientSocket = server.accept();
			new Client(clientSocket);
		}
	}
	
	public void performLogout(Socket socket, String name) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("LOGOUT "+name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void notifyLogin(Socket socket, String name) {
		try {
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("LOGIN "+name);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void sendWord(Socket socket, String name, String word) {
		DataOutputStream out;
		try {
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF("WORD "+word);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public static void main(String[] args) throws IOException {
		new GameServer();

	}

}
