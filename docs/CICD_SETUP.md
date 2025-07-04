# CI/CD Release Setup Guide

This guide explains how to set up the automated CI/CD pipeline for building, signing, and publishing release APKs.

## Overview

The project includes a GitHub Actions workflow that automatically:
- Builds a signed release APK
- Creates a GitHub Release
- Attaches the signed APK to the release

## Setup Instructions

### 1. Generate Release Keystore

First, you need to generate a keystore for signing your release APKs:

```bash
# Make the script executable (if not already)
chmod +x scripts/generate-keystore.sh

# Run the keystore generation script
./scripts/generate-keystore.sh
```

The script will:
- Generate a release keystore file (`release-key.jks`)
- Prompt for keystore and key passwords
- Prompt for certificate information (name, organization, etc.)
- Provide instructions for next steps

**⚠️ Important:** 
- Keep the keystore file secure and never commit it to version control
- Store the passwords in a secure password manager
- If you lose the keystore, you cannot update your app on app stores

### 2. Add GitHub Secrets

Add the following secrets to your GitHub repository (Settings → Secrets and variables → Actions):

| Secret Name | Description | How to get |
|-------------|-------------|------------|
| `RELEASE_KEYSTORE` | Base64 encoded keystore file | `base64 -w 0 release-key.jks` |
| `RELEASE_KEYSTORE_PASSWORD` | Password for the keystore | The password you entered during keystore generation |
| `RELEASE_KEY_ALIAS` | Key alias name | Default is `release-key` or what you specified |
| `RELEASE_KEY_PASSWORD` | Password for the key | The key password you entered |

To get the base64 encoded keystore:
```bash
# On Linux/WSL
base64 -w 0 release-key.jks

# On macOS (copies to clipboard)
base64 -i release-key.jks | tr -d '\n' | pbcopy
```

### 3. Trigger Release Build

You can trigger a release build in two ways:

#### Method 1: Manual Workflow Dispatch
1. Go to your repository on GitHub
2. Navigate to "Actions" tab
3. Select "Build and Release APK" workflow
4. Click "Run workflow"
5. Enter tag name (e.g., `v1.0.0`) and release name
6. Click "Run workflow"

#### Method 2: Push Git Tag
```bash
# Create and push a version tag
git tag v1.0.0
git push origin v1.0.0
```

## Workflow Features

The release workflow (`release-apk.yml`) includes:

- **Secure keystore handling**: Decodes keystore from secrets and cleans up after build
- **APK building**: Builds unsigned release APK using Gradle
- **APK signing**: Signs APK using jarsigner with proper algorithms and verifies signature
- **Automatic releases**: Creates GitHub releases with the signed APK attached using modern actions
- **Artifact upload**: Saves the signed APK as a build artifact with 30-day retention
- **Clean build environment**: Uses latest Ubuntu with Java 17 and Android SDK
- **Enhanced debugging**: Provides detailed output for troubleshooting APK and release issues
- **Proper permissions**: Includes necessary GitHub Actions permissions for release creation

## Security Best Practices

✅ **What the workflow does for security:**
- Keystore is base64 encoded in secrets (not plain text)
- Keystore file is cleaned up after build
- Environment variables are used for sensitive data
- No secrets are exposed in logs

⚠️ **What you must do:**
- Never commit keystore files to version control
- Use strong passwords for keystore and keys
- Store keystore and passwords in secure password manager
- Regularly rotate keys if possible
- Limit access to GitHub repository secrets

## Troubleshooting

### Build fails with "Keystore not found"
- Check that `RELEASE_KEYSTORE` secret is properly base64 encoded
- Verify the secret name matches exactly (case-sensitive)

### Build fails with "Wrong password"
- Verify `RELEASE_KEYSTORE_PASSWORD` and `RELEASE_KEY_PASSWORD` are correct
- Check for extra spaces or characters in the secrets

### APK not signed properly
- Ensure all four secrets are configured correctly
- Check that the keystore was generated properly
- Verify the key alias matches the secret value

### No releases created
- Check that you have proper permissions to create releases
- The workflow now includes `permissions: contents: write` which should resolve most permission issues
- Ensure you're not creating duplicate tags
- If using a fork, you may need to use a Personal Access Token (PAT) instead of `GITHUB_TOKEN`

### "Resource not accessible by integration" error
- This error was fixed by updating to modern GitHub Actions (`softprops/action-gh-release@v1`)
- The workflow now includes proper permissions (`contents: write`)
- If the error persists, check that the repository allows Actions to create releases

### APK not found errors
- The workflow now includes enhanced debugging output to show all files in the release directory
- After signing, the APK is renamed from `app-release-unsigned.apk` to `app-release.apk`
- Signature verification is performed to ensure the APK is properly signed

## Updating Keystore

If you need to update your keystore:

1. Generate a new keystore using the script
2. Update the GitHub secrets with new values
3. **Note**: Apps signed with different keystores cannot be updated - they're considered different apps

## Local Testing

To test release builds locally:

```bash
# Build unsigned release APK
./gradlew assembleRelease

# The unsigned APK will be at:
ls -la app/build/outputs/apk/release/app-release-unsigned.apk

# To test signing manually (for verification):
# 1. Create a test keystore
keytool -genkey -v -keystore test-keystore.jks -alias test-key -keyalg RSA -keysize 2048 -validity 365

# 2. Sign the APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore test-keystore.jks \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  test-key

# 3. Verify signing
jarsigner -verify app/build/outputs/apk/release/app-release-unsigned.apk
```

## Support

If you encounter issues with the CI/CD setup:
1. Check the Actions tab for build logs
2. Verify all secrets are configured correctly
3. Ensure the keystore was generated properly
4. Test local builds first to isolate issues