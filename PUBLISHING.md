# Publishing Guide

Quick reference for publishing Firebase SDKs to GitHub Packages.

## Publishing Options

### 1. Publish Both SDKs (Same Version)

When both SDKs should share the same version number:

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

**Publishes:**
- `com.dallaslabs.sdk:firebase-analytics:1.0.0`
- `com.dallaslabs.sdk:firebase-notifications:1.0.0`

**Workflow:** `.github/workflows/publish-all.yml`

---

### 2. Publish Analytics Only

To release only the Analytics SDK:

```bash
git tag -a analytics-v1.0.1 -m "Analytics SDK v1.0.1"
git push origin analytics-v1.0.1
```

**Publishes:**
- `com.dallaslabs.sdk:firebase-analytics:1.0.1`

**Workflow:** `.github/workflows/publish-analytics.yml`

---

### 3. Publish Notifications Only

To release only the Notifications SDK:

```bash
git tag -a notifications-v1.0.2 -m "Notifications SDK v1.0.2"
git push origin notifications-v1.0.2
```

**Publishes:**
- `com.dallaslabs.sdk:firebase-notifications:1.0.2`

**Workflow:** `.github/workflows/publish-notifications.yml`

---

## Tag Naming Convention

| Tag Pattern | Workflow | Result |
|-------------|----------|--------|
| `v*` | `publish-all.yml` | Both SDKs at same version |
| `analytics-v*` | `publish-analytics.yml` | Analytics SDK only |
| `notifications-v*` | `publish-notifications.yml` | Notifications SDK only |

## Examples

```bash
# Release both SDKs at version 2.0.0
git tag -a v2.0.0 -m "Release 2.0.0"
git push origin v2.0.0

# Update only analytics to 2.1.0
git tag -a analytics-v2.1.0 -m "Analytics 2.1.0 - Bug fixes"
git push origin analytics-v2.1.0

# Update only notifications to 1.5.3
git tag -a notifications-v1.5.3 -m "Notifications 1.5.3 - New features"
git push origin notifications-v1.5.3
```

## Publishing Process

When you push a tag, GitHub Actions automatically:

1. ✓ Checks out the code
2. ✓ Sets up JDK 17
3. ✓ Extracts version from tag
4. ✓ Builds SDK(s)
5. ✓ Publishes to GitHub Packages

**Repository:** `https://maven.pkg.github.com/erikg84/firebase-analytics`

## Verifying Publication

After pushing a tag, you can verify the publication at:

https://github.com/erikg84/firebase-analytics/packages

## Troubleshooting

### Build Failed

1. Check the Actions tab: https://github.com/erikg84/firebase-analytics/actions
2. Review build logs for the failed workflow
3. Ensure all tests pass locally: `./gradlew test`

### Wrong Version Published

Delete the tag and recreate:

```bash
# Delete local tag
git tag -d v1.0.0

# Delete remote tag
git push origin :refs/tags/v1.0.0

# Create new tag
git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin v1.0.1
```

### Package Already Exists

GitHub Packages doesn't allow overwriting published versions. You must:
1. Delete the package version from GitHub Packages UI
2. Or publish a new version with a different version number

## Pre-Release Testing

Before publishing, test locally with Maven Local:

```bash
# Publish to local Maven repository
./gradlew publishToMavenLocal

# In your app's settings.gradle.kts, add:
repositories {
    mavenLocal()
}

# Then use the dependency
dependencies {
    implementation("com.dallaslabs.sdk:firebase-analytics:1.0.0-SNAPSHOT")
}
```

## Version Strategy Recommendations

### Synchronized Versioning (Recommended for most cases)
- Use `v*` tags to keep both SDKs in sync
- Simpler dependency management for consumers
- Clear versioning story

### Independent Versioning
- Use `analytics-v*` or `notifications-v*` for independent releases
- Useful when one SDK has a critical bugfix
- Allows faster iteration on individual SDKs
- More complex dependency management for consumers

---

**Last Updated:** January 22, 2026
