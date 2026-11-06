package inspection.visitor;

import inspection.element.ConfigurationFile;
import inspection.element.Dependency;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ComplianceVisitor implements ProjectVisitor {
    private static final Set<String> PROHIBITED_DEPENDENCIES = Set.of("legacy-crypto");

    private final List<String> violations = new ArrayList<>();
    private final Set<String> productionModules = new LinkedHashSet<>();
    private final Set<String> testedModules = new HashSet<>();

    @Override
    public void visit(SourceCodeFile file) {
        productionModules.add(file.module());
        if (file.hardcodedCredentials()) {
            violations.add(file.name() + " contains credentials");
        }
    }

    @Override
    public void visit(TestFile file) {
        if (file.testCount() > 0) {
            testedModules.add(file.module());
        }
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.exposedCredentials()) {
            violations.add(file.name() + " contains credentials");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.dependencies()) {
            if (dependency.version() == null || dependency.version().isBlank()) {
                violations.add(dependency.name() + " has no specified version");
            }
            if (PROHIBITED_DEPENDENCIES.contains(dependency.name())) {
                violations.add(dependency.name() + " is prohibited");
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public List<String> violations() {
        List<String> result = new ArrayList<>(violations);
        for (String module : productionModules) {
            if (!testedModules.contains(module)) {
                result.add(module + " has no tests");
            }
        }
        return List.copyOf(result);
    }

    public String report() {
        List<String> result = violations();
        if (result.isEmpty()) {
            return "All compliance rules passed.";
        }
        return String.join("\n", result);
    }
}
