package inspection;

import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.visitor.TechnicalDebtVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechnicalDebtVisitorTest {
    @Test
    void addsCleanupEstimatesForComplexAndLongFiles() {
        Directory project = new Directory("project", List.of(
                new SourceCodeFile("A.java", "app", 201, 11, false, false),
                new SourceCodeFile("B.java", "app", 90, 12, false, false)));
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        project.accept(visitor);

        assertEquals(List.of("A.java exceeds complexity 10", "A.java exceeds 200 lines",
                "B.java exceeds complexity 10"), visitor.findings());
        assertEquals(5, visitor.estimatedHours());
    }

    @Test
    void doesNotFlagFilesAtTheLimits() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        new SourceCodeFile("App.java", "app", 200, 10, false, false).accept(visitor);

        assertTrue(visitor.findings().isEmpty());
        assertEquals(0, visitor.estimatedHours());
    }
}
