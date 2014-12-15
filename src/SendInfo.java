
public class SendInfo {

	private String srcIP;
	private String destIP;
	private String sendFilePath;
	
	public SendInfo(String srcIP, String destIP, String sendFilePath) {
		this.srcIP = srcIP;
		this.destIP = destIP;
		this.sendFilePath = sendFilePath;
	}
	
	public void setSrcIP(String ip) {
		srcIP = ip;
	}
	
	public void setDestIP(String ip) {
		destIP = ip;
	}
	
	public void setFilePath(String path) {
		sendFilePath = path;
	}
	
	public String getSrcIP() {
		return srcIP;
	}
	
	public String getDestIP() {
		return destIP;
	}
	
	public String getSendFilePath() {
		return sendFilePath;
	}
}
