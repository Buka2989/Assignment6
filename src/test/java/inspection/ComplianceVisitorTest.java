package inspection;

import inspection.element.ConfigurationFile;
import inspection.element.Dependency;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;
import inspection.visitor.ComplianceVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplianceVisitorTest {
    @Test
    void acceptsAModuleWhenItsTestAppearsBeforeItsSource() {
        Directory project = new Directory("project", List.of(
                new TestFile("AppTest.java", "app", 2),
                new SourceCodeFile("App.java", "app", 60, 5, false, false),
                new DependencyFile("pom.xml", List.of(new Dependency("safe-lib", "1.0", false)))));
        ComplianceVisitor visitor = new ComplianceVisitor();

        project.accept(visitor);

        assertTrue(visitor.violations().isEmpty());
    }

    @Test
    void findsCredentialsMissingVersionsProhibitedDependenciesAndUntestedModules() {
        Directory project = new Directory("project", List.of(
                new SourceCodeFile("App.java", "app", 60, 5, true, false),
                new ConfigurationFile("application.properties", true, false),
                new DependencyFile("pom.xml", List.of(
                        new Dependency("legacy-crypto", "", false),
                        new Dependency("utilities", null, false))),
                new TestFile("AppTest.java", "app", 0)));
        ComplianceVisitor visitor = new ComplianceVisitor();

        project.accept(visitor);

        assertEquals(List.of("App.java contains credentials", "application.properties contains credentials",
                "legacy-crypto has no specified version", "legacy-crypto is prohibited",
                "utilities has no specified version", "app has no tests"), visitor.violations());
    }
}
