package main;

import helper.Constants;
import helper.DataHelper;
import helper.PortHelper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

import org.json.JSONException;
import org.json.JSONObject;

import ui.GUI_V4;

public class RecvFileTask extends SwingWorker<Long, Long> {

	private DataInputStream dis;
	private String recvPath;
	private String fileName;
	private long fileLen;
	private String fileTime;
	private long breakpoint;
	private int port;
	private String srcIP;
	private JSONObject object;
	private Socket socket;

	public RecvFileTask(Socket socket) {
		this.socket = socket;
		init(socket);
	}

	private void init(Socket socket) {
		try {
			socket.setSoTimeout(3000);
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initFileInfo(String fileInfo) {
		try {
			object = new JSONObject(fileInfo);

			recvPath = object.getString(Constants.RECV_PATH);
			fileName = object.getString(Constants.FILE_NAME);
			fileLen = object.getLong(Constants.FILE_LEN);
			fileTime = object.getString(Constants.FILE_TIME);
			breakpoint = object.getLong(Constants.BREAKPOINT);
			port = object.getInt(Constants.PORT);
			srcIP = object.getString(Constants.SRC_IP);

			PortHelper.writeClientToPort(port, srcIP);
			
			// �������߳�thread�ͽ����б����ӳ������
			int index = GUI_V4.recvTableModel.findRowByTime(fileTime);
			System.out.println("index" + index);
			GUI_V4.recvTableModel.setTask(index, this);
			int[] rows = GUI_V4.recvTableModel.getSelectedRows();
			if (rows.length == 1 && rows[0] == index) {
				GUI_V4.recvPauseBtn.setIcon(new ImageIcon("pause.jpg"));
				GUI_V4.recvPauseBtn.setName("pause");
				GUI_V4.recvContinueBtn.setIcon(new ImageIcon(
						"continuenotuse.jpg"));
				GUI_V4.recvContinueBtn.setName("continueNotuse");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/** ���������ļ�  */
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

	/** �ر��ļ�����socket���Ӷ��ж��ļ�����  */
	public void interupt() {
		try {
			dis.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("socket�ѹر�");
		}
	}
	
	/** ���ϵ���Ϣ���浽��ʱ�ļ�  */
	public void saveBreakpointToFile(JSONObject object, long breakpoint) {
		try {
			String fileName = object.getString(Constants.FILE_NAME);
			int index = fileName.lastIndexOf(".");
			fileName = fileName.substring(0, index);
			
			File folder = new File("breakpoint");
			if(!folder.exists()) {
				folder.mkdir();
			}
			//������ʱ�ļ�
			File file = new File(folder, fileName + ".tmp");
			file.createNewFile();

			object.put(Constants.BREAKPOINT, breakpoint);
			
			FileWriter writer = new FileWriter(file);
			writer.write(object.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO: handle exception
		}
	}

	/** ��̨�����ļ����߳�����  */
	@Override
	protected Long doInBackground() throws Exception {
		System.out.println("���ڽ����ļ�");
		RandomAccessFile randFile = null;
		long len = 0;
		try {
			// ���ȶ�ȡ������ļ��йص���Ϣ
			String fileInfo = dis.readUTF();
			initFileInfo(fileInfo);

			randFile = new RandomAccessFile(createFile(), "rw");
			randFile.seek(breakpoint);

			byte[] buffer = new byte[Constants.BUF_SIZE];
			int hasRead = 0;
			len = breakpoint;
			while ((hasRead = dis.read(buffer)) > 0) {
				randFile.write(buffer, 0, hasRead);
				len += hasRead;
				publish(len);
			}
		} catch (SocketTimeoutException e) {
			System.out.println("��ȡ���ݳ�ʱ������");
			GUI_V4.loginState = false;	//��½״̬����Ϊ�Ͽ�����״̬
			saveBreakpointToFile(object, len);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			System.out.println("socket closed");
			saveBreakpointToFile(object, len);
		}catch (IOException e) {
			System.out.println("dis closed");
			saveBreakpointToFile(object, len);
		} finally {
			if (randFile != null) {
				try {
					randFile.close();
					dis.close();
					socket.close();
					System.out.println("�ļ����ս���");
					PortHelper.removeClientFromPort(port, srcIP);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return len;
	}

	/** �����ļ�������Ĵ���  */
	@Override
	protected void done() {
		// TODO Auto-generated method stub
		super.done();
		try {
			long len = get();
			if (len < fileLen) {
				DataHelper.sendHelper.sendBreakpoint(object, len);
				
				int index = GUI_V4.recvTableModel.findRowByTime(fileTime);
				GUI_V4.recvTableModel.setTask(index, null);
				int[] rows = GUI_V4.recvTableModel.getSelectedRows();
				if (rows.length == 1 && rows[0] == index) {
					GUI_V4.recvPauseBtn.setIcon(new ImageIcon("pausenotuse.jpg"));
					GUI_V4.recvPauseBtn.setName("pauseNotuse");
					GUI_V4.recvContinueBtn.setIcon(new ImageIcon("continue.jpg"));
					GUI_V4.recvContinueBtn.setName("continue");
				}
			} else {
				DataHelper.sendHelper.sendComplete(object);
				
				GUI_V4.recvTableModel.deleteRow(fileTime);
				GUI_V4.doneTableModel.addRow(object);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** �ڽ�����ʾ�ļ�����  */
	@Override
	protected void process(List<Long> chunks) {
		// TODO Auto-generated method stub
		super.process(chunks);
		GUI_V4.recvTableModel.setRowProgress(fileTime, chunks.get(0) * 100
				/ fileLen + "%");
	}

}
