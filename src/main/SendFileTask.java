package main;

import helper.Constants;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import org.json.JSONException;
import org.json.JSONObject;

import ui.GUI_V4;

public class SendFileTask extends SwingWorker<Long, Long> {

	private Socket socket;
	private DataOutputStream dos;

	private int port;
	private String sendFilePath;
	private String fileInfo;
	private long fileLen;
	private String fileTime;
	private long breakpoint;
	private JSONObject fileObject;
	private String destIP;

	public SendFileTask(JSONObject object) {
		initFileInfo(object);
		initSocket();
	}

	private void initFileInfo(JSONObject sendObject) {
		try {
			sendFilePath = sendObject.getString(Constants.SEND_PATH);
			port = sendObject.getInt(Constants.PORT);
			fileLen = sendObject.getLong(Constants.FILE_LEN);
			fileTime = sendObject.getString(Constants.FILE_TIME);
			breakpoint = sendObject.getLong(Constants.BREAKPOINT);
			destIP = sendObject.getString(Constants.DEST_IP);

			fileInfo = sendObject.toString();
			fileObject = new JSONObject(fileInfo);
			fileObject.put(Constants.TYPE, Constants.TYPE_FILE);
			// �������̺߳ͷ����б����ӳ������
			int index = GUI_V4.sendTableModel.findRowByTime(fileTime);
			GUI_V4.sendTableModel.setTask(index, this);
			int[] rows = GUI_V4.sendTableModel.getSelectedRows();
			if (rows.length == 1 && rows[0] == index) {
				GUI_V4.sendPauseBtn.setIcon(new ImageIcon("pause.jpg"));
				GUI_V4.sendPauseBtn.setName("pause");
				GUI_V4.sendContinueBtn.setIcon(new ImageIcon(
						"continuenotuse.jpg"));
				GUI_V4.sendContinueBtn.setName("continueNotuse");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initSocket() {
		try {
			socket = new Socket(destIP, port);
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("�����߳�socket���ӳɹ�: " + socket);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("�����߳�socket����ʧ��: " + socket);
		}
	}

	/** �ر��ļ�����socket���Ӷ��ж��ļ�����  */
	public void interupt() {
		try {
			dos.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** ��̨�����ļ����߳�����  */
	@Override
	protected Long doInBackground() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("���ڷ����ļ�");

		RandomAccessFile randFile = null;
		long len = breakpoint;
		try {
			dos.writeUTF(fileObject.toString());

			randFile = new RandomAccessFile(sendFilePath, "r");
			randFile.seek(breakpoint);

			byte[] buffer = new byte[Constants.BUF_SIZE];
			int hasRead = 0;
			while ((hasRead = randFile.read(buffer)) > 0) {
				dos.write(buffer, 0, hasRead);
				len += hasRead;
				publish(len);
			}
		} catch (SocketException e) {
			System.out.println("socket closed");
			GUI_V4.loginState = false;	//��½״̬����Ϊ�Ͽ�����״̬
		} catch (IOException e) {
			GUI_V4.loginState = false;	//��½״̬����Ϊ�Ͽ�����״̬
		} finally {
			if (randFile != null) {
				try {
					dos.flush();
					dos.close();
					randFile.close();
					System.out.println("�ļ�����رգ�");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return len;
	}

	/** �ڽ�����ʾ�ļ�����  */
	@Override
	protected void process(List<Long> chunks) {
		// TODO Auto-generated method stub
		super.process(chunks);
		GUI_V4.sendTableModel.setRowProgress(fileTime, chunks.get(0) * 100
				/ fileLen + "%");

	}

	/** �ļ����ͽ�����Ĵ���  */
	@Override
	protected void done() {
		// TODO Auto-generated method stub
		super.done();

		try {
			long len = get();
			if (len < fileLen) {
				// �ļ����жϷ���,������Ϣ
				System.out.println("�ļ����жϷ���");

				int index = GUI_V4.sendTableModel.findRowByTime(fileTime);
				GUI_V4.sendTableModel.setTask(index, null);
				int[] rows = GUI_V4.sendTableModel.getSelectedRows();
				if (rows.length == 1 && rows[0] == index) {
					GUI_V4.sendPauseBtn
							.setIcon(new ImageIcon("pausenotuse.jpg"));
					GUI_V4.sendPauseBtn.setName("pauseNotuse");
					GUI_V4.sendContinueBtn
							.setIcon(new ImageIcon("continue.jpg"));
					GUI_V4.sendContinueBtn.setName("continue");
				}
			} else {
				System.out.println("�ļ��������");

				GUI_V4.sendTableModel.deleteRow(fileTime);
				GUI_V4.doneTableModel.addRow(fileObject);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
