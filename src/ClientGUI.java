import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientGUI extends JFrame implements ActionListener, NoteMessage {

	private static final long serialVersionUID = 1L;
	// login area
	private JButton btnLogin;
	private JTextField tfIP;
	public JLabel labelLogin;

	// send area
	private JTextField tfDestIP;
	private JButton btnFile;
	private JButton btnSend;
	private JButton btnSendPause;
	private JButton btnSendCancel;
	public static JLabel taSendInfo;
	// receive area
	public JLabel taRecvRequest;
	private JButton btnRecv;
	private JButton btnRecvDeny;
	private JButton btnRecvPause;
	private JButton btnRecvCancel;
	public static JLabel taRecvInfo;

	// public info area
	public static JTextArea printArea;

	// 临时变量，与发送接收有关的配置信息
	private String localIP = "";
	private String destIP;
	private String sendFilePath = "";
	private String recvFilePath = "F:/recv/";

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	public static SendHelper sendHelper;
	private RecvHelper recvHelper;
	public List<JSONObject> recvObjects;
	public List<JSONObject> sendObjects;
	public List<SendFileThread> sendThreads;
	public List<RecvFileThread> recvThreads;
	// 接收第一次请求还是二次请求的标志
	public static int recvState = 0;

	public ClientGUI() {
		initComponents();
		initSocket();
		initHelper();
	}

	private void initComponents() {
		tfIP = new JTextField(10);
		btnLogin = new JButton("Login");
		labelLogin = new JLabel();

		tfDestIP = new JTextField(10);
		btnFile = new JButton("Open");
		btnSend = new JButton("Send");
		btnSendPause = new JButton("Pause");
		btnSendCancel = new JButton("继续发送");
		taSendInfo = new JLabel("正在发送：");

		taRecvRequest = new JLabel("收到文件请求：");
		taRecvInfo = new JLabel("正在接收：");
		btnRecv = new JButton("Receive");
		btnRecvDeny = new JButton("Refuse");
		btnRecvPause = new JButton("Pause");
		btnRecvCancel = new JButton("继续接收");

		setTitle("fuckTCP");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 600);
		setLayout(null);
		setVisible(true);

		Container c = getContentPane();
		JPanel localArea = new JPanel();
		JPanel sendArea = new JPanel();
		JPanel recvArea = new JPanel();

		localArea.setBounds(50, 20, 900, 80);
		localArea.setBorder(BorderFactory.createTitledBorder("Local Area"));
		localArea.setLayout(null);
		localArea.add(tfIP);
		localArea.add(btnLogin);
		localArea.add(labelLogin);
		tfIP.setBounds(30, 30, 200, 30);
		btnLogin.setBounds(240, 30, 100, 30);
		labelLogin.setBounds(350, 30, 100, 30);

		sendArea.setBounds(50, 120, 900, 200);
		sendArea.setBorder(BorderFactory.createTitledBorder("Send Area"));
		sendArea.setLayout(null);
		sendArea.add(tfDestIP);
		sendArea.add(btnSend);
		sendArea.add(btnFile);
		sendArea.add(btnSendPause);
		sendArea.add(btnSendCancel);
		sendArea.add(taSendInfo);
		tfDestIP.setBounds(30, 30, 200, 30);
		btnFile.setBounds(240, 30, 100, 30);
		btnSend.setBounds(360, 30, 100, 30);
		taSendInfo.setBounds(30, 80, 500, 30);
		btnSendPause.setBounds(600, 80, 100, 30);
		btnSendCancel.setBounds(720, 80, 100, 30);

		recvArea.setBounds(50, 340, 900, 200);
		recvArea.setBorder(BorderFactory.createTitledBorder("Receive Area"));
		recvArea.setLayout(null);
		recvArea.add(taRecvRequest);
		recvArea.add(taRecvInfo);
		recvArea.add(btnRecv);
		recvArea.add(btnRecvDeny);
		recvArea.add(btnRecvCancel);
		recvArea.add(btnRecvPause);
		taRecvRequest.setBounds(30, 30, 500, 30);
		btnRecv.setBounds(600, 30, 100, 30);
		btnRecvDeny.setBounds(720, 30, 100, 30);
		taRecvInfo.setBounds(30, 80, 500, 30);
		btnRecvPause.setBounds(600, 80, 100, 30);
		btnRecvCancel.setBounds(720, 80, 100, 30);

		c.add(localArea);
		c.add(sendArea);
		c.add(recvArea);

		printArea = new JTextArea();
		printArea.setBounds(950, 20, 180, 500);
		printArea.setBackground(Color.gray);
		c.add(printArea);

		btnLogin.addActionListener(this);
		btnFile.addActionListener(this);
		btnSend.addActionListener(this);
		btnSendPause.addActionListener(this);
		btnSendCancel.addActionListener(this);
		btnRecv.addActionListener(this);
		btnRecvDeny.addActionListener(this);
		btnRecvPause.addActionListener(this);
		btnRecvCancel.addActionListener(this);
	}

	public static void main(String[] args) {
		new ClientGUI();
	}

	private void initHelper() {
		sendHelper = new SendHelper(dos);
		recvHelper = new RecvHelper(this);

		recvObjects = new ArrayList<JSONObject>();
		sendObjects = new ArrayList<JSONObject>();
		sendThreads = new ArrayList<SendFileThread>();
		recvThreads = new ArrayList<RecvFileThread>();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source == btnLogin) {
			setLocalIP(tfIP.getText());
			if (localIP.isEmpty()) {
				JOptionPane.showMessageDialog(this, "请输入本机IP");
			} else {
				sendHelper.sendLogin(localIP);
				new Thread(new NoteThread(dis, this)).start();
			}
		} else if (source == btnFile) {
			JFileChooser chooser = new JFileChooser("F:/");
			chooser.setDialogTitle("打开");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File sendFile = chooser.getSelectedFile();
				setSendFilePath(sendFile.getAbsolutePath());
				taSendInfo.setText(sendFilePath + " " + sendFile.length()
						/ 1024 + "kb");
			}
		} else if (source == btnSend) {
			setDestIP(tfDestIP.getText());
			if (destIP.isEmpty()) {
				JOptionPane.showMessageDialog(this, "请输入对方IP");
			} else if (sendFilePath.isEmpty()) {
				JOptionPane.showMessageDialog(this, "请选择要发送的文件");
			} else {
				sendHelper.sendRequest(addSendConfigInfo());
			}
		} else if (source == btnSendPause) {
			sendThreads.get(0).interupt();
		} else if (source == btnSendCancel) {
			// 暂时当做继续传送的按钮
			// new Thread(sendThreads.get(0)).start();
			sendHelper.sendReqSend2(sendObjects.get(0));
		} else if (source == btnRecv) {
			// 获取到当前文件选项对应的配置信息
			
			if (recvState == 1) {
				//请求继续发送
				JSONObject object = recvObjects.get(0);
				sendHelper.sendRespSend2(object, true);
			}else if (recvState == 2) {
				//请求继续接收
				JSONObject object = sendObjects.get(0);
				sendHelper.sendRespRecv2(object, true);
			} else {
				//第一次请求发送
				JSONObject object = recvObjects.get(0);
				object = setRecvConfigInfo(object);
				recvObjects.set(0, object);
				sendHelper.sendResponse(object, true);
			}

		} else if (source == btnRecvDeny) {
			sendHelper.sendResponse(recvObjects.get(0), false);
		} else if (source == btnRecvPause) {
			sendHelper.sendInterupt(recvObjects.get(0));
		} else if (source == btnRecvCancel) {
			// 暂时当做请求继续接收的按钮
			sendHelper.sendReqRecv2(recvObjects.get(0));
		}
	}

	private void setLocalIP(String ip) {
		localIP = ip;
	}

	private void setSendFilePath(String path) {
		sendFilePath = path;
	}

	private void setDestIP(String ip) {
		destIP = ip;
	}

	public void setRecvFilePath(String name) {
		recvFilePath += name;
	}

	public String getLocalIP() {
		return localIP;
	}

	public String getSendFilePath() {
		return sendFilePath;
	}

	private void initSocket() {
		try {
			socket = new Socket(Constants.HOST_NAME, Constants.PORT_NUM);
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.println("socket连接成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("socket连接失败");
		}
	}

	private JSONObject addSendConfigInfo() {
		JSONObject sendObject = new JSONObject();
		try {
			sendObject.put(Constants.SRC_IP, localIP);
			sendObject.put(Constants.DEST_IP, destIP);
			sendObject.put(Constants.SEND_PATH, sendFilePath);

			File file = new File(sendFilePath);
			sendObject.put(Constants.FILE_NAME, file.getName());
			sendObject.put(Constants.FILE_LEN, file.length());

			sendObjects.add(sendObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sendObject;
	}

	private JSONObject setRecvConfigInfo(JSONObject object) {
		try {
			object.put(Constants.RECV_PATH, recvFilePath);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}

	@Override
	public void handleMessage(String msg) {
		// TODO Auto-generated method stub
		recvHelper.handleMessage(msg);
	}

}
