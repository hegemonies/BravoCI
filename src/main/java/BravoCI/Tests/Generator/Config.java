package BravoCI.Tests.Generator;

public class Config {
	private String compiler;
	private String targetFile;

	public String getCompiler() {
		return this.compiler;
	}

	public String getTargetFile() {
		return targetFile;
	}

	@Override
	public String toString() {
		return "Compiler: " + compiler
				+ "\ntargetFile: " + targetFile;
	}
}
