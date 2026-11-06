package inspection;

import inspection.element.ConfigurationFile;
import inspection.element.Dependency;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;
import inspection.visitor.ProjectStatisticsVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectStatisticsVisitorTest {
    @Test
    void countsFilesMetricsTestsAndDependenciesAcrossTheTree() {
        Directory project = new Directory("project", List.of(
                new Directory("src", List.of(
                        new SourceCodeFile("Payment.java", "payments", 100, 5, false, false),
                        new SourceCodeFile("User.java", "users", 50, 10, false, false))),
                new TestFile("PaymentTest.java", "payments", 4),
                new ConfigurationFile("application.properties", false, false),
                new DependencyFile("pom.xml", List.of(
                        new Dependency("lib-one", "1.0", false),
                        new Dependency("lib-two", "2.0", false)))));
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(5, visitor.totalFiles());
        assertEquals(2, visitor.sourceFiles());
        assertEquals(150, visitor.linesOfCode());
        assertEquals(4, visitor.testCount());
        assertEquals(2, visitor.dependencyCount());
        assertEquals(7.5, visitor.averageComplexity(), 0.0001);
        assertEquals(0.5, visitor.testSourceRatio(), 0.0001);
    }

    @Test
    void usesZeroForRatiosWhenTheProjectHasNoSourceFiles() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();

        new TestFile("StandaloneTest.java", "test-only", 2).accept(visitor);

        assertEquals(1, visitor.totalFiles());
        assertEquals(2, visitor.testCount());
        assertEquals(0.0, visitor.averageComplexity());
        assertEquals(0.0, visitor.testSourceRatio());
    }
}
