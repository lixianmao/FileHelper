import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	private final int PORT_NUM = 6666;
	private final int QUEUE_LENGTH = 10;	//������е���󳤶�
	private final int POOL_SIZE = 5;		//�̳߳صĴ�С
	
	public static HashMap<String, DataOutputStream> dosMap;
	
	private ServerSocket serverSocket;

	private void init() {
		try {
			serverSocket = new ServerSocket(PORT_NUM, QUEUE_LENGTH);
			System.out.println("ServerSocket started: " + serverSocket);
			System.out.println("Server: " + Thread.currentThread());
			dosMap = new HashMap<String, DataOutputStream>();
			ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("socket accepted: " + socket);
				pool.submit(new ServerThread(socket));				//����һ�������̳߳���
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new Server().init();
	}
	
}
