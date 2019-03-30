package BravoCI.Tests.Generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Generator {
	private Config config;
	private List<Step> steps;

	public Config getConfig() {
		return config;
	}

	public List<Step> getSteps() {
		return this.steps;
	}

	public void scriptGeneration() {
		File file = new File("bravo.sh");
		try {
			file.createNewFile();
			file.setExecutable(true);

			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("#!/bin/bash\n\n");
			fileWriter.flush();

			for (Step step : this.steps) {
				fileWriter.write(step.getCmd() + "\n");
				fileWriter.flush();
			}

			fileWriter.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
