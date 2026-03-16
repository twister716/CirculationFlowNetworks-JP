# Stonecutter Cross-Version Template

This folder is a minimal multi-version mod workspace template extracted from the main repository.

Included versions:

- 1.12.2
- 1.20.1
- 1.21.1

Template rules:

- Each version only keeps a main mod class and version-specific dependency/config files.
- Shared build logic stays in the template root.
- No current mod implementation code is included.

Before using this template:

1. Rename `mod_id`, `mod_name`, `root_package`, and `mod_version` in `gradle.properties`.
2. Rename the example main classes under each version package.
3. Add dependencies in each version `dependencies.gradle` as needed.
4. Add shared code under `src/main/java` only when it is truly cross-version.

Notes:

- The template is configured for Java 21 on 1.12.2 and Java 25 on 1.20.1 / 1.21.1.