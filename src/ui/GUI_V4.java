package ui;

import helper.Constants;
import helper.DataHelper;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import main.RecvFileTask;
import main.SendFileTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GUI_V4 extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private boolean weathertologin = false;

	// login frame
	public JFrame loginFrame;
	private JPanel loginPanel;

	private ImageIcon miniloginFrameIcon;
	private ImageIcon closeloginFrameIcon;
	private JButton closeloginFrameBtn;
	private JButton miniloginFrameBtn;

	private JLabel useripLbl;
	private JTextField usernameField;

	private JButton registerBtn;
	private JButton loginBtn;
	private ImageIcon loginIcon;
	private ImageIcon loginPushIcon;
	private ImageIcon registerIcon;
	private ImageIcon registerPushIcon;

	private boolean loginisMoved;
	private Point login_pre_point;
	private Point login_end_point;

	public JLabel registerokLbl;

	// main frame
	public JFrame mainFrame;
	private JButton closeFrameBtn;
	private ImageIcon closeFrameIcon;;
	private JButton miniFrameBtn;
	private ImageIcon miniFrameIcon;

	private JButton reloginBtn;
	private ImageIcon reloginIcon;
	public static boolean loginState = true;
	
	// up panel
	private JPanel upPanel;
	// 用于拖动窗口
	private boolean isMoved;
	private Point pre_point;
	private Point end_point;

	// logo
	private JLabel logoLbl;
	private ImageIcon logoIcon;

	// host information
	private InetAddress hostinfo;
	private JLabel hostipLbl;
	private JLabel hostnameLbl;

	// main change button
	private JButton sendfileAreaBtn;
	private ImageIcon sendfileAreaIcon;
	private ImageIcon sendfileAreaPushIcon;
	private JButton translistAreaBtn;
	private ImageIcon translistAreaIcon;
	private ImageIcon translistAreaPushIcon;

	// down panel
	private JPanel downPanel;
	private CardLayout downCard;

	// sendfile panel
	private JPanel sendfilePanel;
	
	private JLabel userlistLbl;
	private JPanel userlistPanel;
	private JList<String> userList;
	private JScrollPane userScrollPane; // 文件信息拖动条
	public DefaultListModel<String> userMode; // 文件信息元素
	
	private JPanel serverfiletitlePanel;
	private JLabel serverfiletitleLbl;
	private JButton uploadBtn; 
	private ImageIcon uploadIcon;
	private ImageIcon uploadPushIcon;
	private JButton downloadBtn;
	private ImageIcon downloadIcon;
	private ImageIcon downloadPushIcon;
	
	private JPanel serverfilePanel;
	private JList<String> serverfileList;
	private JScrollPane serverfileScrollPane;
	public DefaultListModel<String> serverfileMode;
	
	private JPanel whitelinePanel; // 美化用白线

	// 指示界面信息
	public JLabel recentfileLbl;
	public JLabel ipinfoLbl;

	// 右下方的Panel用于实现添加发送文件的功能
	private JPanel sendfileFuncPanel;
	private JButton sendBtn;
	private ImageIcon sendIcon;
	private ImageIcon sendPushIcon;
	private JButton sendAddBtn;
	private ImageIcon sendAddIcon;
	private ImageIcon sendAddPushIcon;

	// translist panel
	private JPanel translistPanel;

	private JPanel filestatePanel;
	private JButton sendingAreaBtn;
	private ImageIcon sendingAreaIcon;
	private ImageIcon sendingAreaPushIcon;
	private JButton recvingAreaBtn;
	private ImageIcon recvingAreaIcon;
	private ImageIcon recvingAreaPushIcon;
	private JButton doneAreaBtn;
	private ImageIcon doneAreaIcon;
	private ImageIcon doneAreaPushIcon;

	private JPanel PauseandCancelPanel;
	private CardLayout PauseandCancelCard;
	private JPanel sendPauseandCancelPanel;
	private JPanel recvPauseandCancelPanel;
	private JPanel donePauseandCancelPanel;
	private ImageIcon pauseIcon;
	private ImageIcon pauseNotuseIcon;
	public static JButton sendPauseBtn;
	public static JButton recvPauseBtn;
	private ImageIcon continueIcon;
	private ImageIcon continueNotuseIcon;
	public static JButton sendContinueBtn;
	public static JButton recvContinueBtn;
	private ImageIcon cancelIcon;
	private ImageIcon cancelPushIcon;
	private JButton doneCancelBtn;

	// send file table
	private JPanel fileinfoPanel;
	private CardLayout fileinfoCard;

	private JPanel sendfileinfoPanel;
	private JPanel recvfileinfoPanel;
	private JPanel donefileinfoPanel;

	private FileTable sendfileTable;
	private FileTable recvfileTable;
	private FileTable donefileTable;
	private JScrollPane sendfileScrollPane; // 文件信息拖动条
	private JScrollPane recvfileScrollPane;
	private JScrollPane donefileScrollPane;

	public static BaseTableModel sendTableModel;
	public static BaseTableModel recvTableModel;
	public static BaseTableModel doneTableModel;

	private DataHelper dataHelper;

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					new GUI_V4(); // 实例化一个GUI_V4对象，执行该对象构造函数
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

	}

	public GUI_V4() throws UnknownHostException {

		hostinfo = InetAddress.getLocalHost();

		initHelper();
		initloginFrame();

		initLogoLabel();
		initLocalinfoLabel();
		initmainchangeButton();
		initupPanel();
		initsendfilePanel();
		initfileinfoPanel();
		inittranslistPanel();
		initdownPanel();
		initMainFrame();
	}

	private void initloginFrame() {

		loginFrame = new JFrame();
		loginFrame.setLayout(null);
		loginFrame.setUndecorated(true);
		loginFrame.setBounds(400, 220, 450, 282);

		loginPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// TODO 自动生成的方法存根
				super.paintComponent(g);
				ImageIcon icon = new ImageIcon("back.jpg");
				g.drawImage(icon.getImage(), 0, 0, 450, 282, null);
			}

		};

		miniloginFrameIcon = new ImageIcon("miniloginframe.png");
		miniloginFrameBtn = new JButton(miniloginFrameIcon);
		miniloginFrameBtn.setBounds(395, 5, 20, 20);
		miniloginFrameBtn.setContentAreaFilled(false);

		closeloginFrameIcon = new ImageIcon("closeloginframe.png");
		closeloginFrameBtn = new JButton(closeloginFrameIcon);
		closeloginFrameBtn.setBounds(420, 5, 20, 20);
		closeloginFrameBtn.setContentAreaFilled(false);

		// 关闭程序
		closeloginFrameBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		//
		// 最小化程序
		miniloginFrameBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				loginFrame.setExtendedState(JFrame.ICONIFIED);
			}
		});

		// 拖动窗口
		loginPanel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				loginisMoved = false;// 鼠标释放了以后，是不能再拖拽的了
				loginFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			public void mousePressed(MouseEvent e) {
				loginisMoved = true;
				login_pre_point = new Point(e.getX(), e.getY());// 得到按下去的位置
				loginFrame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
		});
		loginPanel
				.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
					public void mouseDragged(java.awt.event.MouseEvent e) {
						if (loginisMoved) {// 判断是否可以拖拽
							login_end_point = new Point(loginFrame
									.getLocation().x
									+ e.getX()
									- login_pre_point.x, loginFrame
									.getLocation().y
									+ e.getY()
									- login_pre_point.y);
							loginFrame.setLocation(login_end_point);
						}
					}
				});

		Font font = new Font("楷体", Font.BOLD, 17);

		useripLbl = new JLabel(hostinfo.getHostAddress());
		useripLbl.setBounds(217, 112, 175, 28);
		useripLbl.setFont(font);
		useripLbl.setForeground(Color.black);

		usernameField = new JTextField();
		usernameField.setOpaque(false);
		usernameField.setBounds(213, 158, 165, 28);
		usernameField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 0));
		usernameField.setFont(font);
		usernameField.setForeground(Color.black);

		registerIcon = new ImageIcon("register.png");
		registerPushIcon = new ImageIcon("registerpush.png");
		registerBtn = new JButton(registerIcon);
		registerBtn.setPressedIcon(registerPushIcon);
		registerBtn.setBounds(90, 216, 120, 31);
		registerBtn.setContentAreaFilled(false);

		loginIcon = new ImageIcon("login.png");
		loginPushIcon = new ImageIcon("loginpush.png");
		loginBtn = new JButton(loginIcon);
		loginBtn.setPressedIcon(loginPushIcon);
		loginBtn.setBounds(242, 216, 120, 31);
		loginBtn.setContentAreaFilled(false);

		registerokLbl = new JLabel();
		registerokLbl.setBounds(209, 188, 80, 20);
		loginPanel.add(registerokLbl);

		loginPanel.setLayout(null);
		loginPanel.setBounds(0, 0, 450, 282);
		loginPanel.add(miniloginFrameBtn);
		loginPanel.add(closeloginFrameBtn);
		loginPanel.add(useripLbl);
		loginPanel.add(usernameField);

		loginPanel.add(registerBtn);
		loginPanel.add(loginBtn);

		loginFrame.add(loginPanel);
		loginFrame.setVisible(true);

		dataHelper.openNoteThread();
		
		registerBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String localIP = hostinfo.getHostAddress();
				String userName = usernameField.getText();
				
				System.out.println(" regigest pushed! ");
				
				dataHelper.setLocalIP(localIP);
				hostnameLbl.setText("用户名：" + userName);

				dataHelper.getSendHelper().sendLogin(localIP);
				weathertologin = true;

			}
		});

		loginBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String localIP = hostinfo.getHostAddress();
				String userName = usernameField.getText();
				
				System.out.println(" login pushed! ");
				
				dataHelper.setDengluLable(weathertologin);
				hostnameLbl.setText("用户名：" + userName);
			}
		});

	}

	// 主体框架
	private void initMainFrame() {

		mainFrame = new JFrame("GUI_V4");
		mainFrame.setSize(720, 515);
		mainFrame.setLocation(300, 100);
		mainFrame.setLayout(null);
		mainFrame.setResizable(false);

		mainFrame.add(upPanel);
		mainFrame.add(downPanel);
		mainFrame.setUndecorated(true);
		mainFrame.setVisible(false);
	}

	// 顶部的Panel包含logo、本机信息及两个Button
	private void initupPanel() {

		upPanel = new JPanel();
		upPanel.setBackground(new Color(62, 126, 214));
		upPanel.setBounds(0, 0, 720, 95);
		upPanel.setLayout(null);

		closeFrameIcon = new ImageIcon("closeframe.jpg");
		closeFrameBtn = new JButton(closeFrameIcon);
		closeFrameBtn.setBounds(690, 5, 20, 20);
		miniFrameIcon = new ImageIcon("miniframe.jpg");
		miniFrameBtn = new JButton(miniFrameIcon);
		miniFrameBtn.setBounds(665, 5, 20, 20);
		
		reloginIcon = new ImageIcon("relogin.png");
		reloginBtn = new JButton(reloginIcon);
		reloginBtn.setBounds(640, 5, 20, 20); 
		reloginBtn.setContentAreaFilled(false);

		reloginBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(!loginState) {
					dataHelper.initSocket();
				}
			}
		});

		// 关闭程序
		closeFrameBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int option = JOptionPane.showConfirmDialog(null, "确定退出程序？", "",
						JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) {
					List<SwingWorker<Long, Long>> taskList = recvTableModel
							.getTaskList();
					for (SwingWorker<Long, Long> swingWorker : taskList) {
						RecvFileTask task = (RecvFileTask) swingWorker;
						if (task != null) {
							task.interupt();
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					System.exit(0);
				}
			}
		});

		// 最小化程序
		miniFrameBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				mainFrame.setExtendedState(JFrame.ICONIFIED);
			}
		});

		// 拖动窗口
		upPanel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				isMoved = false;// 鼠标释放了以后，是不能再拖拽的了
				mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			public void mousePressed(MouseEvent e) {
				isMoved = true;
				pre_point = new Point(e.getX(), e.getY());// 得到按下去的位置
				mainFrame.setCursor(new Cursor(Cursor.MOVE_CURSOR));
			}
		});
		upPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged(java.awt.event.MouseEvent e) {
				if (isMoved) {// 判断是否可以拖拽
					end_point = new Point(mainFrame.getLocation().x + e.getX()
							- pre_point.x, mainFrame.getLocation().y + e.getY()
							- pre_point.y);
					mainFrame.setLocation(end_point);
				}
			}
		});

		upPanel.add(logoLbl);
		upPanel.add(hostipLbl);
		upPanel.add(hostnameLbl);
		upPanel.add(sendfileAreaBtn);
		upPanel.add(translistAreaBtn);
		upPanel.add(closeFrameBtn);
		upPanel.add(miniFrameBtn);
		upPanel.add(reloginBtn);

	}

	// 初始化logo
	private void initLogoLabel() {

		logoLbl = new JLabel();
		logoIcon = new ImageIcon("logo.jpg");
		logoLbl.setIcon(logoIcon);
		logoLbl.setBounds(5, 5, 90, 90);

	}

	// 初始化本机信息
	private void initLocalinfoLabel() {

		Font font = new Font("楷体", Font.BOLD, 14);

		hostipLbl = new JLabel("主机IP：" + hostinfo.getHostAddress());
		hostipLbl.setForeground(new Color(253, 253, 253));
		hostipLbl.setFont(font);
		hostipLbl.setBounds(97, 28, 200, 35);

		hostnameLbl = new JLabel();
		hostnameLbl.setForeground(new Color(253, 253, 253));
		hostnameLbl.setFont(font);
		hostnameLbl.setBounds(97, 53, 200, 35);

	}

	// 上方两个主控按钮
	public void initmainchangeButton() {

		sendfileAreaIcon = new ImageIcon("sendfileBtn.jpg");
		sendfileAreaPushIcon = new ImageIcon("sendfilepushBtn.jpg");
		translistAreaIcon = new ImageIcon("translistBtn.jpg");
		translistAreaPushIcon = new ImageIcon("translistpushBtn.jpg");

		sendfileAreaBtn = new JButton(sendfileAreaIcon);
		translistAreaBtn = new JButton(translistAreaIcon);

		sendfileAreaBtn.setBounds(390, 35, 140, 40);
		translistAreaBtn.setBounds(550, 35, 140, 40);

	}

	// 下方主体Panel包含两个子Panel
	public void initdownPanel() {

		downPanel = new JPanel();
		downCard = new CardLayout();

		downPanel.setLayout(downCard);
		downPanel.setBounds(0, 95, 720, 420);

		downPanel.add(sendfilePanel, "sendfilePanel");
		downPanel.add(translistPanel, "translistPanel");

		sendfileAreaBtn.setPressedIcon(sendfileAreaPushIcon);
		translistAreaBtn.setPressedIcon(translistAreaPushIcon);

		sendfileAreaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downCard.show(downPanel, "sendfilePanel");
			}
		});
		translistAreaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downCard.show(downPanel, "translistPanel");
			}
		});

	}

	// 发送文件的Panel
	public void initsendfilePanel() {

		sendfilePanel = new JPanel();
		sendfilePanel.setLayout(null);

		userlistLbl = new JLabel();
		userlistLbl.setText("       用 户 列 表");
		userlistLbl.setOpaque(true);
		userlistLbl.setBackground(new Color(190, 210, 253));
		userlistLbl.setBounds(0, 0, 220, 30);
		userlistLbl.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		
		userlistPanel = new JPanel();
		userlistPanel.setLayout(new BorderLayout());
		userlistPanel.setBackground(new Color(228, 237, 254));
		userlistPanel.setBounds(0, 30, 220, 150);
		userList = new JList<String>();
		userList.setFont(new Font("楷体", Font.PLAIN, 16));
		userList.setFixedCellHeight(35);
		userList.setForeground(Color.black);
		userList.setBackground(new Color(228, 237, 254));
		userMode = new DefaultListModel<String>();
		userScrollPane = new JScrollPane(userList);
		userScrollPane.setBorder(BorderFactory.createLineBorder(new Color(228,
				237, 254)));
		userList.setModel(userMode);
		userlistPanel.add(userScrollPane, BorderLayout.CENTER);
		

		serverfiletitleLbl = new JLabel();
		serverfiletitleLbl.setText("      云 端 文 件");
		serverfiletitleLbl.setOpaque(true);
		serverfiletitleLbl.setBackground(new Color(190, 210, 253));
		serverfiletitleLbl.setBounds(1, 0, 120, 30);
		serverfiletitleLbl.setFont(new Font("微软雅黑", Font.PLAIN, 14));
		
		uploadIcon = new ImageIcon("upload.png");
		uploadPushIcon = new ImageIcon("uploadpush.png");
		uploadBtn = new JButton(uploadIcon);
		uploadBtn.setPressedIcon(uploadPushIcon);
		uploadBtn.setBounds(150, 2, 25, 25);
		uploadBtn.setContentAreaFilled(false); 

		downloadIcon = new ImageIcon("download.png");
		downloadPushIcon = new ImageIcon("downloadpush.png");
		downloadBtn = new JButton(downloadIcon);
		downloadBtn.setPressedIcon(downloadPushIcon);
		downloadBtn.setBounds(185, 2, 25, 25);
		downloadBtn.setContentAreaFilled(false); 
		
		serverfiletitlePanel = new JPanel();
		serverfiletitlePanel.setLayout(null);
		serverfiletitlePanel.setBackground(new Color(190, 210, 253));
		serverfiletitlePanel.setBounds(0, 180, 220, 30);
		serverfiletitlePanel.add(serverfiletitleLbl);
		serverfiletitlePanel.add(uploadBtn);
		serverfiletitlePanel.add(downloadBtn);
		
		serverfilePanel = new JPanel();
		serverfilePanel.setLayout(new BorderLayout());
		serverfilePanel.setBackground(new Color(228, 237, 254));
		serverfilePanel.setBounds(0, 210, 220, 210);
		serverfileList = new JList<String>();
		serverfileList.setFont(new Font("楷体", Font.PLAIN, 16));
		serverfileList.setFixedCellHeight(35);
		serverfileList.setForeground(Color.black);
		serverfileList.setBackground(new Color(228, 237, 254));
		serverfileMode = new DefaultListModel<String>();
		serverfileScrollPane = new JScrollPane(serverfileList);
		serverfileScrollPane.setBorder(BorderFactory.createLineBorder(new Color(228,
				237, 254)));
		serverfileList.setModel(serverfileMode);
		serverfilePanel.add(serverfileScrollPane, BorderLayout.CENTER);
		
		
		
		
		userList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				super.mouseClicked(arg0);
				String destIP = userList.getSelectedValue().substring(9);
				dataHelper.setDestIP(destIP);
				ipinfoLbl.setText("<html>" + " " + "<br>" + "目的IP：" + destIP + "<html>");
			}
			
		});
		
		uploadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				File file = dataHelper.selectFileToUpload();
				if(file != null) {
					dataHelper.getSendHelper().sendUpload(file);
				}
			}
		});
		
		downloadBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String fileName = serverfileList.getSelectedValue();
				if(fileName != null && !fileName.isEmpty()) {
					fileName = fileName.substring(2);
					String path = dataHelper.selectPathToDownload();
					if(path != null) {
						dataHelper.getSendHelper().sendDownload(fileName, path);
					}
					
				}
			}
		});
		
		whitelinePanel = new JPanel();
		whitelinePanel.setBackground(Color.white);
		whitelinePanel.setBounds(220, 0, 5, 420);

		sendfileFuncPanel = new JPanel();
		sendfileFuncPanel.setLayout(null);
		sendfileFuncPanel.setBackground(new Color(228, 237, 254));
		sendfileFuncPanel.setBounds(225, 0, 495, 420);

		sendAddIcon = new ImageIcon("addfile.jpg");
		sendAddPushIcon = new ImageIcon("addfilepush.jpg");
		sendAddBtn = new JButton(sendAddIcon);
		sendAddBtn.setBounds(20, 60, 140, 140);

		sendIcon = new ImageIcon("sendfile.jpg");
		sendPushIcon = new ImageIcon("sendfilepush.jpg");
		sendBtn = new JButton(sendIcon);
		sendBtn.setBounds(20, 230, 140, 140);

		// 为sendAddBtn添加监听事件
		Font recentfont = new Font("楷体", Font.BOLD, 17); // 设置当前文件信息Label的字体

		recentfileLbl = new JLabel();
		recentfileLbl.setBounds(170, 50, 320, 140);
		recentfileLbl.setFont(recentfont);

		// 茂兄此处内容自行解决
		ipinfoLbl = new JLabel();
		ipinfoLbl.setBounds(170, 220, 320, 140);
		ipinfoLbl.setFont(recentfont);
		ipinfoLbl.setText("<html>" + " " + "<br>" + "目的IP：" + "<html>");

		sendAddBtn.addActionListener(this);
		// 按钮按下的效果
		sendAddBtn.setPressedIcon(sendAddPushIcon);

		// 点击发送当前文件信息转移至正在发送列表中
		sendBtn.addActionListener(this);
		// 按钮按下的效果
		sendBtn.setPressedIcon(sendPushIcon);


		// sendfileFuncPanel加入两个按钮及当前文件信息
		sendfileFuncPanel.add(sendAddBtn);
		sendfileFuncPanel.add(sendBtn);
		sendfileFuncPanel.add(recentfileLbl);
		sendfileFuncPanel.add(ipinfoLbl);

		sendfilePanel.add(serverfiletitlePanel);
		sendfilePanel.add(serverfilePanel);
		sendfilePanel.add(userlistLbl);
		sendfilePanel.add(userlistPanel);
		sendfilePanel.add(whitelinePanel);
		sendfilePanel.add(sendfileFuncPanel);
		
	}

	public void inittranslistPanel() {

		translistPanel = new JPanel();
		translistPanel.setLayout(null);

		filestatePanel = new JPanel();
		filestatePanel.setLayout(new GridLayout(1, 3));
		filestatePanel.setBounds(0, 0, 540, 60);
		filestatePanel.setBackground(new Color(190, 210, 253));

		sendingAreaIcon = new ImageIcon("sending.jpg");
		sendingAreaPushIcon = new ImageIcon("sendingpush.jpg");
		sendingAreaBtn = new JButton(sendingAreaPushIcon);
		recvingAreaIcon = new ImageIcon("recving.jpg");
		recvingAreaPushIcon = new ImageIcon("recvingpush.jpg");
		recvingAreaBtn = new JButton(recvingAreaIcon);
		doneAreaIcon = new ImageIcon("done.jpg");
		doneAreaPushIcon = new ImageIcon("donepush.jpg");
		doneAreaBtn = new JButton(doneAreaIcon);

		filestatePanel.add(sendingAreaBtn);
		filestatePanel.add(recvingAreaBtn);
		filestatePanel.add(doneAreaBtn);

		PauseandCancelPanel = new JPanel();
		PauseandCancelCard = new CardLayout();
		PauseandCancelPanel.setLayout(PauseandCancelCard);
		PauseandCancelPanel.setBounds(540, 0, 180, 60);

		pauseIcon = new ImageIcon("pause.jpg");
		pauseNotuseIcon = new ImageIcon("pausenotuse.jpg");
		continueIcon = new ImageIcon("continue.jpg");
		continueNotuseIcon = new ImageIcon("continuenotuse.jpg");
		cancelIcon = new ImageIcon("cancel.jpg");
		cancelPushIcon = new ImageIcon("cancelpush.jpg");

		sendPauseandCancelPanel = new JPanel();
		sendPauseandCancelPanel.setLayout(null);
		sendPauseandCancelPanel.setBackground(new Color(190, 210, 253));
		sendPauseBtn = new JButton(pauseNotuseIcon);
		sendPauseBtn.setBounds(85, 20, 30, 30);
		sendContinueBtn = new JButton(continueNotuseIcon);
		sendContinueBtn.setBounds(125, 20, 30, 30);
		sendPauseandCancelPanel.add(sendPauseBtn);
		sendPauseandCancelPanel.add(sendContinueBtn);

		recvPauseandCancelPanel = new JPanel();
		recvPauseandCancelPanel.setLayout(null);
		recvPauseandCancelPanel.setBackground(new Color(190, 210, 253));
		recvPauseBtn = new JButton(pauseNotuseIcon);
		recvPauseBtn.setBounds(85, 20, 30, 30);
		recvContinueBtn = new JButton(continueNotuseIcon);
		recvContinueBtn.setBounds(125, 20, 30, 30);
		recvPauseandCancelPanel.add(recvPauseBtn);
		recvPauseandCancelPanel.add(recvContinueBtn);

		donePauseandCancelPanel = new JPanel();
		donePauseandCancelPanel.setLayout(null);
		donePauseandCancelPanel.setBackground(new Color(190, 210, 253));
		doneCancelBtn = new JButton(cancelIcon);
		doneCancelBtn.setPressedIcon(cancelPushIcon);
		doneCancelBtn.setBounds(125, 20, 30, 30);
		donePauseandCancelPanel.add(doneCancelBtn);

		PauseandCancelPanel.add(sendPauseandCancelPanel,
				"sendPauseandCancelPanel");
		PauseandCancelPanel.add(recvPauseandCancelPanel,
				"recvPauseandCancelPanel");
		PauseandCancelPanel.add(donePauseandCancelPanel,
				"donePauseandCancelPanel");

		sendingAreaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PauseandCancelCard.show(PauseandCancelPanel,
						"sendPauseandCancelPanel");
				fileinfoCard.show(fileinfoPanel, "sendfileinfoPanel");
				sendingAreaBtn.setIcon(sendingAreaPushIcon);
				recvingAreaBtn.setIcon(recvingAreaIcon);
				doneAreaBtn.setIcon(doneAreaIcon);
			}
		});
		recvingAreaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PauseandCancelCard.show(PauseandCancelPanel,
						"recvPauseandCancelPanel");
				fileinfoCard.show(fileinfoPanel, "recvfileinfoPanel");
				sendingAreaBtn.setIcon(sendingAreaIcon);
				recvingAreaBtn.setIcon(recvingAreaPushIcon);
				doneAreaBtn.setIcon(doneAreaIcon);
			}
		});
		doneAreaBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PauseandCancelCard.show(PauseandCancelPanel,
						"donePauseandCancelPanel");
				fileinfoCard.show(fileinfoPanel, "donefileinfoPanel");
				sendingAreaBtn.setIcon(sendingAreaIcon);
				recvingAreaBtn.setIcon(recvingAreaIcon);
				doneAreaBtn.setIcon(doneAreaPushIcon);
			}
		});

		sendfileTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);
				int[] rows = sendTableModel.getSelectedRows();
				int size = rows.length;
				if (size == 0) {
					sendPauseBtn.setIcon(pauseNotuseIcon);
					sendPauseBtn.setName("pauseNotuse");
					sendContinueBtn.setIcon(continueNotuseIcon);
					sendContinueBtn.setName("continueNotuse");
				} else if (size > 1) {
					sendPauseBtn.setIcon(pauseIcon);
					sendPauseBtn.setName("pause");
					sendContinueBtn.setIcon(continueIcon);
					sendContinueBtn.setName("continue");
				} else if (null == sendTableModel.getTaskList().get(rows[0])) {
					sendPauseBtn.setIcon(pauseNotuseIcon);
					sendPauseBtn.setName("pauseNotuse");
					sendContinueBtn.setIcon(continueIcon);
					sendContinueBtn.setName("continue");
				} else {
					sendPauseBtn.setIcon(pauseIcon);
					sendPauseBtn.setName("pause");
					sendContinueBtn.setIcon(continueNotuseIcon);
					sendContinueBtn.setName("continueNotuse");
				}
			}

		});

		recvfileTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseClicked(e);

				int[] rows = recvTableModel.getSelectedRows();
				int size = rows.length;
				if (size == 0) {
					recvPauseBtn.setIcon(pauseNotuseIcon);
					recvPauseBtn.setName("pauseNotuse");
					recvContinueBtn.setIcon(continueNotuseIcon);
					recvContinueBtn.setName("continueNotuse");
				} else if (size > 1) {
					recvPauseBtn.setIcon(pauseIcon);
					recvPauseBtn.setName("pause");
					recvContinueBtn.setIcon(continueIcon);
					recvContinueBtn.setName("continue");
				} else if (null == recvTableModel.getTaskList().get(rows[0])) {
					recvPauseBtn.setIcon(pauseNotuseIcon);
					recvPauseBtn.setName("pauseNotuse");
					recvContinueBtn.setIcon(continueIcon);
					recvContinueBtn.setName("continue");
				} else {
					recvPauseBtn.setIcon(pauseIcon);
					recvPauseBtn.setName("pause");
					recvContinueBtn.setIcon(continueNotuseIcon);
					recvContinueBtn.setName("continueNotuse");
				}
			}
		});

		sendPauseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (sendPauseBtn.getName().equals("pause")) {
					sendPauseBtn.setIcon(pauseNotuseIcon);
					sendPauseBtn.setName("pauseNotuse");
					sendContinueBtn.setIcon(continueIcon);
					sendContinueBtn.setName("continue");

					int[] rows = sendTableModel.getSelectedRows();
					for (int row : rows) {
						SendFileTask task = (SendFileTask) sendTableModel
								.getTaskList().get(row);
						if (task != null) {
							task.interupt();
						} else {
							System.out.println("SendFileThread null");
						}
					}

				}
			}
		});

		recvPauseBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (recvPauseBtn.getName().equals("pause")) {
					int[] rows = recvTableModel.getSelectedRows();
					for (int row : rows) {
						RecvFileTask task = (RecvFileTask) recvTableModel
								.getTaskList().get(row);
						if (task != null) {
							task.interupt();
						}
					}

					recvPauseBtn.setIcon(pauseNotuseIcon);
					recvPauseBtn.setName("pauseNotuse");
					recvContinueBtn.setIcon(continueIcon);
					recvContinueBtn.setName("continue");
				}
			}
		});

		sendContinueBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (sendContinueBtn.getName().equals("continue")) {
					int[] rows = sendTableModel.getSelectedRows();
					for (int row : rows) {
						if (sendTableModel.getTaskList().get(row) == null) {
							JSONObject object = sendTableModel.getJSONList()
									.get(row);
							DataHelper.sendHelper.sendReqSend2(object);
						}
					}

					sendContinueBtn.setIcon(continueNotuseIcon);
					sendContinueBtn.setName("continueNotuse");
					sendPauseBtn.setIcon(pauseIcon);
					sendPauseBtn.setName("pause");
				}
			}
		});

		recvContinueBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (recvContinueBtn.getName().equals("continue")) {
					int[] rows = recvTableModel.getSelectedRows();
					for (int row : rows) {
						if (recvTableModel.getTaskList().get(row) == null) {
							JSONObject object = recvTableModel.getJSONList()
									.get(row);
							DataHelper.sendHelper.sendReqRecv2(object);
						}
					}

					recvContinueBtn.setIcon(continueNotuseIcon);
					recvContinueBtn.setName("continueNotuse");
					recvPauseBtn.setIcon(pauseIcon);
					recvPauseBtn.setName("pause");
				}
			}
		});

		// 实际应该为doneCancelBtn
		doneCancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int[] rows = doneTableModel.getSelectedRows();
				JSONArray array = doneTableModel.getSelectedObjects(rows);
				doneTableModel.deleteRows(rows);
				dataHelper.getSendHelper().sendDelete(array);
			}
		});

		translistPanel.add(PauseandCancelPanel);
		translistPanel.add(filestatePanel);
		translistPanel.add(fileinfoPanel);

	}

	public void initfileinfoPanel() {

		fileinfoPanel = new JPanel();
		fileinfoCard = new CardLayout();
		fileinfoPanel.setLayout(fileinfoCard);
		fileinfoPanel.setBounds(0, 60, 720, 360);

		// 发送文件信息列表
		sendfileinfoPanel = new JPanel();
		recvfileinfoPanel = new JPanel();
		donefileinfoPanel = new JPanel();

		sendfileinfoPanel.setLayout(new BorderLayout());
		recvfileinfoPanel.setLayout(new BorderLayout());
		donefileinfoPanel.setLayout(new BorderLayout());
		// 文件元素模型
		// sendfileModel = new sendfileTableModel(20);
		sendTableModel = new BaseTableModel(0);
		recvTableModel = new BaseTableModel(1);
		doneTableModel = new BaseTableModel(2);
		// 文件信息表格

		sendfileTable = new FileTable(sendTableModel, 0);
		recvfileTable = new FileTable(recvTableModel, 1);
		donefileTable = new FileTable(doneTableModel, 2);
		// 文件信息拖动条
		sendfileScrollPane = new JScrollPane(sendfileTable);

		sendfileScrollPane.getViewport()
				.setBackground(new Color(228, 237, 254));
		// 把整个table加入sendfileinfoPanel
		sendfileinfoPanel.add(sendfileScrollPane, BorderLayout.CENTER);

		recvfileScrollPane = new JScrollPane(recvfileTable);
		recvfileScrollPane.getViewport()
				.setBackground(new Color(228, 237, 254));
		recvfileinfoPanel.add(recvfileScrollPane, BorderLayout.CENTER);

		donefileScrollPane = new JScrollPane(donefileTable);
		donefileScrollPane.getViewport()
				.setBackground(new Color(228, 237, 254));
		donefileinfoPanel.add(donefileScrollPane, BorderLayout.CENTER);

		fileinfoPanel.add(sendfileinfoPanel, "sendfileinfoPanel");
		fileinfoPanel.add(recvfileinfoPanel, "recvfileinfoPanel");
		fileinfoPanel.add(donefileinfoPanel, "donefileinfoPanel");

	}

	private void initHelper() {
		dataHelper = new DataHelper(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getSource();
		if (source == sendAddBtn) {
			dataHelper.selectFileToSend();
		} else if (source == sendBtn) {
			if (dataHelper.getDestIP().isEmpty()) {
				JOptionPane.showMessageDialog(this, "请选择接收方IP");
			} else if (dataHelper.getRecentFileName().isEmpty()) {
				JOptionPane.showMessageDialog(this, "请选择要发送的文件");
			} else {
				JSONObject object = dataHelper.setSendObject();
				JSONObject tmpObject = dataHelper.getTmpObject();
//				try {
//					String filetime = object.getString(Constants.FILE_TIME);
//					if(tmpObject != null && tmpObject.getString(Constants.FILE_TIME).equals(filetime)) {
//						JOptionPane.showMessageDialog(null, "请勿重复发送！");
//					} else {
						sendTableModel.addRow(object);
						dataHelper.getSendHelper().sendRequest(object);
						dataHelper.setTmpObject(object);
//					}
//				} catch (JSONException e1) {
//					// TODO 自动生成的 catch 块
//					e1.printStackTrace();
//				}
//
			}
		}
	}

}
