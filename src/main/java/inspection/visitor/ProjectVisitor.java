package inspection.visitor;

import inspection.element.ConfigurationFile;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;

public interface ProjectVisitor {
    void visit(SourceCodeFile file);

    void visit(TestFile file);

    void visit(ConfigurationFile file);

    void visit(DependencyFile file);

    void visit(Directory directory);
}
