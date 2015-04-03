package main;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ������Ϊ�˽����ļ��������˿ں󣬶Ըö˿ڽ��м������߳�
 * @author Administrator
 *
 */
public class FilePortThread implements Runnable {

	private ServerSocket serverSocket;
	private int port;

	public FilePortThread(int port) {
		this.port = port;
	}
	
	private void listen() {
		try {
			serverSocket = new ServerSocket(port, 10);
			System.out.println("file port is open: " + port);
			
			ExecutorService pool = Executors.newFixedThreadPool(10);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("socket accepted: " + socket);
				pool.submit(new RecvFileTask(socket));				//����һ�������̳߳���
			}
		} catch (IOException e) {
			System.out.println("port is already open: " + port);
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		listen();
	}
	
}
