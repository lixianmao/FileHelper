import java.io.DataInputStream;
import java.io.IOException;

public class ClientThread implements Runnable {

	private SendHelper sendHelper;
	private RecvHelper recvHelper;
	private SocketHelper socketHelper;
	private DataInputStream dis;
	private boolean runFlag = true;

	public ClientThread(SocketHelper socketHelper, SendHelper sendHelper, RecvHelper recvHelper) {
		this.socketHelper = socketHelper;
		this.sendHelper = sendHelper;
		this.recvHelper = recvHelper;
		
		dis = socketHelper.getDIS();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (runFlag) {
			listen();
		}
	}

	public void listen() {
		try {
			byte type = dis.readByte();
			switch (type) {
			case Constants.TYPE_LOGIN:
				socketHelper.recvLogin();
				break;
			case Constants.TYPE_REQUEST:
				recvHelper.recvRequest();
				break;
			case Constants.TYPE_RESPONSE:
				sendHelper.recvResponse();
				break;
			case Constants.TYPE_FILE:
				recvHelper.recvFile();
				break;
			case Constants.TYPE_INTERUPT:
				break;
			case Constants.TYPE_LOGOUT:
				break;
			default:
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			runFlag = false;
		}
		
	}
	
}
