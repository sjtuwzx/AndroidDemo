package ctrip.android.hotel;

import java.io.FileInputStream;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

public class Test {

	public static void main(String[] args) throws Exception {
		// creates an input stream for the file to be parsed
		FileInputStream in = new FileInputStream("test.java");

		CompilationUnit cu;
		try {
			// parse the file
			cu = JavaParser.parse(in);
		} finally {
			in.close();
		}

		// prints the resulting compilation unit to default system output
		System.out.println(cu.toString());
	}
}
