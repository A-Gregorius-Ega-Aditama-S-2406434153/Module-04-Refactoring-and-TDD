# Submission-Tutorial-Modul-1

### Gregorius Ega Aditama Sudjali
### NPM: 2406434153

## Reflection 1:

```text
You already implemented two new features using Spring Boot. Check again your source code
and evaluate the coding standards that you have learned in this module. Write clean code
principles and secure coding practices that have been applied to your code. If you find any
mistake in your source code, please explain how to improve your code. Please write your
reflection inside the repository's README.md file.
```

## Answer reflection 1:
```text
I reviewed my source code by following the instruction and evaluated it based on the coding standards from this module, and overall
I think my structure is fairly clean because I separate responsibilities across layers: my `ProductController` focuses
on handling HTTP requests and preparing data for the view, my `ProductService` (interface + implementation) acts as the 
business layer, and my `ProductRepository` manages the data storage. I also apply the clean code principle of 
programming to an interface by having the controller depend on `ProductService` instead of the implementation 
class, which reduces coupling and makes the code easier to test or change later. In addition, 
I use clear, consistent method names such as `create()` and `findAll()` so the intent of each method is easy 
to understand, and I follow Spring conventions by using annotations like `@Controller`,
`@Service`, and `@Repository` to make each component’s role explicit. For secure coding practices, I use `POST` for 
creating products rather than `GET`, which aligns with safe HTTP semantics for state-changing operations, and I use 
the redirect-after-post pattern (`redirect:list`) to prevent duplicate submissions when users refresh the page.
I also avoid risky patterns such as building SQL queries or running commands using user input in these classes, which 
reduces the likelihood of injection-style vulnerabilities at this stage. However, I did find 
a mistake in my code: I named some files incorrectly, which breaks Java conventions and can reduce readability and 
maintainability (for example, in Java the public class name must match the filename exactly). To improve this, I should
rename files so every public class matches its filename (e.g., `ProductServiceImpl.java` must contain `public class
ProductServiceImpl`, and `ProductController.java` must contain `public class ProductController`) and keep 
naming consistent across packages. Beyond naming, I can further improve my code by switching from field
injection (`@Autowired` on fields) to constructor injection for better immutability and testing, adding
input validation (for example using `@Valid` and validation annotations or using a DTO instead of 
binding directly to the entity), and improving the repository API so it returns a `List<Product>` directly 
instead of an `Iterator<Product>` to avoid extra conversion logic in the service layer.
```
## Reflection 2:
```text
After writing the unit test, how do you feel? How many unit tests should be made in a
class? How to make sure that our unit tests are enough to verify our program? It would be
good if you learned about code coverage. Code coverage is a metric that can help you
understand how much of your source is tested. If you have 100% code coverage, does
that mean your code has no bugs or errors?
2. Suppose that after writing the CreateProductFunctionalTest.java along with the
corresponding test case, you were asked to create another functional test suite that
verifies the number of items in the product list. You decided to create a new Java class
similar to the prior functional test suites with the same setup procedures and instance
variables.
What do you think about the cleanliness of the code of the new functional test suite? Will
the new code reduce the code quality? Identify the potential clean code issues, explain
the reasons, and suggest possible improvements to make the code cleaner! Please write
your reflection inside the repository's README.md file.
```

## Answer reflection 2:
```text
1. Unit testing confidence, coverage, and limitations

After writing unit tests, I feel more confident about the correctness of the core logic because tests help
verify expected behavior under controlled conditions. However, I still remain cautious, since unit tests can
never prove the absence of bugs they only reduce uncertainty. There is no fixed or “correct” number of unit
tests per class; instead, the goal is to have sufficient tests that cover important behaviors, decision branches,
and edge cases without becoming redundant or overly coupled to internal implementation details. To judge
whether tests are sufficient, I focus on critical execution paths, boundary conditions  and error handling
scenarios. I also use code coverage tools as a guideline to identify which lines and branches have not been exercised.
That said, coverage metrics must be interpreted carefully: achieving 100% coverage does not guarantee the
program is bug free. Coverage only indicates that code was executed during tests, not that the assertions
were meaningful or correct. Bugs may still exist due to missing or weak assertions, incorrect assumptions
in test expectations, race conditions in concurrent code, or issues that only appear when
components interact in real environments. Because of this, unit testing should be complemented
with other testing levels such as integration tests, system tests, and exploratory testing, especially
for complex logic or state-dependent behavior.

2. Test duplication, maintainability, and test design improvements Creating another functional test suite with the
same setup logic and instance variables can easily introduce code duplication and reduce clarity. Repeating driver
initialization, base URL construction, timeouts, and helper utilities often leads to copy-paste code, which makes changes
error prone and increases the risk of inconsistent behavior across test classes. This negatively impacts code cleanliness by
violating the DRY (Don’t Repeat Yourself) principle and makes long-term maintenance harder.A better approach is to extract
shared setup and teardown logic into a base test class or a reusable JUnit 5 extension, allowing common configuration to be
defined in one place. Shared helper methods for example, for waiting strategies, navigation, or URL construction can
further improve consistency and readability. This also makes updates easier, since changes only need to be applied
once instead of across multiple test files. Another important improvement is applying the Page Object Model (POM).
By centralizing UI element locators and user interactions into dedicated page classes, test casesbecome shorter,
more expressive, and easier to understand. This separation of concerns makes tests more resilient to UI changes,
ince updates to element selectors or interaction logic are confined to page objects rather than scattered throughout test code.
Overall, these practices lead to cleaner, more maintainable, and more scalable test suites.
```

## Reflection 3:
```text
1. List the code quality issue(s) that you fixed during the exercise and explain your strategy
on fixing them.
2. Look at your CI/CD workflows (GitHub)/pipelines (GitLab). Do you think the current
implementation has met the definition of Continuous Integration and Continuous
Deployment? Explain the reasons (minimum 3 sentence)!
```

## Answer reflection 3:
```text
1. Code quality issues fixed and strategy

During this exercise, I fixed several code quality and security-related issues identified by static analysis and workflow review. 
First, I resolved an unused field issue in HomePageFunctionalTest by using WebDriverWait with ExpectedConditions, so the test 
became more stable and the existing wait dependency was used correctly. Second, I refactored ProductServiceImpl from field injection 
to constructor injection, made the dependency final, and added a null check to improve immutability, explicit dependencies, and testability. 
Third, in ci.yml I replaced gradle/actions/setup-gradle@v3 with a full commit SHA to reduce supply-chain risk from mutable tags. 
Fourth, in build.gradle.kts I removed hardcoded dependency versions where Spring dependency management already provides managed versions. 
Finally, I enabled Gradle dependency verification by adding gradle/verification-metadata.xml and gradle/verification-keyring.keys. 

My strategy was to prioritize issues by risk and maintainability: fix security-sensitive configuration first, then structural code quality issues, 
then consistency/build hygiene issues. I kept each change minimal and localized, aligned with existing project patterns, and verified changes by 
running Gradle tests after each significant update. I also validated each fix with Sonar results so quality improvements were measurable, and the 
project quality status improved from E to AD with no remaining issues.

2. CI/CD evaluation

The current implementation has met the definition of Continuous Integration because every push and pull request triggers automated build and test execution, 
and SonarCloud analysis is also executed to keep code quality checks continuous. In addition, this project already has Continuous Deployment through Koyeb 
using the Dockerfile in the root directory, where deployment is handled as a containerized release pipeline. After code changes are integrated, the deployment 
is propagated to the running application environment on Koyeb, so updates are delivered continuously without manual server-side steps in this repository. 
This setup gives a clear CI to CD flow: code integration, automated verification, container build, and live deployment to one running service. 
Using Docker also helps keep runtime behavior consistent between local build and cloud deployment because the same container definition is used. 
To strengthen this further, it is still good to keep branch protection and post-deploy smoke checks so each automatic release is both fast and safe. 
The deployed application is accessible at: https://determined-allissa-a-gregorius-ega-sditama-s-240643415-86f9fa9c.koyeb.app/product/list
```

## Reflection 4:
```text
Apply the SOLID principles you have learned. You are allowed to modify the source code
according to the principles you want to implement. Please answer the following questions:
1) Explain what principles you apply to your project!
2) Explain the advantages of applying SOLID principles to your project with examples.
3) Explain the disadvantages of not applying SOLID principles to your project with examples.
Please write the answer in the README.md file.
```

## Answer reflection 4:
```text
1) SOLID principles applied in this project

I applied at least three SOLID principles in the Car feature:

- SRP (Single Responsibility Principle):
  Each layer now has one clear responsibility. `CarController` handles HTTP requests/response flow, `CarServiceImpl`
  handles business rules (validation + ID generation + orchestration), and `InMemoryCarRepository` handles in-memory
  persistence operations only.

- DIP (Dependency Inversion Principle):
  High-level modules depend on abstractions instead of concrete classes. `CarController` depends on `CarService`
  (interface), and `CarServiceImpl` depends on `CarRepository` (interface), not concrete implementations.

- OCP (Open/Closed Principle):
  By introducing `CarRepository` interface and `InMemoryCarRepository`, the service/controller can stay unchanged
  if later we add another repository implementation (for example file-based or database-backed storage).

I also improved maintainability by using constructor injection instead of field injection in the refactored classes.

2) Advantages of applying SOLID principles (with examples)

- Easier maintenance:
  Because responsibilities are separated, changes are localized. Example: if car validation rules change, we update
  `CarServiceImpl` without touching repository storage logic or controller route handling.

- Better testability:
  DIP makes mocking straightforward. Example: `CarServiceImplTest` mocks `CarRepository`, and `CarControllerTest`
  mocks `CarService`, so each class can be tested in isolation with focused assertions.

- Lower coupling and safer evolution:
  Example: replacing `InMemoryCarRepository` with another implementation would not require changing controller/service
  code that already depends on `CarRepository` abstraction.

- Clearer contracts and safer behavior:
  Using `Optional` for not-found cases in car flow avoids null ambiguity and makes not-found handling explicit.
  This reduces hidden NullPointerException risks and encourages explicit control flow in the controller.

3) Disadvantages of not applying SOLID principles (with examples)

- Tight coupling makes changes expensive:
  If `CarController` directly depends on `CarServiceImpl`, a service implementation change can cascade into controller
  updates and brittle tests.

- Mixed responsibilities increase bug risk:
  If repository and business rules are mixed, storage changes can accidentally break validation logic. Example:
  combining ID generation, input validation, and data persistence in one class makes regressions more likely.

- Harder to test and debug:
  Without abstractions, tests require real dependencies and become slower/more fragile. Example: controller tests would
  be harder to isolate without mocking a `CarService` interface.

- Less extensible design:
  Without OCP through interfaces, adding new storage mechanisms requires modifying existing working code instead of
  extending with a new implementation, increasing regression risk.

In summary, applying SRP, DIP, and OCP in this project made the code easier to reason about, easier to test,
and safer to evolve while keeping existing user-facing behavior intact.
```

## Reflection 5:
```text
You have followed the Test-Driven Development workflow in the Exercise. Now answer these questions:

1. Reflect based on Percival (2017) proposed self-reflective questions (in “Principles and Best Practice of Testing” submodule, chapter “Evaluating Your Testing Objectives”), whether this TDD flow is useful enough for you or not. If not, explain things that you need to do next time you make more tests.
2. You have created unit tests in Tutorial. Now reflect whether your tests have successfully followed F.I.R.S.T. principle or not. If not, explain things that you need to do the next time you create more tests.

Please write your reflection inside the repository's README.md file.
```

## Answer reflection 5:
The Test-Driven Development (TDD) flow in this exercise is useful for me because it pushes me to define expected behavior before writing implementation code. When I start from tests, I think earlier about both normal and failure scenarios, so my implementation becomes more focused and less speculative. The red-green-refactor cycle also gives fast feedback, which reduces the chance of large hidden mistakes. During this module, I felt that TDD helped me move with more confidence because I could verify behavior incrementally instead of relying on manual checks after many changes were already done.

At the same time, I still need to improve how I apply TDD in practice. In some cases I wrote setup data that was bigger than necessary, which made tests harder to read and maintain. Next time, I should keep each test case narrower, prepare only the minimum required test data, and make test names more explicit about behavior. I also need to be stricter with the full cycle: write a failing test first, implement the smallest change needed to pass it, and then refactor for clarity. Doing that consistently will make my tests cleaner and make the development flow more predictable.

From the F.I.R.S.T. perspective, my tests are mostly fast and repeatable because they use isolated unit-level scope and deterministic inputs. They are also self-validating since assertions clearly indicate pass or fail without manual interpretation. For independence, I have improved through setup/reset patterns, but I can still reduce coupling by avoiding unnecessary shared fixtures and over-stubbing. For timeliness, I generally followed writing tests before implementation in the tutorial, but I should apply that discipline more consistently for every edge case and negative path. Overall, my current tests are on the right track, and my next goal is to make them leaner, more behavior-focused, and more consistently aligned with all F.I.R.S.T. principles.
