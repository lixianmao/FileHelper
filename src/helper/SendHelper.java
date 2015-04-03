package helper;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import main.FilePortThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SendHelper {

	private DataOutputStream dos;

	public SendHelper(DataOutputStream dos) {
		this.dos = dos;
	}
	
	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	/** 客户端向服务器发送登陆请求  */
	public void sendLogin(String localIP) {
		System.out.println(localIP + " 正在发送登陆请求");
		
		try {
			JSONObject object = new JSONObject();
			object.put(Constants.TYPE, Constants.TYPE_LOGIN);
			object.put(Constants.SRC_IP, localIP);
			dos.writeUTF(object.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	// 发送方请求发送文件
	public void sendRequest(JSONObject object) {
		System.out.println("正在发送文件请求");

		try {
			object.put(Constants.TYPE, Constants.TYPE_REQUEST);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方发送回应
	public void sendResponse(JSONObject object, boolean response) {
		System.out.println("正在发送回应");
	
		try {
			if (response) {
				int port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port);
				object.put(Constants.BREAKPOINT, 0);
				object.put(Constants.COMPLETE, 0);

				new Thread(new FilePortThread(port)).start();
			}

			object.put(Constants.TYPE, Constants.TYPE_RESPONSE);
			object.put(Constants.RESPONSE, response);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收文件中断，向服务器发送断点信息
	public void sendBreakpoint(JSONObject object, long breakpoint) {
		System.out.println("正在向服务器发送断点信息");
		try {
			object.put(Constants.TYPE, Constants.TYPE_BREAKPOINT);
			object.put(Constants.BREAKPOINT, breakpoint);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	//读取本地临时文件
	private JSONArray readTmpFile() {
		File folder = new File("breakpoint");
		if(!folder.exists()) 
			return null;
		JSONArray array = new JSONArray();
		File[] tmpFiles = folder.listFiles();
		for (File file : tmpFiles) {
			try {
				FileReader reader = new FileReader(file);
				char[] buf = new char[(int) file.length()];
				reader.read(buf);
				array.put(new JSONObject(new String(buf)));
				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return array;
	}
	
	//接收端恢复和服务器的连接后，向服务器发送断点信息
	public void sendBreakpoints(String srcIP) {
		System.out.println("正在向服务器发送本地断点记录");
		
		JSONArray array = readTmpFile();
		JSONObject object = new JSONObject();
		try {
			object.put(Constants.TYPE, Constants.TYPE_BREAKPOINTS);
			object.put(Constants.SRC_IP, srcIP);
			if(array == null || array.length() == 0) {
				object.put(Constants.RECORDS, "");
			} else {
				object.put(Constants.RECORDS, array.toString());
			}
			dos.writeUTF(object.toString());
			deleteFolder();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//删除存放临时文件的文件夹
	private void deleteFolder() {
		File folder = new File("breakpoint");
		if(!folder.exists()) 
			return;
		File[] files = folder.listFiles();
		for (File file : files) {
			file.delete();
		}
		folder.delete();
	}
	
	// 接收方完成文件接收，向服务器发送完成信号
	public void sendComplete(JSONObject object) {
		System.out.println("正在向服务器报告发送文件接收完成信息");
		try {
			object.put(Constants.TYPE, Constants.TYPE_COMPLETE);
			object.put(Constants.COMPLETE, 1);
			object.put(Constants.BREAKPOINT, 0);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 发送方在中断后请求继续发送文件
	public void sendReqSend2(JSONObject object) {
		System.out.println("正在请求继续发送文件");
		try {
			object.put(Constants.TYPE, Constants.TYPE_REQSEND2);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方回复发送方关于继续发送文件请求
	public void sendRespSend2(JSONObject object, boolean response) {
		System.out.println("正在发送继续传输文件回应");
		try {
			if (response) {
				int port = PortHelper.getAvailablePort(object
						.getString(Constants.SRC_IP));
				object.put(Constants.PORT, port);
				object.put(Constants.BREAKPOINT, 0);

				new Thread(new FilePortThread(port)).start();
			}

			object.put(Constants.TYPE, Constants.TYPE_RESPSEND2);
			object.put(Constants.RESPONSE, response);
			dos.writeUTF(object.toString());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 接收方在中断后请求继续接收文件
	public void sendReqRecv2(JSONObject object) {
		System.out.println("正在请求继续接收文件");
		try {
			object.put(Constants.TYPE, Constants.TYPE_REQRECV2);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// 发送方回复接收方关于继续接收文件的请求
	public void sendRespRecv2(JSONObject object, boolean response) {
		System.out.println("正在发送请求继续接收文件的回复");

		try {
			object.put(Constants.TYPE, Constants.TYPE_RESPRECV2);
			object.put(Constants.RESPONSE, response);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	//发送删除列表文件内容的信息
	public void sendDelete(JSONArray array) {
		JSONObject object = new JSONObject();
		System.out.println("正在向服务器发送删除信息");
		try {
			object.put(Constants.TYPE, Constants.TYPE_DELETE);
			object.put(Constants.RECORDS, array.toString());
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 发送上传文件到云端的请求  */
	public void sendUpload(File file) {
		System.out.println("正在向服务器发送上传请求");
		JSONObject object = new JSONObject();
		try {
			String fileName = file.getName();
			String filePath = file.getAbsolutePath();
			object.put(Constants.TYPE, Constants.TYPE_UPLOAD);
			object.put(Constants.FILE_NAME, fileName);
			object.put(Constants.SEND_PATH, filePath);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 发送下载文件请求  */
	public void sendDownload(String fileName, String filePath) {
		System.out.println("正在向服务器发送下载请求");
		JSONObject object = new JSONObject();
		try {
			object.put(Constants.TYPE, Constants.TYPE_DOWNLOAD);
			object.put(Constants.FILE_NAME, fileName);
			object.put(Constants.RECV_PATH, filePath);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 请求云端文件列表  */
	public void sendServerFiles() {
		System.out.println("正在向服务器发送文件列表请求");
		JSONObject object = new JSONObject();
		try {
			object.put(Constants.TYPE, Constants.TYPE_SERVERFILES);
			dos.writeUTF(object.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
