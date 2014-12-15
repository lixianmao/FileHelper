import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SendHelper {

	private String localIP;
	private String destIP;
	private String sendFilePath = "";
	private String sendFileName;
	private long sendFileLen;

	private DataInputStream dis;
	private DataOutputStream dos;
	private ClientGUI client;

	public SendHelper(ClientGUI client) {
		this.client = client;
		dis = client.getDIS();
		dos = client.getDOS();
	}

	public void setSendInfo(SendInfo sendInfo) {
		localIP = sendInfo.getSrcIP();
		destIP = sendInfo.getDestIP();
		sendFilePath = sendInfo.getSendFilePath();

		File file = new File(sendFilePath);
		sendFileName = file.getName();
		sendFileLen = file.length();
	}

	public void sendRequest() {
		System.out.println("���ڷ����ļ�����");
		try {
			dos.writeByte(Constants.TYPE_REQUEST);
			dos.writeUTF(localIP); // Դ��ַ
			dos.writeUTF(destIP); // Ŀ�ĵ�ַ

			dos.writeUTF(sendFileName); // �ļ���
			dos.writeLong(sendFileLen); // �ļ���С
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void recvResponse() {
		System.out.println("���ڽ��ջ�Ӧ");
		try {
			String srcIP = dis.readUTF();
			dis.readUTF(); // destIP = localIP;
			boolean response = dis.readBoolean();

			if (srcIP.equals(destIP) && response) {
				sendFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendFile() {
		System.out.println("���ڷ����ļ�");
		DataInputStream fileIn = null;
		try {
			dos.writeByte(Constants.TYPE_FILE);

			fileIn = new DataInputStream(new BufferedInputStream(
					new FileInputStream(sendFilePath)));
			byte[] buffer = new byte[1024 * 100];
			int hasRead = 0;
			long len = 0L;
			while ((hasRead = fileIn.read(buffer)) > 0) {
				dos.write(buffer, 0, hasRead);
				len += hasRead;
				client.taSendInfo.setText(sendFileName + "   " + sendFileLen
						/ 1024 + "kb   " + len * 100 / sendFileLen + "%");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fileIn != null) {
				try {
					dos.flush();
					dos.close();
					fileIn.close();
					System.out.println("�ļ�������ɣ�");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
