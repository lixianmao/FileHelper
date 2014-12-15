import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketHelper {

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private ClientGUI client;

	public SocketHelper(ClientGUI client) {
		this.client = client;
		initSocket();
	}
	
	public void initSocket() {
		try {
			socket = new Socket(Constants.HOST, Constants.PORT);
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("socket初始化成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("socket初始化失败");
		}
	}

	public DataInputStream getDIS() {
		return dis;
	}

	public DataOutputStream getDOS() {
		return dos;
	}

	public void sendLogin() {
		System.out.println(client.getLocalIP() + " 正在发送登陆请求");
		
		try {
			dos.writeByte(Constants.TYPE_LOGIN);
			dos.writeUTF(client.getLocalIP());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recvLogin() {
		System.out.println("正在接收登陆状态");
		try {
			if (dis.readBoolean()) {
				client.labelLogin.setText("注册成功");
			} else {
				client.labelLogin.setText("用户名已存在");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
