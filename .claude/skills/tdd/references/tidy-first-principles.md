# Tidy First Principles

## Two Types of Changes

### Structural Changes
**Rearranging code without changing behavior**

Examples:
- Renaming variables, methods, classes
- Extracting methods
- Moving code to different files
- Reorganizing packages
- Improving formatting

**Rule:** Always verify with tests before and after

```bash
# Before structural change
./gradlew test

# Make structural change (e.g., rename method)

# After structural change
./gradlew test  # Must still pass
```

### Behavioral Changes
**Adding or modifying functionality**

Examples:
- New features
- Bug fixes
- Changing business logic
- Adding validation

**Rule:** Requires new/modified tests

```kotlin
// Behavioral: Add email validation
Given("이미 가입된 이메일 주소를 사용하여 가입을 시도할 때") {
    When("가입 요청을 보내면") {
        Then("이미 등록된 이메일이므로 가입이 거절된다") {
            // New test for new behavior
        }
    }
}
```

## Critical Rule: Never Mix

**❌ WRONG:**
- Rename method AND add validation in same change
- Refactor structure while adding new feature

**✅ CORRECT:**
- Do structural changes first, verify tests still pass
- Then do behavioral changes separately

## Workflow

### 1. Structural First

When both changes are needed:

```
1. Run tests (ensure green)
2. Make structural changes
3. Run tests (ensure still green)
4. Make behavioral changes
5. Run tests (ensure green with new tests)
```

### 2. Verify with Tests

**Structural changes:**
- Tests must pass before and after
- No new tests needed
- No test modifications needed

**Behavioral changes:**
- New tests required
- Existing tests may need updates
- Tests must pass after change

## Examples

### Structural Change Example

```bash
# 1. Ensure tests pass
./gradlew test

# 2. Make structural change
# Rename: updateUsername() → changeUsername()

# 3. Verify tests still pass
./gradlew test
```

### Behavioral Change Example

```bash
# 1. Write failing test (RED)
# Add test for email uniqueness

# 2. Make it pass (GREEN)
# Implement duplicate email check

# 3. Refactor (REFACTOR)
# Clean up implementation

# 4. Verify all tests pass
./gradlew test
```
