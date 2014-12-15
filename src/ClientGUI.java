import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// login area
	private JButton btnLogin;
	private JTextField tfIP;
	public JLabel labelLogin;
	public boolean loginState;
	// send area
	private JTextField tfDestIP;
	private JButton btnFile;
	private JButton btnSend;
	private JButton btnSendPause;
	private JButton btnSendCancel;
	public JLabel taSendInfo;
	// receive area
	public JLabel taRecvRequest;
	private JButton btnRecv;
	private JButton btnRecvDeny;
	private JButton btnRecvPause;
	private JButton btnRecvCancel;
	public JLabel taRecvInfo;

	// 共享变量
	private String localIP = "";
	private String destIP;
	private String sendFilePath = "";
	private DataInputStream dis;
	private DataOutputStream dos;

	private SendHelper sendHelper;
	private RecvHelper recvHelper;
	private SocketHelper socketHelper;
	
	public ClientGUI() {
		initComponents();
	}

	private void initComponents() {
		tfIP = new JTextField(10);
		btnLogin = new JButton("注册");
		labelLogin = new JLabel();

		tfDestIP = new JTextField(10);
		btnFile = new JButton("选择文件");
		btnSend = new JButton("发送");
		btnSendPause = new JButton("暂停");
		btnSendCancel = new JButton("取消");
		taSendInfo = new JLabel("正在发送：");

		taRecvRequest = new JLabel("请求：");
		taRecvInfo = new JLabel("正在接收：");
		btnRecv = new JButton("接收");
		btnRecvDeny = new JButton("拒绝");
		btnRecvPause = new JButton("暂停");
		btnRecvCancel = new JButton("取消");

		setTitle("fuckTCP");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 600);
		setLayout(null);
		setVisible(true);

		Container c = getContentPane();
		JPanel localArea = new JPanel();
		JPanel sendArea = new JPanel();
		JPanel recvArea = new JPanel();

		localArea.setBounds(50, 20, 900, 80);
		localArea.setBorder(BorderFactory.createTitledBorder("本机信息"));
		localArea.setLayout(null);
		localArea.add(tfIP);
		localArea.add(btnLogin);
		localArea.add(labelLogin);
		tfIP.setBounds(30, 30, 200, 30);
		btnLogin.setBounds(240, 30, 100, 30);
		labelLogin.setBounds(350, 30, 100, 30);

		sendArea.setBounds(50, 120, 900, 200);
		sendArea.setBorder(BorderFactory.createTitledBorder("发送区"));
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
		recvArea.setBorder(BorderFactory.createTitledBorder("接收区"));
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
		socketHelper = new SocketHelper(this);
		dis = socketHelper.getDIS();
		dos = socketHelper.getDOS();
		
		sendHelper = new SendHelper(this);
		recvHelper = new RecvHelper(this);
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
				initHelper();
				socketHelper.sendLogin();
				new Thread(new ClientThread(socketHelper, sendHelper, recvHelper)).start();
			}
		} else if (source == btnFile) {
			JFileChooser chooser = new JFileChooser("F:/");
			chooser.setDialogTitle("打开");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				File sendFile = chooser.getSelectedFile();
				setSendFilePath(sendFile.getAbsolutePath());
				taSendInfo.setText(sendFilePath + " " + sendFile.length() / 1024 + "kb");
			}
		} else if (source == btnSend) {
			setDestIP(tfDestIP.getText());
			if (destIP.isEmpty()) {
				JOptionPane.showMessageDialog(this, "请输入对方IP");
			} else if (sendFilePath.isEmpty()) {
				JOptionPane.showMessageDialog(this, "请选择要发送的文件");
			} else {
				sendHelper.setSendInfo(new SendInfo(localIP, destIP, sendFilePath));
				sendHelper.sendRequest();
			}
		} else if (source == btnSendPause) {

		} else if (source == btnSendCancel) {

		} else if (source == btnRecv) {
			recvHelper.sendResponse(true);
			recvHelper.createFile();
		} else if (source == btnRecvDeny) {
			recvHelper.sendResponse(false);
		} else if (source == btnRecvPause) {
			
		} else if (source == btnRecvCancel) {
			
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
	
	public DataInputStream getDIS() {
		return dis;
	}
	
	public DataOutputStream getDOS() {
		return dos;
	}
	
	public String getLocalIP() {
		return localIP;
	}
}
