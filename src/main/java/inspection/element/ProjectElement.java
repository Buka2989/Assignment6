package inspection.element;

import inspection.visitor.ProjectVisitor;

public interface ProjectElement {
    void accept(ProjectVisitor visitor);
}
