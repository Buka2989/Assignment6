package inspection;

import inspection.element.ConfigurationFile;
import inspection.element.Dependency;
import inspection.element.DependencyFile;
import inspection.element.SourceCodeFile;
import inspection.visitor.SecurityAuditVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityAuditVisitorTest {
    @Test
    void separatesHardcodedCredentialsFromHighRiskWarning() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();

        new SourceCodeFile("Payment.java", "payments", 80, 12, true, true).accept(visitor);

        assertEquals(1, visitor.criticalIssueCount());
        assertEquals(List.of("CRITICAL: Payment.java contains hardcoded credentials",
                "WARNING: Payment.java is marked as high risk"), visitor.findings());
    }

    @Test
    void detectsInsecureSettingsAndExposedCredentials() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();

        new ConfigurationFile("application.properties", true, true).accept(visitor);

        assertEquals(2, visitor.criticalIssueCount());
        assertEquals(List.of("CRITICAL: application.properties exposes credentials",
                "CRITICAL: application.properties uses insecure settings"), visitor.findings());
    }

    @Test
    void reportsOnlyVulnerableDependencies() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        DependencyFile file = new DependencyFile("pom.xml", List.of(
                new Dependency("safe-lib", "2.0", false),
                new Dependency("old-lib", "1.0", true)));

        file.accept(visitor);

        assertEquals(1, visitor.criticalIssueCount());
        assertEquals(List.of("CRITICAL: pom.xml declares vulnerable dependency old-lib"),
                visitor.findings());
    }
}
