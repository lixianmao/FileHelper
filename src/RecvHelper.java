import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecvHelper {

	private String srcIP;
	private String recvFilePath = "F:/recv/";
	private long recvFileLen;

	private DataInputStream dis;
	private DataOutputStream dos;
	private ClientGUI client;

	public RecvHelper(ClientGUI client) {
		this.client = client;
		dis = client.getDIS();
		dos = client.getDOS();
	}

	private void setSrcIP(String ip) {
		srcIP = ip;
	}

	private void setRecvFilePath(String path) {
		recvFilePath = path;
	}

	private void setRecvFileLen(long len) {
		recvFileLen = len;
	}

	public void recvRequest() {
		System.out.println("正在接收文件请求");
		try {
			String srcIP = dis.readUTF();
			dis.readUTF(); // destIP = localIP
			String fileName = dis.readUTF();
			long fileLen = dis.readLong();

			setSrcIP(srcIP);
			setRecvFilePath(recvFilePath + fileName);
			setRecvFileLen(fileLen);
			client.taRecvRequest.setText("收到来自 " + srcIP + " 的文件请求：" + fileName
					+ " " + fileLen / 1024 + "kb");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendResponse(boolean response) {
		System.out.println("正在发送回应");
		try {
			dos.writeByte(Constants.TYPE_RESPONSE);
			dos.writeUTF(client.getLocalIP());
			dos.writeUTF(srcIP);
			dos.writeBoolean(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recvFile() {
		System.out.println("正在接收文件");
		DataOutputStream fileOut = null;
		try {
			fileOut = new DataOutputStream(new FileOutputStream(recvFilePath));
			byte[] buffer = new byte[1024 * 100];
			int hasRead = 0;
			long len = 0L;
			while ((hasRead = dis.read(buffer)) > 0) {
				fileOut.write(buffer, 0, hasRead);
				len += hasRead;

				client.taRecvInfo.setText(recvFilePath + " " + len * 100 / recvFileLen + "%");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (fileOut != null) {
				try {
					fileOut.flush();
					fileOut.close();
					System.out.println("文件接收完成");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void createFile() {
		File file = new File(recvFilePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
