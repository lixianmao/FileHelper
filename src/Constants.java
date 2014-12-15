
public interface Constants {

	//服务器信息
	public static final String HOST = "127.0.0.1";
	public static final int PORT = 6666;

	//消息类型
	public static final byte TYPE_LOGIN = 0x0;
	public static final byte TYPE_LOGOUT = 0x1;
	public static final byte TYPE_REQUEST = 0x2;
	public static final byte TYPE_RESPONSE = 0x3;
	public static final byte TYPE_INTERUPT = 0x4;
	public static final byte TYPE_FILE = 0x5;
}
