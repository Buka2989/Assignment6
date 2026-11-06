package inspection.visitor;

import inspection.element.ConfigurationFile;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;

import java.util.ArrayList;
import java.util.List;

public class TechnicalDebtVisitor implements ProjectVisitor {
    private static final int MAX_COMPLEXITY = 10;
    private static final int MAX_LINES = 200;
    private static final int COMPLEXITY_HOURS = 2;
    private static final int LONG_FILE_HOURS = 1;

    private final List<String> findings = new ArrayList<>();
    private int estimatedHours;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.complexity() > MAX_COMPLEXITY) {
            findings.add(file.name() + " exceeds complexity " + MAX_COMPLEXITY);
            estimatedHours += COMPLEXITY_HOURS;
        }
        if (file.linesOfCode() > MAX_LINES) {
            findings.add(file.name() + " exceeds " + MAX_LINES + " lines");
            estimatedHours += LONG_FILE_HOURS;
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
    }

    @Override
    public void visit(DependencyFile file) {
    }

    @Override
    public void visit(Directory directory) {
    }

    public List<String> findings() {
        return List.copyOf(findings);
    }

    public int estimatedHours() {
        return estimatedHours;
    }

    public String report() {
        if (findings.isEmpty()) {
            return "No technical debt flags found.";
        }
        return String.join("\n", findings) + "\nEstimated cleanup: " + estimatedHours + " hours";
    }
}
