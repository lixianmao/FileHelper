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
 * 与界面操作数据有关的工具类
 * @author Administrator
 *
 */
public class DataHelper implements NoteMessage {

	private GUI_V4 gui;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	// 工具类
	public static SendHelper sendHelper;
	public static RecvHelper recvHelper;

	// 与界面操作有关的变量
	private String recentfileName = "";
	private long recentfileLength;
	private String exrecentfileLength = ""; // 转换后带单位的文件长度（字符型）
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
	
	/** 打开监听服务器消息的线程 */
	public void openNoteThread() {
		noteThread = new NoteThread(dis, this);
		new Thread(noteThread).start();
	}
	
	/** 初始化与服务器socket的连接 */
	public void initSocket() {
		try {
			socket = new Socket(Constants.HOST_NAME, Constants.PORT_NUM);
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("socket连接成功");
			GUI_V4.loginState = true;
			//断开网线后进行重新连接
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
			System.out.println("socket连接失败");
		}
	}

	/** 初始化发送和接收的工具类  */
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

	/** 打开文件对话框，选择文件发送  */
	public void selectFileToSend() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("打开");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

			File recentfile = chooser.getSelectedFile();
			sendFilePath = recentfile.getAbsolutePath();
			recentfileName = recentfile.getName();
			recentfileTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(Calendar.getInstance().getTime());
			recentfileLength = recentfile.length();
			exrecentfileLength = exFileLen(recentfileLength);

			gui.recentfileLbl.setText("<html>" + "文件名：" + recentfileName
					+ "<br>" + "文件大小：" + exrecentfileLength + "<br>" + "添加时间："
					+ recentfileTime + "<html>");
		}
	}

	/** 打开文件对话框，选择路径接收  */
	public boolean selectPathToSave() {
		boolean path = false;
		JFileChooser savepath = new JFileChooser();
		savepath.setDialogTitle("保存");
		savepath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (savepath.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			recvFilePath = savepath.getSelectedFile().getAbsolutePath();
			path = true;
		}
		System.out.println(recvFilePath);
		return path;
	}

	/** 将文件长度转换为合适单位 */
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
			gui.registerokLbl.setText("注 册 成 功");
		} else {
			gui.registerokLbl.setText("此IP已注册");
		}
	}

	public void setDengluLable(boolean response) {
		if(response) {
			gui.registerokLbl.setText("登 录 成 功");
//			System.out.println(" FUCK 222 !!!");
			try {
				Thread.sleep(600);
			} catch (InterruptedException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			gui.loginFrame.setVisible(false); 
			System.out.println("登录界面已关闭");
			gui.mainFrame.setVisible(true);
			System.out.println("文件界面打开");
//			System.out.println(" FUCK 333 !!!");
		} else {
//			System.out.println(" FUCK 444 !!!");
			gui.registerokLbl.setText("请 先 注 册");
		}
	}
	
	public void setTmpObject(JSONObject object) {
		this.tmpObject = object;
	}

	public JSONObject getTmpObject() {
		return tmpObject;
	}

	/** 显示界面上的用户列表  */
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

	/** 在界面上显示云端文件列表  */
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

	/** 打开对话框，选择文件上传到服务器  */
	public File selectFileToUpload() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("上传");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			return file;
		}
		return null;
	}

	/** 打开对话框，选择从服务器下载文件的路径  */
	public String selectPathToDownload() {
		JFileChooser savepath = new JFileChooser();
		savepath.setDialogTitle("下载");
		savepath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (savepath.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return savepath.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

}
