import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 发送文件的线程，socket连接到服务器指定端口
 * 
 * @author Administrator
 *
 */
public class SendFileThread implements Runnable {

	private Socket socket;
	private DataOutputStream dos;
	// 中断文件传输的标识
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
			ClientGUI.printArea.append("断点信息: " + breakpoint + "\n");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initSocket() {
		try {
			socket = new Socket(Constants.HOST_NAME, port);
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println(": 发送线程socket连接成功: " + socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("发送线程socket连接失败: " + socket);
		}
	}

	public void interupt() {
		intr = true;
	}

	private void sendFile() {
		System.out.println("正在发送文件");
		ClientGUI.printArea.append("正在发送文件\n");

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
				dos.write(buffer, 0, hasRead); // 这个地方有时会出现阻塞
				len += hasRead;
				// System.out.println("len: " + len);
				ClientGUI.taSendInfo
						.setText("已发送：" + len * 100 / fileLen + "%");
			}
			if (len < fileLen) {
				// 文件被中断发送,保存信息
				System.out.println("文件已中断发送");
			} else {
				System.out.println("文件发送完成");
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
					// socket.close(); //可能需要关闭socket
					System.out.println("文件传输完成！");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
