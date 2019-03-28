package BravoCI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class Core {
	public static void main(String... args) {
		JSONParser jsonParser = new JSONParser();

		try {
			FileReader fileReader = new FileReader("bravo.json");
			try {
				JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);

				JSONObject config = (JSONObject) jsonObject.get("config");

				System.out.println("Config");
				System.out.println(config.get("compile"));

				JSONArray steps = (JSONArray) jsonObject.get("steps");

				System.out.println("Steps");
				for (Object obj : steps) {
					JSONObject step = (JSONObject) obj;

					System.out.println(step.get("title"));
					System.out.println(step.get("cmd"));
				}
			} catch (ParseException exception) {
				exception.printStackTrace();
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
