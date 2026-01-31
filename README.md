# GPT Tennis Coach - AI-Powered Tennis Analysis

An intelligent Android application that uses computer vision and AI to analyze tennis swings and provide real-time coaching feedback.

## Features

- **Video Capture & Analysis**: Record tennis swings and send them to the backend AI for analysis
- **Real-Time Feedback**: Receive detailed coaching feedback including focus points, drills, and safety notes
- **Skeleton Overlay**: Visualize pose landmarks and bone connections overlaid on video playback
- **Quality Metrics**: Track swing quality scores with detailed metrics and warnings
- **Session History**: Access and review previous analysis sessions
- **Multi-Language Support**: Analysis feedback available in multiple languages (English, Russian, French, German, Spanish, Italian, Turkish, Ukrainian, Azerbaijani, Georgian)
- **Player Metadata**: Customize analysis based on skill level, hand dominance, specific goals, and detail preferences

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Hilt Dependency Injection
- **Database**: Room Database
- **Networking**: Retrofit2 with OkHttp3
- **Media Handling**: ExoPlayer for video playback
- **Video Processing**: MediaCodec for efficient video handling
- **Serialization**: Kotlinx Serialization

## Project Structure

```
GPTennisCoach/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/aaa/gptenniscoach/
│   │   │   │   ├── MainActivity.kt              # App entry point
│   │   │   │   ├── TennisCoachApp.kt           # Hilt app class
│   │   │   │   ├── core/                       # Utility classes
│   │   │   │   │   ├── VideoFiles.kt
│   │   │   │   │   ├── Time.kt
│   │   │   │   │   ├── Result.kt
│   │   │   │   │   ├── Json.kt
│   │   │   │   │   └── overlay/                # Skeleton visualization
│   │   │   │   ├── nav/                        # Navigation
│   │   │   │   │   ├── AppNavGraph.kt
│   │   │   │   │   └── Destinations.kt
│   │   │   │   ├── ui/                         # UI components
│   │   │   │   │   └── theme/Theme.kt
│   │   │   │   ├── di/                         # Dependency Injection
│   │   │   │   │   └── AppModule.kt
│   │   │   │   ├── data/                       # Data layer
│   │   │   │   │   ├── api/                    # Retrofit API
│   │   │   │   │   ├── db/                     # Room Database
│   │   │   │   │   └── repo/                   # Repository pattern
│   │   │   │   └── feature/                    # Features
│   │   │   │       ├── capture/                # Video capture screen
│   │   │   │       ├── analyze/                # Analysis progress screen
│   │   │   │       ├── results/                # Results & feedback screen
│   │   │   │       ├── history/                # Session history
│   │   │   │       ├── sessiondetail/          # Session details
│   │   │   │       └── preview/                # Video preview
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                               # Unit tests
│   │   └── androidTest/                        # Instrumented tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/                                     # Gradle wrapper
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties                            # Local SDK path (not versioned)
```

## Installation

### Prerequisites

- Android SDK 28 or higher
- Android Studio Arctic Fox or later
- Java 11 or higher
- Git

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/gpt-tennis-coach.git
   cd GPTennisCoach
   ```

2. **Configure Backend URL**
   - Open `app/src/main/java/com/aaa/gptenniscoach/di/AppModule.kt`
   - Update `BASE_URL` with your backend API endpoint (must end with `/`)
   ```kotlin
   private const val BASE_URL = "https://your-backend-url/"
   ```

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**
   ```bash
   ./gradlew installDebug
   ```

## Usage

### Basic Workflow

1. **Capture**: Open the app and use the capture screen to record a tennis swing
   - Select player metadata (skill level, hand dominance, improvement goal, language, detail level)
   - Use front or back camera
   - Preview video before analysis

2. **Analyze**: Submit video for backend analysis
   - Video is uploaded to the backend
   - Backend performs AI analysis
   - Real-time progress updates

3. **Review Results**: View detailed coaching feedback
   - Quality score and metrics
   - Focus points with current issues and targets
   - Recommended drills
   - Overlay skeleton visualization on video

4. **Access History**: Browse previously analyzed sessions
   - View session summaries with dates and quality scores
   - Delete individual sessions or all history

## Configuration

### Backend Integration

The app communicates with a Python backend that performs the actual tennis swing analysis. Ensure the backend is running and accessible.

**Backend URL**: Configure in `AppModule.kt`

**API Endpoint**: `POST /v1/analyze/video`
- Expects multipart form data with:
  - `video`: MP4 video file
  - `meta`: JSON player metadata
  - `include_overlay`: Query parameter (1 for skeleton overlay)

### Permissions

The app requires the following Android permissions:
- `android.permission.CAMERA` - Video capture
- `android.permission.READ_EXTERNAL_STORAGE` - Access imported videos
- `android.permission.WRITE_EXTERNAL_STORAGE` - Save analysis results

## Development

### Adding New Features

1. Create feature in `feature/` directory following MVVM pattern
2. Create corresponding ViewModel with Hilt `@HiltViewModel`
3. Implement Composable UI screen
4. Add navigation route in `nav/Destinations.kt` and `AppNavGraph.kt`
5. Inject dependencies using Hilt in `AppModule.kt`

### Code Style

- Kotlin language conventions
- MVVM architecture pattern
- Jetpack Compose for UI
- Coroutines for async operations
- Flow/StateFlow for reactive state management

### Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Troubleshooting

### Backend Connection Issues
- Verify `BASE_URL` in `AppModule.kt` is correct
- Ensure backend server is running and accessible
- Check network connectivity on device

### Video Upload Failures
- Verify video file is in MP4 format
- Check available storage space
- Ensure proper file permissions

### Skeleton Overlay Not Showing
- Confirm backend returned overlay data (check response JSON)
- Verify video dimensions are correctly detected
- Check that analysis quality is sufficient

## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines on how to contribute to this project.

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) file for details.

## Authors

- **Development Team**: azeragayev@yahoo.com
- **Project**: GPT Tennis Coach

## Acknowledgments

- Android Jetpack libraries
- ExoPlayer for video playback
- Retrofit for API communication
- Hilt for dependency injection

## Support

For issues, questions, or suggestions, please open an issue on the GitHub repository.

---

**Version**: 1.0.0  
**Last Updated**: January 29, 2026
