package helper;
public interface Constants {

	// 服务器信息
	public static final String HOST_NAME = "192.168.0.4";
	public static final int PORT_NUM = 6666;

	// 消息类型
	public static final int TYPE_LOGIN = 0x0;
	public static final int TYPE_LOGOUT = 0x1;
	public static final int TYPE_REQUEST = 0x2;
	public static final int TYPE_RESPONSE = 0x3;
	public static final int TYPE_BREAKPOINTS = 0x4;
	public static final int TYPE_FILE = 0x5;
	public static final int TYPE_COMPLETE = 0x6;
	public static final int TYPE_REQSEND2 = 0x7;
	public static final int TYPE_RESPSEND2 = 0x8;
	public static final int TYPE_BREAKPOINT = 0x9;
	public static final int TYPE_REQRECV2 = 0xa;
	public static final int TYPE_RESPRECV2 = 0xb;
	public static final int TYPE_RECORD = 0xc;
	public static final int TYPE_DELETE = 0xd;
	public static final int TYPE_USERS = 0xe;
	public static final int TYPE_SERVERFILES = 0x10;
	public static final int TYPE_UPLOAD = 0x11;
	public static final int TYPE_DOWNLOAD = 0x12;

	// JSON key
	public static final String TYPE = "type";
	public static final String RESPONSE = "response";
	public static final String SRC_IP = "srcIP";
	public static final String DEST_IP = "destIP";
	public static final String FILE_NAME = "fileName";
	public static final String FILE_LEN = "fileLen";
	public static final String PORT = "port";
	public static final String RECV_PATH = "recvPath";
	public static final String SEND_PATH = "sendPath";
	public static final	String PORT_CLIENTS = "clients";
	public static final String BREAKPOINT = "breakpoint";
	public static final String COMPLETE = "complete";
	public static final String FILE_TIME = "fileTime";
	public static final String RECORDS = "records";
	public static final String USER_LIST = "userList";
	
	public static final int BUF_SIZE = 1024 * 64; 
	
}
