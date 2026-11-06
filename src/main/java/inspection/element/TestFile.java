package inspection.element;

import inspection.visitor.ProjectVisitor;

public record TestFile(String name, String module, int testCount) implements ProjectElement {
    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
