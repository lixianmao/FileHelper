import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread implements Runnable {

	// 消息类型
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
	private boolean threadFlag = true; // 线程运行标志

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
			System.out.println("来自源主机的IO流发生错误");
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
					System.out.println("目标客户不存在!");
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
				System.out.println(destDos + "正在转发response");
				break;
			case TYPE_FILE: // 转发数据流
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
			System.out.println("文件成功转发");
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
