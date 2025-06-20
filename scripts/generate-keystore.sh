#!/bin/bash

# Script to generate a release keystore for Android app signing
# This keystore will be used for signing release APKs

echo "🔐 Android Release Keystore Generator"
echo "====================================="
echo ""

# Set default values
KEYSTORE_NAME="release-key.jks"
ALIAS_NAME="release-key"
VALIDITY_DAYS=10000

echo "This script will generate a keystore for signing your Android app releases."
echo "⚠️  IMPORTANT: Keep the keystore and passwords secure! If lost, you cannot update your app on app stores."
echo ""

# Get keystore details
read -p "Enter keystore filename (default: $KEYSTORE_NAME): " input_keystore
KEYSTORE_NAME=${input_keystore:-$KEYSTORE_NAME}

read -p "Enter key alias (default: $ALIAS_NAME): " input_alias
ALIAS_NAME=${input_alias:-$ALIAS_NAME}

echo ""
echo "Now you'll be prompted for passwords and certificate information..."
echo ""

# Generate the keystore
keytool -genkey -v -keystore "$KEYSTORE_NAME" -alias "$ALIAS_NAME" -keyalg RSA -keysize 2048 -validity $VALIDITY_DAYS

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Keystore generated successfully!"
    echo ""
    echo "📋 Next steps:"
    echo "1. Keep '$KEYSTORE_NAME' file secure - do NOT commit it to version control"
    echo "2. Add the following secrets to your GitHub repository:"
    echo "   - RELEASE_KEYSTORE: Base64 encoded keystore file"
    echo "   - RELEASE_KEYSTORE_PASSWORD: Keystore password"
    echo "   - RELEASE_KEY_ALIAS: Key alias (probably '$ALIAS_NAME')"
    echo "   - RELEASE_KEY_PASSWORD: Key password"
    echo ""
    echo "🔍 To get the base64 encoded keystore for GitHub secrets:"
    echo "   base64 -i '$KEYSTORE_NAME' | tr -d '\n' | pbcopy"
    echo "   (This copies the base64 string to clipboard on macOS)"
    echo ""
    echo "   Or on Linux:"
    echo "   base64 -w 0 '$KEYSTORE_NAME'"
    echo ""
    echo "📝 Keep this information in a secure password manager:"
    echo "   - Keystore file: $KEYSTORE_NAME"
    echo "   - Key alias: $ALIAS_NAME"
    echo "   - Keystore password: [the password you entered]"
    echo "   - Key password: [the password you entered]"
    echo ""
else
    echo "❌ Failed to generate keystore"
    exit 1
fi