package inspection.element;

import inspection.visitor.ProjectVisitor;
import java.util.List;

public record DependencyFile(String name, List<Dependency> dependencies) implements ProjectElement {
    public DependencyFile {
        dependencies = List.copyOf(dependencies);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
