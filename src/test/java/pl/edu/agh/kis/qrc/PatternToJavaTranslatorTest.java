package pl.edu.agh.kis.qrc;

import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.qrc.java.PatternToJavaTranslator;

public class PatternToJavaTranslatorTest {
    @Test
    public void LoopExprTest() {
        PatternToJavaTranslator translator = new PatternToJavaTranslator("Loop(a, b, c, Loop(c, x, Loop(a, b, c, d), z))");

        System.out.println(translator.generateCode());
    }
}