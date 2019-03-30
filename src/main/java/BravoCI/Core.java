package BravoCI;

import BravoCI.Tests.Generator.Config;
import BravoCI.Tests.Generator.Generator;
import BravoCI.Tests.Generator.Step;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;

import java.util.List;

public class Core {
	public static void main(String... args) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		try {
			FileReader fileReader = new FileReader("bravo.json");
			Generator generator = gson.fromJson(fileReader, Generator.class);

			System.out.println(generator.getConfig().getCompile());

			List<Step> steps = generator.getSteps();
			for (Step step : steps) {
				System.out.println(step.getTitle());
				System.out.println(step.getCmd());
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}

	}
}
