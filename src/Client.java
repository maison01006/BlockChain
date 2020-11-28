/*
 key���� �����ڵ� + ����ð�����
 ���ο� Ŭ���̾�Ʈ�� ���� 10���� ���� ��ǥ�� ȹ��

 */



import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.lang.Thread.State;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject; 
import org.json.simple.parser.JSONParser; 
import org.json.simple.parser.ParseException;

public class Client {
	JFrame f = new JFrame();
	JPanel p = new JPanel();
	JPanel basePanel = new JPanel();
	JPanel loginPanel = new JPanel();

	JTextArea t = new JTextArea();
	JLabel nameL = new JLabel("이름");
	JLabel ssnL = new JLabel("주민번호");
	JLabel diseaseL = new JLabel("병명");
	JTextField name = new JTextField();
	JTextField ssn = new JTextField();
	JTextField disease = new JTextField();
	JTextField idText = new JTextField();

	JButton postB = new JButton("post");
	JButton blockB = new JButton("block");
	JButton loginB = new JButton("login");
	JButton exitB = new JButton("exit");
	CardLayout card = new CardLayout();

	BufferedReader sbr = null;
	PrintWriter spw = null;

	ArrayList<Socket> postSockList = new ArrayList<Socket>();
	ArrayList<Socket> getSockList = new ArrayList<Socket>();

	ArrayList<BlockThread> blockThreadList = new ArrayList<BlockThread>();

	JsonControl jc = new JsonControl();
	JSONArray dataList = new JSONArray(); 

	Blockchain blockChain = new Blockchain(jc.readJson());
	Block block = new Block();

	int agr=0;
	int opp=0;

	

	public Client() {
		f.setVisible(true);
		f.setSize(550,800);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		basePanel.setLayout(card);
		loginPanel.setLayout(null);
		p.setLayout(null);

		idText.setBounds(100, 150, 100, 40);
		loginB.setBounds(250, 150, 100, 40);



		t.setBounds(0, 0, 500, 300);
		nameL.setBounds(130, 530, 100, 30);
		name.setBounds(250, 530, 100, 30);
		ssnL.setBounds(130, 580, 100, 30);
		ssn.setBounds(250, 580, 100, 30);
		diseaseL.setBounds(130, 630, 100, 30);
		disease.setBounds(250, 630, 100, 30);
		postB.setBounds(430, 350, 70, 100);
		exitB.setBounds(430, 470,70,100);
		blockB.setBounds(430, 580, 70, 100);

		f.add(basePanel);
		basePanel.add("login",loginPanel);
		basePanel.add("p",p);
		loginPanel.add(loginB);
		loginPanel.add(idText);

		p.add(t);
		p.add(nameL);
		p.add(ssnL);
		p.add(diseaseL);
		p.add(name);
		p.add(ssn);
		p.add(disease);
		p.add(postB);
		p.add(exitB);
		p.add(blockB);
	}
	public static void main(String[] args) {
		Client tc = new Client();
		Client.OpenServer os = tc.new OpenServer();
		Socket sock;


		try {
			sock = new Socket("114.71.208.82",5555);
			tc.spw = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
			tc.sbr = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			InetAddress ip = InetAddress.getLocalHost();

			Client.GetIPThread git = tc.new GetIPThread();
			git.start();

			tc.loginB.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					tc.spw.println(ip.getHostAddress());
					tc.spw.flush();
					tc.card.show(tc.basePanel, "p");
					os.start();
				}
			});


			tc.postB.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {

					JSONObject msgJson = tc.jc.store(tc.name.getText(), tc.ssn.getText(), tc.disease.getText());
					for(int i=0;i<tc.postSockList.size();i++) {
						try {

							PrintWriter pw = new PrintWriter(new OutputStreamWriter(tc.postSockList.get(i).getOutputStream()));		
							pw.println(msgJson);
							pw.flush();

							System.out.println(tc.postSockList.get(i).getInetAddress()+" 보냄");
						}catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					tc.dataList.add(msgJson);
					if(tc.dataList.size()==10) {
						BlockThread bt = tc.new BlockThread(tc.dataList);
						tc.dataList = new JSONArray();
						tc.blockThreadList.add(bt);
						tc.blockThreadList.get(0).start();		
						tc.blockThreadList.remove(0);
					}


				}
			});

			tc.blockB.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if(tc.blockChain.chain.size()!=0) {
						System.out.println(tc.blockChain.chain.get(0).data);
						System.out.println(tc.blockChain.chain.get(0).hash);
						for(int i=1;i<tc.blockChain.chain.size();i++) {
							JSONParser jp = new JSONParser();

							try {
								JSONArray ja = (JSONArray)jp.parse(tc.blockChain.chain.get(i).data);
								for(int j=0;j<ja.size();j++) {
									System.out.println(ja.get(j));
								}
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							System.out.println(tc.blockChain.chain.get(i).hash);	
						}

					}
				}
			});
		} catch (Exception e) {

		}

		tc.exitB.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				tc.spw.println("exit");
				tc.spw.flush();
				System.exit(0);
			}
		});


	}
	class OpenServer extends Thread{
		public void run() {
			try {
				ServerSocket server = new ServerSocket(5550);
				while(true) {
					Socket sock = server.accept();
					GetMsgThread gmt = new GetMsgThread(sock);
					gmt.start();

					getSockList.add(sock);



				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	class GetIPThread extends Thread{
		String ss=null;

		public void run() {
			while(true) {
				try {
					while((ss=sbr.readLine())!=null) {
						t.append(ss+"\n");

						Socket sock = new Socket(ss,5550);
						postSockList.add(sock);
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}

			}
		}
	}

	class GetMsgThread extends Thread{
		String msg=null;
		Socket sock;
		BufferedReader br = null;
		PrintWriter pw = null;
		public GetMsgThread(Socket sock) {
			this.sock = sock;
		}
		public void run() {
			while(true) {
				try {
					br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

					while((msg = br.readLine())!=null) {
						if(msg.equals("vote1")) {
							agr++;
						}else if(msg.equals("vote0")){
							opp++;
						}
						else if(msg.equals("block")) {
							System.out.println("block data : "+ block.data);
							block.setType();
							if(block.checkHash(br.readLine(),Long.parseLong(br.readLine()))) {
								for(int i=0;i<getSockList.size();i++) {
									pw = new PrintWriter(new OutputStreamWriter(getSockList.get(i).getOutputStream()));
									pw.println("vote1");
									pw.flush();
								}
							}
							else {
								for(int i=0;i<getSockList.size();i++) {
									pw = new PrintWriter(new OutputStreamWriter(getSockList.get(i).getOutputStream()));
									pw.println("vote0");
									pw.flush();
								}
							}
						}else{
							System.out.println("받은갯수 : "+(dataList.size()+1));

							dataList.add(jc.parse(msg));
							t.append(sock.getInetAddress().getHostAddress()+" : "+msg+"\n");

							if(dataList.size()==10) {
								BlockThread bt = new BlockThread(dataList);
								dataList = new JSONArray();
								bt.start();
							}
						}

					}

				} catch (IOException e1) {
					for(int i=0;i<postSockList.size();i++) {
						if(postSockList.get(i)==sock) {
							postSockList.remove(i);
							getSockList.remove(i);
							break;
						}
					}
					break;
				}

			}
		}
	}
	class BlockThread extends Thread{
		VoteTimeThread vtt;
		Boolean mine=true;
		String json="";
		public BlockThread(JSONArray dataList) {
			this.json=dataList.toJSONString();
		}
		public void run() {

			synchronized (BlockThread.class) {

				block=new Block(blockChain.chain.size(),"0" ,json,blockChain.getLastestBlock().hash);
				try {
					if(block.mineBlock()) {

						System.out.println("vote start2");
						vtt = new VoteTimeThread();
						vtt.start();
						vtt.join();
						for(int i=0;i<postSockList.size();i++) {
							PrintWriter pw = new PrintWriter(new OutputStreamWriter(postSockList.get(i).getOutputStream()));
							pw.println("block");

							System.out.println("block");											
							pw.println(block.hash); //���� hash ������
							pw.println(block.nonce);
							pw.println("vote1");
							pw.flush();
						}
					}else {
							vtt = new VoteTimeThread();
							vtt.start();
							vtt.join();

					}
				}catch (Exception ex) {
					ex.printStackTrace();
				}

			}

		}
	}
	class VoteTimeThread extends Thread{
		int time=0;
		public void run() {
			while(time<=5) {
				try {
					Thread.sleep(1000);
					time++;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(true/*(opp+agr)>(serverSockList.size()*(2/3.0))*/) {
				agr=0;
				opp=0;
				if(true) {
					System.out.println("찬성");
					blockChain.addBlock(block);
				}else {
					System.out.println("반대");
					block.mineBlock();
				}
			}else {
				System.out.println("대기");
				this.run();
			}
		}
	}
}


