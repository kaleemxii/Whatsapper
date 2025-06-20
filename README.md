# Whatsapper

A beautiful Android app that integrates with WhatsApp to help you quickly open chats from your call history or manually entered phone numbers.

## Features

- **Call History Integration**: View your call history with contact names (when available) or phone numbers
- **One-Tap WhatsApp**: Tap any call record to open a WhatsApp chat with that number
- **Manual Number Entry**: Enter any phone number manually to start a WhatsApp conversation
- **Modern UI**: Built with Jetpack Compose and Material Design 3
- **Dark Mode Support**: Automatically adapts to system theme preferences
- **Responsive Design**: Works beautifully on phones and tablets

## Screenshots

*Screenshots coming soon*

## Permissions

The app requires the following permissions:

- **READ_CALL_LOG**: To access and display your call history
- **READ_CONTACTS**: To show contact names instead of just phone numbers
- **INTERNET**: To open WhatsApp web as a fallback option

## Technical Details

### Built With

- **Kotlin**: Modern Android development language
- **Jetpack Compose**: Modern UI toolkit for native Android
- **Material Design 3**: Latest Material Design components
- **ViewModel**: Lifecycle-aware data holder
- **StateFlow**: Reactive state management
- **Coroutines**: Asynchronous programming

### Architecture

The app follows MVVM (Model-View-ViewModel) architecture:

- **MainActivity.kt**: Main UI entry point with Compose UI
- **CallHistoryViewModel.kt**: Manages call history data and state
- **WhatsAppUtils.kt**: Utility functions for WhatsApp integration
- **AppTheme.kt**: Material Design 3 theming with dark mode support

## Installation

### Requirements

- Android 7.0 (API level 24) or higher
- WhatsApp installed on the device

### From Releases

1. Download the latest APK from the [Releases](../../releases) page
2. Install the APK on your Android device
3. Grant the required permissions when prompted

### Building from Source

1. Clone the repository:
```bash
git clone https://github.com/kaleemxii/Whatsapper.git
cd Whatsapper
```

2. Open the project in Android Studio

3. Build and run the project:
```bash
./gradlew assembleDebug
```

## Usage

1. **First Launch**: Grant permissions to access call logs and contacts
2. **View Call History**: Browse through your recent calls with contact names
3. **Open WhatsApp Chat**: Tap any call record to open WhatsApp chat
4. **Manual Entry**: Use the text field at the top to enter any phone number and start a WhatsApp conversation

## Development

### Project Structure

```
app/
├── src/main/
│   ├── java/com/example/whatsapper/
│   │   ├── MainActivity.kt
│   │   ├── CallHistoryViewModel.kt
│   │   ├── ui/theme/
│   │   │   └── AppTheme.kt
│   │   └── utils/
│   │       └── WhatsAppUtils.kt
│   ├── res/
│   │   └── values/
│   │       ├── colors.xml
│   │       ├── strings.xml
│   │       └── themes.xml
│   └── AndroidManifest.xml
└── build.gradle
```

### Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## CI/CD

The project includes automated GitHub Actions workflows:

### Continuous Integration
- Builds the project on every push and pull request
- Runs unit tests
- Generates debug and release APKs
- Uploads build artifacts

### Release Automation
- Builds signed release APKs with proper keystore
- Creates GitHub Releases automatically
- Attaches signed APKs to releases
- Supports both manual triggers and tag-based releases

For detailed setup instructions, see [CI/CD Setup Guide](docs/CICD_SETUP.md).

### Quick Release Setup
1. Generate keystore: `./scripts/generate-keystore.sh`
2. Add GitHub secrets (keystore, passwords)
3. Trigger release via GitHub Actions or push a tag

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [WhatsApp Business API](https://developers.facebook.com/docs/whatsapp) for the integration URLs
- [Material Design 3](https://m3.material.io/) for the beautiful design system
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the modern UI toolkit

## Support

If you encounter any issues or have suggestions, please [open an issue](../../issues) on GitHub.

---

**Note**: This app is not affiliated with or endorsed by WhatsApp Inc. WhatsApp is a trademark of WhatsApp Inc.