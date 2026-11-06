package inspection.visitor;

import inspection.element.ConfigurationFile;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;

import java.util.Locale;

public class ProjectStatisticsVisitor implements ProjectVisitor {
    private int totalFiles;
    private int sourceFiles;
    private int linesOfCode;
    private int testFiles;
    private int testCount;
    private int dependencyCount;
    private int totalComplexity;

    @Override
    public void visit(SourceCodeFile file) {
        totalFiles++;
        sourceFiles++;
        linesOfCode += file.linesOfCode();
        totalComplexity += file.complexity();
    }

    @Override
    public void visit(TestFile file) {
        totalFiles++;
        testFiles++;
        testCount += file.testCount();
    }

    @Override
    public void visit(ConfigurationFile file) {
        totalFiles++;
    }

    @Override
    public void visit(DependencyFile file) {
        totalFiles++;
        dependencyCount += file.dependencies().size();
    }

    @Override
    public void visit(Directory directory) {
    }

    public int totalFiles() {
        return totalFiles;
    }

    public int sourceFiles() {
        return sourceFiles;
    }

    public int linesOfCode() {
        return linesOfCode;
    }

    public int testCount() {
        return testCount;
    }

    public int dependencyCount() {
        return dependencyCount;
    }

    public double averageComplexity() {
        return sourceFiles == 0 ? 0.0 : (double) totalComplexity / sourceFiles;
    }

    public double testSourceRatio() {
        return sourceFiles == 0 ? 0.0 : (double) testFiles / sourceFiles;
    }

    public String report() {
        return String.format(Locale.ROOT, """
                Total files: %d
                Source files: %d
                Lines of source code: %d
                Test files: %d
                Test cases: %d
                Dependencies: %d
                Average source complexity: %.2f
                Test/source file ratio: %.2f""",
                totalFiles, sourceFiles, linesOfCode, testFiles, testCount,
                dependencyCount, averageComplexity(), testSourceRatio());
    }
}
