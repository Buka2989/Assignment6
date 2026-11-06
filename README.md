# Assignment 6 Smart Code Inspection and Compliance Engine

I built a Java program that runs five checks on the same project tree. My demo models a small fintech project with two source files, one test file, an application configuration, and a dependency manifest. I give the files sample metrics and security flags when I build the tree in `Main`.

## How I run the project

I use Maven and JDK 17 or later. My Maven configuration targets Java 17, so I can also build it with my local JDK 27.

```bash
mvn test
mvn compile
java -cp target/classes inspection.Main
```

I expect 14 JUnit 5 tests. My demo prints the security audit, project statistics, quality gate, compliance check, and technical debt report in that order.

## How I chose the inspection rules

| Visitor | What I check |
| --- | --- |
| `SecurityAuditVisitor` | I treat hardcoded or exposed credentials, insecure settings, and dependencies marked as vulnerable as critical. I report source files marked as high risk as warnings. |
| `ProjectStatisticsVisitor` | I count files, source lines, test cases, and declared dependencies. I compute the mean complexity per source file and divide test files by source files. I use zero for both calculations when the project has no source files. |
| `QualityGateVisitor` | I require zero critical security issues, average source complexity of at most 10, and a test-to-source file ratio of at least 0.40. I reuse the audit and statistics logic. |
| `ComplianceVisitor` | I check for credentials, missing dependency versions, and the prohibited dependency `legacy-crypto`. I require each production module to have a test file with at least one test case. |
| `TechnicalDebtVisitor` | I flag source files over 200 lines or above complexity 10. I estimate one hour for a long file and two hours for high complexity. I add both estimates when both rules apply. |

In my demo, the quality gate fails because the configuration is insecure. The complexity and test-ratio checks pass. My compliance report also flags the `users` module because I gave it no tests.

## How I used the Visitor pattern

### 1. What problem do I solve with Visitor?

I use Visitor to run different inspections on the same project structure. Each inspection keeps its own rules and builds its own report.

### 2. Where did I put Element and Visitor?

I put the `ProjectElement` interface and its five implementations in `inspection.element`. I put `ProjectVisitor` and its five implementations in `inspection.visitor`. I use `Directory.accept()` to visit a folder and then forward the visitor to each child.

### 3. How does double dispatch work in my project?

When I call `element.accept(visitor)`, Java uses the actual element to choose the `accept` implementation. Inside `SourceCodeFile.accept()`, the type of `this` makes Java select the `visit(SourceCodeFile)` overload. The actual visitor object then determines which implementation of that overload runs. A security visitor checks security flags, and a statistics visitor counts lines and complexity.

### 4. Why did I keep inspection logic outside the elements?

I want my elements to represent files and folders. If I put every inspection in the elements, I would need to edit several classes each time I introduced a new report or changed how it works.

### 5. Which extension is easier for me to make?

I can add a new visitor by implementing `ProjectVisitor` and calling `accept()` with the new object. I added `TechnicalDebtVisitor` and its tests in a separate commit. When I add a new element type, I need to extend the visitor interface and update every implementation.

### 6. When would I avoid using Visitor?

I would avoid it if my element types changed frequently. Each new type would make me update every visitor. I would also choose a simpler design if I only needed one small operation.

### 7. How would I choose between Visitor and Strategy?

I use Visitor when one operation needs to handle several kinds of objects in a tree. I would use Strategy if I wanted a class to switch between algorithms for the same job, such as two ways to score the same security report.

### 8. Which SOLID principles did I apply?

I used the Single Responsibility Principle by separating the file data from the inspection rules. I applied the Open/Closed Principle when I added a new inspection while keeping the existing element classes intact. My elements also depend on the `ProjectVisitor` interface, which lets me use any implementation with the same tree.

## How I would add DatabaseMigrationFile

I would implement `ProjectElement` in a new `DatabaseMigrationFile` record and call `visitor.visit(this)` in its `accept()` method. I would add `visit(DatabaseMigrationFile file)` to `ProjectVisitor`. Then I would implement it in all five visitors and in my test `RecordingVisitor`. I would decide which visitors need migration-specific behavior, such as counting SQL files in the statistics or checking destructive statements in the audit. If I reused those rules in the quality gate, I would forward the migration to its internal visitors too.

I can add a migration to a directory through its existing `List<ProjectElement>`. The `ProjectElement` interface, `Directory`, and the existing file classes already support that tree structure.

## How I documented the design

I used a [class diagram](uml/class-diagram.puml) to show the element and visitor interfaces, the five implementations of each, and the relationships used during inspection. I used a [sequence diagram](uml/sequence-diagram.puml) to show the security audit reaching `PaymentService.java` inside the nested `src` directory.
