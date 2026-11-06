package inspection.element;

import inspection.visitor.ProjectVisitor;

public record SourceCodeFile(String name, String module, int linesOfCode, int complexity,
                             boolean hardcodedCredentials, boolean highRisk) implements ProjectElement {
    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
