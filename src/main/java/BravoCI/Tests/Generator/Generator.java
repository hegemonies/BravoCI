package BravoCI.Tests.Generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class Generator {
	public static void scriptGeneration(Content content, String path) {
		File file = new File(path + "/bravo.sh");

		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			file.setExecutable(true);

			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("#!/bin/bash\n\n");
			fileWriter.flush();

			fileWriter.write("date &> logs.txt\n\n");
			fileWriter.flush();

			for (Step step : content.getSteps()) {
				fileWriter.write(step.getCmd() + " &>> logs.txt\n");
				fileWriter.flush();
			}

			fileWriter.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static Content readJSON(String path) throws FileNotFoundException {
		File file = new File(path + "/bravo.json");

		if (!file.exists()) {
			throw new FileNotFoundException();
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		try (FileReader fileReader = new FileReader(file)) {
			Content content = gson.fromJson(fileReader, Content.class);

			if (content != null) {
				return content;
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

		return null;
	}
}
