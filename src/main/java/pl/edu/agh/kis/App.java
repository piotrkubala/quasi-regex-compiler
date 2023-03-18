package pl.edu.agh.kis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static pl.edu.agh.kis.PatternCompiler.Language.*;

public class App {
    public static void main( String[] args ) {
        String pattern = "Para(a, Para(aw, ax, ay, az), c, d)";

        try {
            Files.writeString(Path.of("program.py"), PatternCompiler.compile(pattern, PYTHON));
        } catch (IOException ignore) {}
    }
}
