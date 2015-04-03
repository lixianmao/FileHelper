package helper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFileChooser;

import main.NoteThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ui.GUI_V4;

/**
 * �������������йصĹ�����
 * @author Administrator
 *
 */
public class DataHelper implements NoteMessage {

	private GUI_V4 gui;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	// ������
	public static SendHelper sendHelper;
	public static RecvHelper recvHelper;

	// ���������йصı���
	private String recentfileName = "";
	private long recentfileLength;
	private String exrecentfileLength = ""; // ת�������λ���ļ����ȣ��ַ��ͣ�
	private String recentfileTime = "";
	private String localIP = "";
	private String destIP = "";

	private String sendFilePath = "";
	private String recvFilePath = "F:/recv/";
	private JSONObject tmpObject;
	
	private NoteThread noteThread;

	public DataHelper(GUI_V4 gui) {
		this.gui = gui;

		initSocket();
		initHelper();
	}
	
	/** �򿪼�����������Ϣ���߳� */
	public void openNoteThread() {
		noteThread = new NoteThread(dis, this);
		new Thread(noteThread).start();
	}
	
	/** ��ʼ���������socket������ */
	public void initSocket() {
		try {
			socket = new Socket(Constants.HOST_NAME, Constants.PORT_NUM);
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("socket���ӳɹ�");
			GUI_V4.loginState = true;
			//�Ͽ����ߺ������������
			if(sendHelper != null) {
				sendHelper.setDos(dos);
				sendHelper.sendLogin(localIP);
				if(noteThread != null) {
					noteThread.stopThread();
					noteThread = null;
				}
				openNoteThread();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("socket����ʧ��");
		}
	}

	/** ��ʼ�����ͺͽ��յĹ�����  */
	private void initHelper() {
		sendHelper = new SendHelper(dos);
		recvHelper = new RecvHelper(this);
	}

	@Override
	public void handleMessage(String msg) {
		// TODO Auto-generated method stub
		recvHelper.handleMessage(msg);
	}

	public SendHelper getSendHelper() {
		return sendHelper;
	}

	public JSONObject setSendObject() {
		JSONObject object = new JSONObject();
		try {
			object.put(Constants.SRC_IP, localIP);
			object.put(Constants.DEST_IP, destIP);
			object.put(Constants.FILE_LEN, recentfileLength);
			object.put(Constants.FILE_NAME, recentfileName);
			object.put(Constants.SEND_PATH, sendFilePath);
			object.put(Constants.FILE_TIME, recentfileTime);
			object.put(Constants.BREAKPOINT, 0);
			object.put(Constants.COMPLETE, 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	/** ���ļ��Ի���ѡ���ļ�����  */
	public void selectFileToSend() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("��");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

			File recentfile = chooser.getSelectedFile();
			sendFilePath = recentfile.getAbsolutePath();
			recentfileName = recentfile.getName();
			recentfileTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(Calendar.getInstance().getTime());
			recentfileLength = recentfile.length();
			exrecentfileLength = exFileLen(recentfileLength);

			gui.recentfileLbl.setText("<html>" + "�ļ�����" + recentfileName
					+ "<br>" + "�ļ���С��" + exrecentfileLength + "<br>" + "���ʱ�䣺"
					+ recentfileTime + "<html>");
		}
	}

	/** ���ļ��Ի���ѡ��·������  */
	public boolean selectPathToSave() {
		boolean path = false;
		JFileChooser savepath = new JFileChooser();
		savepath.setDialogTitle("����");
		savepath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (savepath.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			recvFilePath = savepath.getSelectedFile().getAbsolutePath();
			path = true;
		}
		System.out.println(recvFilePath);
		return path;
	}

	/** ���ļ�����ת��Ϊ���ʵ�λ */
	public String exFileLen(long fileLen) {
		String exrecentfileLength = "";
		DecimalFormat df = new DecimalFormat("#.00");
		if (fileLen < 1024) {
			exrecentfileLength = fileLen + "B";
		} else if (fileLen < 1048576) {
			exrecentfileLength = df.format(fileLen / 1024.0) + "KB";
		} else if (fileLen < 1073741824) {
			exrecentfileLength = df.format(fileLen / 1048576.0) + "MB";
		} else {
			exrecentfileLength = df.format(fileLen / 1073741824.0) + "GB";
		}
		return exrecentfileLength;
	}

	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	public void setDestIP(String destIP) {
		this.destIP = destIP;
	}

	public String getDestIP() {
		return destIP;
	}

	public String getRecentFileName() {
		return recentfileName;
	}

	public JSONObject addRecvObject(JSONObject object) {
		try {
			object.put(Constants.RECV_PATH, recvFilePath);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	public void setRegisterLable(boolean response) {
		if (response) {
			gui.registerokLbl.setText("ע �� �� ��");
		} else {
			gui.registerokLbl.setText("��IP��ע��");
		}
	}

	public void setDengluLable(boolean response) {
		if(response) {
			gui.registerokLbl.setText("�� ¼ �� ��");
//			System.out.println(" FUCK 222 !!!");
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
			gui.loginFrame.setVisible(false); 
			System.out.println("��¼�����ѹر�");
			gui.mainFrame.setVisible(true);
			System.out.println("�ļ������");
//			System.out.println(" FUCK 333 !!!");
		} else {
//			System.out.println(" FUCK 444 !!!");
			gui.registerokLbl.setText("�� �� ע ��");
		}
	}
	
	public void setTmpObject(JSONObject object) {
		this.tmpObject = object;
	}

	public JSONObject getTmpObject() {
		return tmpObject;
	}

	/** ��ʾ�����ϵ��û��б�  */
	public void setUserList(JSONArray array) {
		gui.userMode.removeAllElements();
		for (int i = 0; i < array.length(); i++) {
			try {
				gui.userMode.addElement("     IP: " + array.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** �ڽ�������ʾ�ƶ��ļ��б�  */
	public void setServerFilesList(JSONArray array) {
		gui.serverfileMode.removeAllElements();
		for (int i = 0; i < array.length(); i++) {
			try {
				gui.serverfileMode.addElement("  " + array.getString(i));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** �򿪶Ի���ѡ���ļ��ϴ���������  */
	public File selectFileToUpload() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("�ϴ�");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			return file;
		}
		return null;
	}

	/** �򿪶Ի���ѡ��ӷ����������ļ���·��  */
	public String selectPathToDownload() {
		JFileChooser savepath = new JFileChooser();
		savepath.setDialogTitle("����");
		savepath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (savepath.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return savepath.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

}
