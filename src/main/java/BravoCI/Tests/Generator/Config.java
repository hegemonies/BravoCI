package BravoCI.Tests.Generator;

public class Config {
	private String compiler;

	public String getCompile() {
		return this.compiler;
	}

	@Override
	public String toString() {
		return "Compiler: " + compiler;
	}
}
