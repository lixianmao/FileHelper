package main;
import helper.NoteMessage;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * ���շ�����֪ͨ���̣߳����ӵ�������Ĭ�ϵķ���˿�6666
 * @author Administrator
 *
 */
public class NoteThread implements Runnable{

	private DataInputStream dis;
	private boolean runFlag = true;
	private NoteMessage note;
	
	public NoteThread(DataInputStream dis, NoteMessage note) {
		//this.client = client;
		this.dis = dis;
		this.note = note;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("NoteThread: " + Thread.currentThread());
		while(runFlag) {
			listen();
		}
	}
	
	//
	private void listen() {
		try {
			String msg = dis.readUTF();
			note.handleMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stopThread();
		} 
	}
	
	public void stopThread() {
		runFlag = false;
	}
}
