import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务器为了接收文件而新增端口后，对该端口进行监听的线程
 * @author Administrator
 *
 */
public class FilePortThread implements Runnable {

	private int port;
	private ServerSocket serverSocket;

	public FilePortThread(int port) {
		this.port = port;
	}

	private void listen() {
		try {
			System.out.println("fuck you");
			serverSocket = new ServerSocket(port, 10);
			System.out.println("file port is open: " + port);

			ExecutorService pool = Executors.newFixedThreadPool(4);
			while (true) {
				Socket socket = serverSocket.accept();
				System.out.println("socket accepted: " + socket);
				pool.submit(new RecvFileThread(socket));				//加入一个任务到线程池中
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		listen();
	}
	
}
