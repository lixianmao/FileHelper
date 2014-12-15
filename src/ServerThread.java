import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread implements Runnable {

	// ��Ϣ����
	private final byte TYPE_LOGIN = 0x0;
	private final byte TYPE_LOGOUT = 0x1;
	private final byte TYPE_REQUEST = 0x2;
	private final byte TYPE_RESPONSE = 0x3;
	private final byte TYPE_INTERUPT = 0x4;
	private final byte TYPE_FILE = 0x5;

	private Socket socket;
	private DataOutputStream destDos;
	private DataInputStream srcDis;
	private DataOutputStream srcDos;
	private byte type = 0x0;
	private boolean threadFlag = true; // �߳����б�־

	public ServerThread(Socket socket) {
		this.socket = socket;
		initStream();
	}

	private void initStream() {
		try {
			srcDis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			srcDos = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("����Դ������IO����������");
		}
	}

	private boolean recvMessage() {
		String srcIp = "";
		String destIp = "";
		System.out.println(this);
		try {
			type = srcDis.readByte();
			switch (type) {
			case TYPE_LOGIN:
				srcIp = srcDis.readUTF();
				System.out.println("login " + srcIp);
				srcDos.writeByte(TYPE_LOGIN);
				if (Server.dosMap.containsKey(srcIp)) {
					srcDos.writeBoolean(false);
				} else {
					srcDos.writeBoolean(true);
					Server.dosMap.put(srcIp, srcDos);
					System.out.println("ip: " + srcIp + " dos: " + srcDos);
				}
				break;
			case TYPE_REQUEST:
				srcIp = srcDis.readUTF();
				destIp = srcDis.readUTF();
				String fileName = srcDis.readUTF();
				long fileLen = srcDis.readLong();
				System.out.println("request: " + srcIp + " " + destIp + " "
						+ fileName + " " + fileLen);

				if (Server.dosMap.containsKey(destIp)) {
					destDos = Server.dosMap.get(destIp);
					destDos.writeByte(TYPE_REQUEST);
					destDos.writeUTF(srcIp);
					destDos.writeUTF(destIp);
					destDos.writeUTF(fileName);
					destDos.writeLong(fileLen);
				} else {
					System.out.println("Ŀ��ͻ�������!");
				}

				break;
			case TYPE_RESPONSE:
				srcIp = srcDis.readUTF();
				destIp = srcDis.readUTF();
				boolean response = srcDis.readBoolean();
				System.out.println("response: " + srcIp + " " + destIp + " "
						+ response);

				destDos = Server.dosMap.get(destIp);
				destDos.writeByte(TYPE_RESPONSE);
				destDos.writeUTF(srcIp);
				destDos.writeUTF(destIp);
				destDos.writeBoolean(response);
				System.out.println(destDos + "����ת��response");
				break;
			case TYPE_FILE: // ת��������
				System.out.println("file");
				destDos.writeByte(TYPE_FILE);
				byte[] buffer = new byte[1024 * 1024];
				int hasRead = 0;
				while ((hasRead = srcDis.read(buffer)) > 0) {
					destDos.write(buffer, 0, hasRead);
				}
				destDos.flush();
				destDos.close();
				break;
			case TYPE_INTERUPT:
				break;
			case TYPE_LOGOUT:
				break;
			default:
				break;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�ļ��ɹ�ת��");
			// e.printStackTrace();
			stopThread();
			return false;
		}
		return true;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (threadFlag) {
			recvMessage();
		}
	}

	private void stopThread() {
		this.threadFlag = false;
	}
}
