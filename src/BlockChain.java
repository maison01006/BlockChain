import java.security.MessageDigest;
import java.util.ArrayList;

import org.json.simple.JSONObject;



class Blockchain{
	ArrayList<Block> chain = new ArrayList<Block>();
	JsonControl jc = new JsonControl();
	Block block = new Block();
	public Blockchain(JSONObject jo) {
		this.pushList(jo);
	}
	public void pushList(JSONObject jo) {
		for(int i=0;i<jo.size();i++) {
			JSONObject jo2 = (JSONObject)jo.get("block"+i);
			Block block = new Block(jo2);
			this.chain.add(block);
		}
	}


	public Block getLastestBlock(){
		return this.chain.get(this.chain.size()-1);
	}

	public void addBlock(Block newBlock){
		System.out.println("block add");
		this.chain.add(newBlock);
		jc.storeJson(newBlock);
	}

	public boolean isChainValid() {
		for(int i = 1; i < this.chain.size(); i++){
			Block currentBlock = this.chain.get(i);
			Block previousBlock = this.chain.get(i-1);

			if(currentBlock.hash != currentBlock.calculateHash()){
				return false;
			}

			if(currentBlock.previousHash != previousBlock.hash){
				return false;
			}
		}

		return true;
	}

}
