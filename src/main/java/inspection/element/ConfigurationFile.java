package inspection.element;

import inspection.visitor.ProjectVisitor;

public record ConfigurationFile(String name, boolean exposedCredentials, boolean insecure) implements ProjectElement {
    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
