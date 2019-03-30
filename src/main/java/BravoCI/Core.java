package BravoCI;

import BravoCI.Tests.Generator.Generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.IOException;

public class Core {
	public static void main(String... args) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();

		try (FileReader fileReader = new FileReader("bravo.json")) {
			Generator generator = gson.fromJson(fileReader, Generator.class);
			fileReader.close();

			generator.scriptGeneration();
		} catch (IOException exception) {
			exception.printStackTrace();
		}

	}
}
