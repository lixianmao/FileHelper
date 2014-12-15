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
			System.out.println("socket��ʼ���ɹ�");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("socket��ʼ��ʧ��");
		}
	}

	public DataInputStream getDIS() {
		return dis;
	}

	public DataOutputStream getDOS() {
		return dos;
	}

	public void sendLogin() {
		System.out.println(client.getLocalIP() + " ���ڷ��͵�½����");
		
		try {
			dos.writeByte(Constants.TYPE_LOGIN);
			dos.writeUTF(client.getLocalIP());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recvLogin() {
		System.out.println("���ڽ��յ�½״̬");
		try {
			if (dis.readBoolean()) {
				client.labelLogin.setText("ע��ɹ�");
			} else {
				client.labelLogin.setText("�û����Ѵ���");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
