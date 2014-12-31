public interface Constants {

	// 服务器信息
	public static final String HOST_NAME = "127.0.0.1";
	public static final int PORT_NUM = 6666;

	// 消息类型
		public static final int TYPE_LOGIN = 0x0;
		public static final int TYPE_LOGOUT = 0x1;
		public static final int TYPE_REQUEST = 0x2;
		public static final int TYPE_RESPONSE = 0x3;
		public static final int TYPE_INTERUPT = 0x4;
		public static final int TYPE_FILE = 0x5;
		public static final int TYPE_COMPLETE = 0x6;
		public static final int TYPE_REQSEND2 = 0x7;
		public static final int TYPE_RESPSEND2 = 0x8;
		public static final int TYPE_BREAKPOINT = 0x9;
		public static final int TYPE_REQRECV2 = 0xa;
		public static final int TYPE_RESPRECV2 = 0xb;

	// JSON key
	public static final String TYPE = "type";
	public static final String RESPONSE = "response";
	public static final String SRC_IP = "srcIP";
	public static final String DEST_IP = "destIP";
	public static final String FILE_NAME = "fileName";
	public static final String FILE_LEN = "fileLen";
	public static final String PORT = "port";
	public static final String CLIENT_IN = "in";
	public static final String CLIENT_OUT = "out";
	public static final String BREAKPOINT = "breakpoint";
	public static final String COMPLETE = "complete";
}
