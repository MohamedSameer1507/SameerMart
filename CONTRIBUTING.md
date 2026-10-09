# Contributing to SameerMart

Thank you for contributing to the **SameerMart** e-commerce project. To maintain high code quality and consistency across releases, please follow these guidelines.

---

## 1. Branching Strategy

We follow a structured Git feature branch workflow:

- **`main`**: Production-ready branch. Every commit on `main` must pass all automated tests and build cleanly. Direct pushes to `main` are restricted.
- **`feature/<feature-name>`**: Used for introducing new features (e.g., `feature/ai-chatbot`, `feature/order-workflow`).
- **`fix/<bug-name>`**: Used for bug fixes and patches.
- **`docs/<topic>`**: Used for documentation updates.

### Creating a Feature Branch
```bash
git checkout main
git pull origin main
git checkout -b feature/my-new-feature
```

---

## 2. Commit Message Conventions

This project strictly adheres to [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/):

```
<type>(<scope>): <short imperative description>

[optional body explaining context and rationale]
```

### Allowed Types
- **`feat`**: A new feature for the user or platform.
- **`fix`**: A bug fix.
- **`docs`**: Documentation changes only.
- **`style`**: Formatting, white-space, missing semi-colons (no code behavior change).
- **`refactor`**: Code changes that neither fix a bug nor add a feature.
- **`test`**: Adding missing tests or correcting existing tests.
- **`chore`**: Build system, dependencies, or tool configurations.

### Examples
- `feat(orders): implement O2 order status workflow and F8 delivered-only reviews`
- `fix(auth): prevent session fixation on user login`
- `docs(readme): add environment variables table and mermaid architecture diagram`

---

## 3. Pre-Push Verification Checklist

Before pushing commits or opening a pull request, ensure the following commands complete with zero errors:

1. **Clean Compile & Test Execution**:
   ```bash
   mvn clean verify
   ```
   - Must output `BUILD SUCCESS`.
   - Total test count must be non-decreasing (currently 153+ tests).
   - Zero test failures and zero test errors.

2. **Security & Secrets Check**:
   - Verify no API keys, private passwords, database connection credentials, or `.env` files are tracked by Git:
     ```bash
     git status
     git diff
     ```

3. **Code Style & Practices**:
   - All SQL queries must use `PreparedStatement` with parameterized placeholders (`?`). Never concatenate raw strings into SQL queries.
   - Use try-with-resources blocks for `Connection`, `PreparedStatement`, and `ResultSet`.
   - All state-changing HTML forms (`POST`) must include the hidden `csrfToken` parameter.

---

## 4. Pull Request (PR) Template

When opening a Pull Request, please copy and fill out the template below:

```markdown
### Summary of Changes
- Briefly describe the purpose and scope of this change.

### Feature / Module Addressed
- [ ] F1: User Authentication & Role Management
- [ ] F2: Product Catalog & Browsing
- [ ] F3: Seller Inventory Management
- [ ] F4: Shopping Cart Operations
- [ ] F5: Order Processing & Transactions
- [ ] F6: Seller Fulfillment & Isolation
- [ ] F7: Platform Administration & Metrics
- [ ] F8: Product Reviews & Verification
- [ ] Other: (Observability, AI Chatbot, etc.)

### Verification Steps
- [ ] Ran `mvn clean verify` — 153 tests passed with 0 failures.
- [ ] Verified locally in browser / mock container.
- [ ] Verified CSRF tokens in all modified forms.
- [ ] Verified no credentials/secrets are committed.

### Screenshots / Logs (if applicable)
[Paste screenshots or log outputs here]
```

---

## 5. Merging Policy

- Pull requests are reviewed and merged into `main` using **no-fast-forward merge** (`git merge --no-ff`) to preserve branch history and audit trails.
- Delete feature branches post-merge to keep repository clean.
