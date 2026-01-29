# Contributing to GPT Tennis Coach

Thank you for your interest in contributing to GPT Tennis Coach! This document provides guidelines and instructions for contributing to this project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Coding Standards](#coding-standards)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Reporting Issues](#reporting-issues)
- [Feature Requests](#feature-requests)

## Code of Conduct

Be respectful and constructive in all interactions with other contributors. We are committed to providing a welcoming and inclusive environment for everyone.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** to your local machine:
   ```bash
   git clone https://github.com/yourusername/gpt-tennis-coach.git
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/originalowner/gpt-tennis-coach.git
   ```
4. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Setup

### Prerequisites

- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK 28+
- Git

### Initial Setup

1. Open the project in Android Studio
2. Let Gradle sync automatically
3. Configure your local backend URL in `app/src/main/java/com/aaa/gptenniscoach/di/AppModule.kt`
4. Build and run the project on an emulator or device

## Coding Standards

### Kotlin Style Guide

- Follow [Google's Kotlin style guide](https://developer.android.com/kotlin/style-guide)
- Use 4-space indentation
- Keep lines under 120 characters where possible
- Use meaningful variable and function names

### Architecture

The project follows MVVM (Model-View-ViewModel) pattern:

- **Models**: Data classes in `data/` layer representing API responses and database entities
- **Views**: Composable functions in `feature/` screens using Jetpack Compose
- **ViewModels**: State management in `feature/` packages extending `ViewModel`
- **Repositories**: Data access abstraction in `data/repo/`

### Naming Conventions

- **Files**: PascalCase for class files (e.g., `MainActivity.kt`)
- **Classes**: PascalCase (e.g., `CaptureViewModel`, `SessionEntity`)
- **Functions**: camelCase (e.g., `updateLevel()`, `onVideoCaptured()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `BASE_URL`, `TAG_VIDEO`)
- **Variables**: camelCase (e.g., `currentState`, `isLoading`)

### Documentation

- Add KDoc-style comments for all public functions and classes
- Use brief, descriptive comments explaining the purpose
- Example:
  ```kotlin
  /**
   * Copies video file from content URI to app-internal storage.
   */
  fun copyToAppStorage(context: Context, uri: Uri): File
  ```

### Imports

- Use IDE auto-import functionality
- Remove unused imports before committing
- Organize imports alphabetically

### Null Safety

- Use nullable types (`?`) explicitly
- Prefer non-null types with proper initialization
- Use scope functions (`let`, `run`, `apply`) for safe null handling
- Avoid `!!` operator; use `checkNotNull()` or Elvis operator `?:`

## Commit Guidelines

### Commit Message Format

Follow conventional commit format:

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types

- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, missing semicolons, etc.)
- `refactor`: Code refactoring without feature changes
- `perf`: Performance improvements
- `test`: Adding or updating tests
- `chore`: Build process, dependencies, or tooling changes

### Examples

```
feat(capture): add camera orientation handling

docs(readme): update installation instructions

fix(results): resolve skeleton overlay rendering issue

refactor(api): simplify error handling in TennisCoachApi
```

### Best Practices

- Write commits in imperative mood ("Add feature" not "Added feature")
- Each commit should be a logically independent unit
- Keep commits small and focused
- Don't commit broken or incomplete code

## Pull Request Process

### Before Submitting

1. **Update your branch** with latest upstream changes:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Ensure code quality**:
   - Run unit tests: `./gradlew test`
   - Run instrumented tests: `./gradlew connectedAndroidTest`
   - Build the project: `./gradlew build`

3. **Check code style**:
   - Use Android Studio's code inspection tools
   - Format code: `Ctrl+Alt+L` (or `Cmd+Alt+L` on Mac)

### PR Description Template

```markdown
## Description
Brief description of changes and why they're needed.

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
Describe testing performed:
- Devices/emulators tested
- Test scenarios covered
- Any edge cases considered

## Screenshots
If applicable, add UI screenshots showing the changes.

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review of own code completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] No new warnings generated
```

### PR Guidelines

- Keep PRs focused on a single feature or fix
- Reference related issues using `#issue_number`
- Respond to review feedback promptly
- Request re-review after making changes
- Maintain a respectful tone in discussions

## Reporting Issues

### Bug Reports

When reporting a bug, include:

1. **Description**: Clear summary of the issue
2. **Steps to Reproduce**: Exact steps to trigger the bug
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Environment**:
   - Android version
   - Device model (or emulator)
   - App version
6. **Logs**: Relevant error messages or stack traces
7. **Screenshots**: Visual evidence if applicable

### Issue Template

```markdown
## Bug Report

### Description
[Describe the bug clearly]

### Steps to Reproduce
1. [First step]
2. [Second step]
3. [And so on...]

### Expected Behavior
[What should happen]

### Actual Behavior
[What actually happens]

### Environment
- Android Version: [e.g., Android 12]
- Device: [e.g., Pixel 6]
- App Version: [e.g., 1.0.0]

### Logs
[Relevant error messages or logcat output]

### Screenshots
[If applicable]
```

## Feature Requests

When requesting a new feature:

1. **Title**: Clear, concise description
2. **Motivation**: Why this feature is needed
3. **Proposed Solution**: How it should work
4. **Alternative Approaches**: Other ways to solve it
5. **Additional Context**: Any other relevant information

### Feature Request Template

```markdown
## Feature Request

### Description
[Clear description of the requested feature]

### Motivation
[Why is this feature needed? What problem does it solve?]

### Proposed Solution
[How should this feature work?]

### Alternative Approaches
[Other ways this could be implemented]

### Additional Context
[Any other relevant information]
```

## Testing Guidelines

### Unit Tests

- Place in `app/src/test/java/com/aaa/gptenniscoach/`
- Name test classes as `[ClassName]Test` or `[ClassName]Tests`
- Test file structure:
  ```kotlin
  @Test
  fun functionName_givenCondition_expectedResult() {
      // Arrange
      val input = ...
      
      // Act
      val result = functionUnderTest(input)
      
      // Assert
      assertEquals(expected, result)
  }
  ```

### Instrumented Tests

- Place in `app/src/androidTest/java/com/aaa/gptenniscoach/`
- Test Android-specific components (UI, database, etc.)
- Use AndroidJUnit4 test runner

## Documentation Standards

- Update README.md if adding new features or changing setup
- Add inline comments for complex logic
- Document API changes in PR description
- Keep documentation in sync with code

## Getting Help

- Check existing issues for similar problems
- Review project documentation
- Ask questions in GitHub discussions
- Reach out to maintainers for guidance

## Recognition

Contributors will be acknowledged in:
- Pull request comments
- Project README contributors section
- Release notes for major contributions

---

Thank you for contributing to GPT Tennis Coach! Your efforts help make this project better for everyone.
