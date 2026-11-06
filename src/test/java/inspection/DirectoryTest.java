package inspection;

import inspection.element.ConfigurationFile;
import inspection.element.DependencyFile;
import inspection.element.Directory;
import inspection.element.SourceCodeFile;
import inspection.element.TestFile;
import inspection.visitor.ProjectVisitor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectoryTest {
    @Test
    void visitsAllElementTypesInTreeOrder() {
        Directory project = new Directory("project", List.of(
                new Directory("src", List.of(new SourceCodeFile(
                        "App.java", "app", 30, 3, false, false))),
                new TestFile("AppTest.java", "app", 2),
                new ConfigurationFile("application.properties", false, false),
                new DependencyFile("pom.xml", List.of())
        ));
        RecordingVisitor visitor = new RecordingVisitor();

        project.accept(visitor);

        assertEquals(List.of("directory:project", "directory:src", "source:App.java",
                "test:AppTest.java", "config:application.properties", "dependency:pom.xml"),
                visitor.visits);
    }

    @Test
    void visitsAnEmptyDirectoryOnce() {
        RecordingVisitor visitor = new RecordingVisitor();

        new Directory("empty", List.of()).accept(visitor);

        assertEquals(List.of("directory:empty"), visitor.visits);
    }

    private static class RecordingVisitor implements ProjectVisitor {
        private final List<String> visits = new ArrayList<>();

        @Override
        public void visit(SourceCodeFile file) {
            visits.add("source:" + file.name());
        }

        @Override
        public void visit(TestFile file) {
            visits.add("test:" + file.name());
        }

        @Override
        public void visit(ConfigurationFile file) {
            visits.add("config:" + file.name());
        }

        @Override
        public void visit(DependencyFile file) {
            visits.add("dependency:" + file.name());
        }

        @Override
        public void visit(Directory directory) {
            visits.add("directory:" + directory.name());
        }
    }
}
