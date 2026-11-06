package inspection.visitor;

import inspection.element.ConfigurationFile;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;

public class QualityGateVisitor implements ProjectVisitor {
    private static final double MAX_AVERAGE_COMPLEXITY = 10;
    private static final double MIN_TEST_SOURCE_RATIO = 0.40;

    private final SecurityAuditVisitor security = new SecurityAuditVisitor();
    private final ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();

    @Override
    public void visit(SourceCodeFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(TestFile file) {
        statistics.visit(file);
    }

    @Override
    public void visit(ConfigurationFile file) {
        security.visit(file);
    }

    @Override
    public void visit(DependencyFile file) {
        security.visit(file);
    }

    @Override
    public void visit(Directory directory) {
    }

    public boolean securityPasses() {
        return security.criticalIssueCount() == 0;
    }

    public boolean complexityPasses() {
        return statistics.averageComplexity() <= MAX_AVERAGE_COMPLEXITY;
    }

    public boolean testsPass() {
        return statistics.testSourceRatio() >= MIN_TEST_SOURCE_RATIO;
    }

    public boolean passes() {
        return securityPasses() && complexityPasses() && testsPass();
    }

    public String report() {
        return """
                Security: %s
                Complexity: %s
                Tests: %s
                Final result: %s""".formatted(status(securityPasses()), status(complexityPasses()),
                status(testsPass()), status(passes()));
    }

    private String status(boolean passes) {
        return passes ? "PASS" : "FAIL";
    }
}
