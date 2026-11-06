package inspection.visitor;

import inspection.element.ConfigurationFile;
import inspection.element.Dependency;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;

import java.util.ArrayList;
import java.util.List;

public class SecurityAuditVisitor implements ProjectVisitor {
    private final List<String> findings = new ArrayList<>();
    private int criticalIssueCount;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hardcodedCredentials()) {
            addCritical(file.name() + " contains hardcoded credentials");
        }
        if (file.highRisk()) {
            findings.add("WARNING: " + file.name() + " is marked as high risk");
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.exposedCredentials()) {
            addCritical(file.name() + " exposes credentials");
        }
        if (file.insecure()) {
            addCritical(file.name() + " uses insecure settings");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (dependency.vulnerable()) {
                addCritical(file.name() + " declares vulnerable dependency " + dependency.name());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public int criticalIssueCount() {
        return criticalIssueCount;
    }

    public List<String> findings() {
        return List.copyOf(findings);
    }

    public String report() {
        if (findings.isEmpty()) {
            return "No security issues found.";
        }
        return String.join("\n", findings) + "\nCritical issues: " + criticalIssueCount;
    }

    private void addCritical(String issue) {
        findings.add("CRITICAL: " + issue);
        criticalIssueCount++;
    }
}
