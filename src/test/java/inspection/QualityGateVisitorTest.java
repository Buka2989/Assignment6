package inspection;

import inspection.element.ConfigurationFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;
import inspection.visitor.QualityGateVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QualityGateVisitorTest {
    @Test
    void passesAtBothThresholds() {
        Directory project = new Directory("project", List.of(
                source("A.java", 10), source("B.java", 10), source("C.java", 10),
                source("D.java", 10), source("E.java", 10),
                new TestFile("ATest.java", "app", 1),
                new TestFile("BTest.java", "app", 1)));
        QualityGateVisitor visitor = new QualityGateVisitor();

        project.accept(visitor);

        assertTrue(visitor.passes());
    }

    @Test
    void failsWhenConfigurationHasACriticalSecurityIssue() {
        Directory project = new Directory("project", List.of(source("A.java", 5),
                new TestFile("ATest.java", "app", 1),
                new ConfigurationFile("application.properties", false, true)));
        QualityGateVisitor visitor = new QualityGateVisitor();

        project.accept(visitor);

        assertFalse(visitor.securityPasses());
        assertTrue(visitor.complexityPasses());
        assertTrue(visitor.testsPass());
        assertFalse(visitor.passes());
    }

    @Test
    void failsWhenComplexityIsTooHighAndTestsAreMissing() {
        QualityGateVisitor visitor = new QualityGateVisitor();

        source("A.java", 11).accept(visitor);

        assertTrue(visitor.securityPasses());
        assertFalse(visitor.complexityPasses());
        assertFalse(visitor.testsPass());
        assertFalse(visitor.passes());
    }

    private SourceCodeFile source(String name, int complexity) {
        return new SourceCodeFile(name, "app", 100, complexity, false, false);
    }
}
