import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * �����ļ����̣߳�socket���ӵ�������ָ���˿�
 * 
 * @author Administrator
 *
 */
public class SendFileThread implements Runnable {

	private Socket socket;
	private DataOutputStream dos;
	// �ж��ļ�����ı�ʶ
	private boolean intr;

	private int port;
	private String sendFilePath;
	private String fileInfo;
	private long fileLen;
	private long breakpoint;

	public SendFileThread(JSONObject object) {
		initFileInfo(object);
		initSocket();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		sendFile();
	}

	private void initFileInfo(JSONObject sendObject) {
		try {
			sendFilePath = sendObject.getString(Constants.SEND_PATH);
			port = sendObject.getInt(Constants.PORT);
			fileLen = sendObject.getLong(Constants.FILE_LEN);
			breakpoint = sendObject.getLong(Constants.BREAKPOINT);
			
			fileInfo = sendObject.toString();
			ClientGUI.printArea.append("�ϵ���Ϣ: " + breakpoint + "\n");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initSocket() {
		try {
			socket = new Socket(Constants.HOST_NAME, port);
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println(": �����߳�socket���ӳɹ�: " + socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�����߳�socket����ʧ��: " + socket);
		}
	}

	public void interupt() {
		intr = true;
	}

	private void sendFile() {
		System.out.println("���ڷ����ļ�");
		ClientGUI.printArea.append("���ڷ����ļ�\n");

		intr = false;
		RandomAccessFile randFile = null;
		try {
			JSONObject fileObject = new JSONObject(fileInfo);
			fileObject.put(Constants.TYPE, Constants.TYPE_FILE);
			dos.writeUTF(fileObject.toString());

			randFile = new RandomAccessFile(sendFilePath, "r");
			randFile.seek(breakpoint);

			byte[] buffer = new byte[Constants.BUF_SIZE];
			int hasRead = 0;
			long len = breakpoint;

			while ((hasRead = randFile.read(buffer)) > 0 && !intr) {
				dos.write(buffer, 0, hasRead); // ����ط���ʱ���������
				len += hasRead;
				// System.out.println("len: " + len);
				ClientGUI.taSendInfo
						.setText("�ѷ��ͣ�" + len * 100 / fileLen + "%");
			}
			if (len < fileLen) {
				// �ļ����жϷ���,������Ϣ
				System.out.println("�ļ����жϷ���");
			} else {
				System.out.println("�ļ��������");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (randFile != null) {
				try {
					dos.flush();
					dos.close();
					randFile.close();
					// socket.close(); //������Ҫ�ر�socket
					System.out.println("�ļ�������ɣ�");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
