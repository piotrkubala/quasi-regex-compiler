package pl.edu.agh.kis;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import pl.edu.agh.kis.compiler.Compiler;

public class App {
    public static void main(String[] args) {
        try {
            String pattern = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);
            String code = Compiler.compile(pattern, Compiler.Language.PYTHON);
            System.out.print(code);
        } catch (IOException e) {
            System.err.println("Error while reading stdio");
        }
    }
}
