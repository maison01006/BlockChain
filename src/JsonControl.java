
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonControl {
	JSONParser jp = new JSONParser();

	public JSONObject readJson() {
		JSONObject jo=null;
		try(Reader reader = new FileReader("./src/data/block.json")){
			jo = (JSONObject)jp.parse(reader);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jo;
	}
	public void storeJson(Block block) {
		try{
			System.out.println("저장");
			Reader reader = new FileReader("./src/data/block.json");
			JSONObject jo = (JSONObject)jp.parse(reader);
			jo.put("block"+(jo.size()+1),block.toJson());
			Writer write = new FileWriter("./src/data/block.json");
			write.write(jo.toJSONString());
			write.flush();
			write.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public JSONObject parse(String json) {
		JSONObject jo=null;
		try {
			jo = (JSONObject)jp.parse(json);
			System.out.println(jo);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jo;
	}
	public JSONObject store(String name,String ssn, String disease) {
		JSONObject jo1 = new JSONObject();
		jo1.put("환자이름", name);
		jo1.put("주민번호", ssn);
		jo1.put("병명", disease);

		return jo1;
	}
}
