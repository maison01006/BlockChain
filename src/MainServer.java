

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

public class MainServer {

	static ArrayList<Socket> sockList = new ArrayList<Socket>();
	static ArrayList<String> ipList = new ArrayList<String>();


	public static void main(String[] args) {

		MainServer s = new MainServer();
		AcceptThread at = new AcceptThread();
		at.start();

	}
	static class AcceptThread extends Thread{// Ŭ���̾�Ʈ ���� ������( Ŭ���̾�Ʈ�� out,in stream�� ����)
		String id;

		public void run() {

			try {
				ServerSocket server = new ServerSocket(5555);

				while(true){
					System.out.println("������ ��ٸ��ϴ�.");
					Socket sock = server.accept();
					
					ipList.add(new BufferedReader(new InputStreamReader(sock.getInputStream())).readLine());
					sockList.add(sock);
					
					exitThread et = new exitThread(sock);
					et.start();
					for(int i=0;i<sockList.size()-1;i++) {
					
						PrintWriter	pw = new PrintWriter(new OutputStreamWriter(sockList.get(i).getOutputStream()));
						pw.println(ipList.get(ipList.size()-1));
						pw.flush();
					}
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
					for(int i =0;i<ipList.size()-1;i++) {
						
						pw.println(ipList.get(i));
						pw.flush();
						System.out.println(ipList.get(i));
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	static class exitThread extends Thread{
		Socket sock;
		
		public exitThread(Socket sock) {
			this.sock=sock;
		}
		public void run() {
			while(true) {
				try {
					
				String br = new BufferedReader(new InputStreamReader(sock.getInputStream())).readLine();
				
				if(br.equals("exit")){
					for(int i=0;i<sockList.size();i++) {
						if(sockList.get(i)==sock) {
							sockList.remove(i);
							ipList.remove(i);
							break;
						}
					}
				}
				}catch(Exception e){

				}
			}
		}
	}
}




