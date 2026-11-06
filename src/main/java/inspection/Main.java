package inspection;

import inspection.element.ConfigurationFile;
import inspection.element.Dependency;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;
import inspection.visitor.ComplianceVisitor;
import inspection.visitor.ProjectStatisticsVisitor;
import inspection.visitor.QualityGateVisitor;
import inspection.visitor.SecurityAuditVisitor;
import inspection.visitor.TechnicalDebtVisitor;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Directory project = new Directory("fintech-project", List.of(
                new Directory("src", List.of(
                        new SourceCodeFile("PaymentService.java", "payments", 235, 13, false, true),
                        new SourceCodeFile("UserService.java", "users", 85, 5, false, false))),
                new Directory("tests", List.of(new TestFile("PaymentServiceTest.java", "payments", 4))),
                new Directory("config", List.of(new ConfigurationFile("application.properties", false, true))),
                new DependencyFile("pom.xml", List.of(new Dependency("jackson-databind", "2.18.3", false)))
        ));

        SecurityAuditVisitor security = new SecurityAuditVisitor();
        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        QualityGateVisitor quality = new QualityGateVisitor();
        ComplianceVisitor compliance = new ComplianceVisitor();
        TechnicalDebtVisitor debt = new TechnicalDebtVisitor();

        project.accept(security);
        project.accept(statistics);
        project.accept(quality);
        project.accept(compliance);
        project.accept(debt);

        System.out.println("Inspection of " + project.name());
        printReport("Security audit", security.report());
        printReport("Project statistics", statistics.report());
        printReport("Quality gate", quality.report());
        printReport("Compliance", compliance.report());
        printReport("Technical debt", debt.report());
    }

    private static void printReport(String title, String report) {
        System.out.println();
        System.out.println("=== " + title + " ===");
        System.out.println(report);
    }
}
