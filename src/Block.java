

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONArray;
import org.json.simple.JSONObject;

public class Block{
	int index;
	String timestampm;
	String data;
	String previousHash;
	String hash;
	long nonce;
	
	public boolean type = true;
	int difficulty = 6;
	
	public Block() {}
	public Block(JSONObject jo) {
		this.index = Integer.parseInt(jo.get("index").toString());
		this.timestampm = jo.get("time").toString();
		this.data = jo.get("data").toString();
		this.previousHash = jo.get("previousHash").toString();
		this.hash = jo.get("hash").toString();
	}
	public Block(int index,String timestampm,String data,String previousHash) {
		this.index = index;
		this.timestampm = timestampm;
		this.data = data;
		this.hash = this.calculateHash();
		this.nonce = 0;
		this.previousHash=previousHash;
	}
	public String calculateHash(){
		MessageDigest md=null;
		String str="";
		try {
			md = MessageDigest.getInstance("SHA-256");


			str = this.index + this.previousHash + this.timestampm +this.data + this.nonce;
			md.update(str.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<byteData.length;i++) {
				sb.append(Integer.toString((byteData[i]&0xff)+0x100,16).substring(1));
			}
			str=sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			str=null;
		}
		return str;
	}
	public void setType() {
		this.type=false;
		System.out.println("type"+type);
	}
	public boolean mineBlock(){
		String str="";
		System.out.println("mine start");
		for(int i=0;i<this.difficulty;i++) {
			str+="0";
		}
		while((!this.hash.substring(0, this.difficulty).equals(str))&&type ){
			this.nonce=(long)(Math.random()*1000000000*1000000000)+1;
			this.hash = this.calculateHash();
			
		}
		if(type) {
			System.out.println("Block minded " + this.hash);
			System.out.println(this.nonce);
			System.out.println(this.index);
			System.out.println(this.previousHash);
			System.out.println(this.timestampm);
			System.out.println(this.data);
			return true;
		}
		type=true;

		return false;
	}
	public Boolean checkHash(String hash, long nonce) {
		this.nonce=nonce;

		String str = this.calculateHash();
		if(!hash.equals(str)) {
			System.out.println("hash : "+hash);
			System.out.println("str : "+str);
			System.out.println("nonce : "+nonce);
			System.out.println(this.index);
			System.out.println(this.previousHash);
			System.out.println(this.timestampm);
			System.out.println(this.data);
			return false;
		}
		this.hash=hash;
		return true;
	}
	public JSONObject toJson() {
		JSONObject jo = new JSONObject();
		
		jo.put("index", this.index);
		jo.put("timestampm", this.timestampm);
		jo.put("previousHash", this.previousHash);
		jo.put("hash", this.hash);
		jo.put("nonce", this.nonce);
		JSONArray ja = new JSONArray(this.data);
		
		jo.put("data", ja);
		
		return jo;
	}

}