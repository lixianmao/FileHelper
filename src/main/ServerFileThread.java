package main;

import helper.Constants;
import helper.DataHelper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import ui.GUI_V4;
/**
 * 向服务器你上传或下载文件的线程
 * @author Administrator
 *
 */
public class ServerFileThread implements Runnable{

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private File sendFile;
	private File recvFile;
	private JSONObject object;
	
	public ServerFileThread(JSONObject object) {
		this.object = object;
	}
	
	private void initSocket(JSONObject object) {
		
		try {
			int port = object.getInt(Constants.PORT);
			socket = new Socket(Constants.HOST_NAME, port);
			dis = new DataInputStream(new BufferedInputStream(
					socket.getInputStream()));
			dos = new DataOutputStream(socket.getOutputStream());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initSendFile(JSONObject object) {
		try {
			String sendPath = object.getString(Constants.SEND_PATH);
			sendFile = new File(sendPath);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createRecvFile(JSONObject object) {
		try {
			String fileName = object.getString(Constants.FILE_NAME);
			String recvPath = object.getString(Constants.RECV_PATH);
			recvFile = new File(recvPath, fileName);
			recvFile.createNewFile();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initFileInfo(JSONObject object) {
		try {
			int type = object.getInt(Constants.TYPE);
			dos.writeUTF(object.toString());
			if(type == Constants.TYPE_UPLOAD) {
				initSendFile(object);
				sendFile();
			} else {
				createRecvFile(object);
				recvFile();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void recvFile() {
		try {
			
			FileOutputStream fos = new FileOutputStream(recvFile);
			byte[] buffer = new byte[Constants.BUF_SIZE];
			int hasRead = 0;
			while ((hasRead = dis.read(buffer)) > 0) {
				fos.write(buffer, 0, hasRead);
			}
			System.out.println("文件接收完成");
			fos.close();
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, recvFile.getName() + "\n下载完成");
	}

	private void sendFile() {
		try {
			FileInputStream fis = new FileInputStream(sendFile);
			byte[] buffer = new byte[Constants.BUF_SIZE];
			int hasRead = 0;
			while ((hasRead = fis.read(buffer)) > 0) {
				dos.write(buffer, 0, hasRead);
			}
			System.out.println("文件发送完成");
			dos.close();
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DataHelper.sendHelper.sendServerFiles();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		initSocket(object);
		initFileInfo(object);
	}

}
