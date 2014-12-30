import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class RecvFileThread implements Runnable {

	private DataInputStream dis;

	private String recvPath;
	private String fileName;
	private long fileLen;
	private long breakpoint;
	private int port;
	private String srcIP;
	private JSONObject object;

	public RecvFileThread(Socket socket) {
		init(socket);
	}

	private void init(Socket socket) {
		try {
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		recvFile();
	}

	private void initFileInfo(String fileInfo) {
		try {
			object = new JSONObject(fileInfo);
			recvPath = object.getString(Constants.RECV_PATH);
			fileName = object.getString(Constants.FILE_NAME);
			fileLen = object.getLong(Constants.FILE_LEN);
			breakpoint = object.getLong(Constants.BREAKPOINT);
			port = object.getInt(Constants.PORT);
			srcIP = object.getString(Constants.SRC_IP);

			PortHelper.writeClientToPort(port, srcIP);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private File createFile() {
		File file = new File(recvPath, fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file;
	}

	private void recvFile() {
		System.out.println("正在接收文件");
		ClientGUI.printArea.append("正在接收文件\n");
		RandomAccessFile randFile = null;
		try {
			// 首先读取与接收文件有关的信息
			String fileInfo = dis.readUTF();
			initFileInfo(fileInfo);

			randFile = new RandomAccessFile(createFile(), "rw");
			randFile.seek(breakpoint);

			byte[] buffer = new byte[Constants.BUF_SIZE];
			int hasRead = 0;
			long len = breakpoint;
			while ((hasRead = dis.read(buffer)) > 0) {
				randFile.write(buffer, 0, hasRead);
				len += hasRead;
				//System.out.println(len);
				ClientGUI.taRecvInfo
						.setText("已接收：" + len * 100 / fileLen + "%");
			}
			// 文件传输中断，向服务器发送断点信息
			if (breakpoint + len < fileLen) {
				ClientGUI.sendHelper.sendBreakpoint(object, len);
			} else {
				ClientGUI.sendHelper.sendComplete(object);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (randFile != null) {
				try {
					randFile.close();
					dis.close();
					System.out.println("文件接收完成");

					PortHelper.removeClientFromPort(port, srcIP);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
