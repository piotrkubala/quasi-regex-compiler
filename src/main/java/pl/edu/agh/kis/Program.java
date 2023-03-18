package pl.edu.agh.kis;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Program {
    private static class Line {
        private final String content;
        private int indentation = 0;

        Line(String content) {
            this.content = content;
        }

        Line(String content, int indentation) {
            this.content = content;
            this.indentation = indentation;
        }

        private Line indent(int indentAmount) {
            indentation += indentAmount;
            return this;
        }
    }

    private List<Line> lines;
    private final String indentationString;

    public Program() {
        this.lines = new ArrayList<>();
        this.indentationString = "    ";
    }

    public Program appendLine(String line) {
        lines.add(new Line(line));
        return this;
    }

    public Program appendLine(String line, int indentation) {
        lines.add(new Line(line, indentation));
        return this;
    }

    public Program appendBlock(Program block) {
        return appendBlock(block, 0);
    }

    public Program appendBlock(Program block, int indentation) {
        lines.addAll(block.indent(indentation).lines);
        return this;
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        for (Line line : lines) {
            builder.append(indentationString.repeat(line.indentation))
                    .append(line.content)
                    .append('\n');
        }
        return builder.toString();
    }

    private Program indent(int indentAmount) {
        lines = lines.stream().map(line -> line.indent(indentAmount)).collect(Collectors.toList());
        return this;
    }
}
