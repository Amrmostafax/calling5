# Voice Chat Application

Full-stack voice-based chat application with Next.js backend (Vercel) and Android frontend (GitHub Actions).

## 🎯 Project Structure

```
calling5/
├── backend/              # Next.js API for Vercel
│   ├── app/
│   │   └── api/
│   │       ├── chat/    # Main voice processing endpoint
│   │       └── health/  # Health check
│   ├── package.json
│   └── vercel.json
│
├── app/                  # Android application
│   ├── src/main/
│   │   ├── java/com/example/myvoicebackend/
│   │   │   ├── MainActivity.kt
│   │   │   ├── models/
│   │   │   ├── network/
│   │   │   ├── ui/
│   │   │   └── utils/
│   │   └── res/
│   └── build.gradle.kts
│
└── .github/workflows/    # GitHub Actions for APK build
```

## 🚀 Quick Start

### Backend Deployment (Vercel)

1. **Navigate to backend folder:**
   ```bash
   cd backend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Create `.env.local` file:**
   ```env
   OPENAI_API_KEY=sk-your-api-key-here
   ```

4. **Test locally:**
   ```bash
   npm run dev
   ```
   Visit: http://localhost:3000/api/health

5. **Deploy to Vercel:**
   - Go to [vercel.com](https://vercel.com)
   - Import project from GitHub
   - Set root directory: `backend`
   - Add environment variable: `OPENAI_API_KEY`
   - Deploy!

6. **Copy your Vercel URL** (e.g., `https://your-app.vercel.app`)

### Android App (GitHub Web Interface)

1. **Update Vercel URL:**
   - Open `app/src/main/java/com/example/myvoicebackend/network/NetworkConfig.kt`
   - Replace `https://your-app.vercel.app/` with your actual Vercel URL
   - Commit via GitHub web interface

2. **Commit all files to GitHub:**
   - Use GitHub web interface to create/edit files
   - Every commit to `main` branch triggers APK build automatically

3. **Download APK:**
   - Go to Actions tab
   - Click on latest successful build
   - Download `app-debug` artifact
   - Extract ZIP to get APK

4. **Install on Android:**
   - Transfer APK to phone
   - Enable "Install from Unknown Sources"
   - Install and enjoy!

## 📱 Features

- 🎤 **Voice Recording**: Tap button to record your voice
- 🔊 **Speech-to-Text**: Automatic transcription using OpenAI Whisper
- 🤖 **AI Chat**: Intelligent responses from GPT-3.5 Turbo
- 🗣️ **Text-to-Speech**: AI reads responses aloud
- 💬 **Chat UI**: Beautiful message interface
- ☁️ **Cloud Build**: No local Android Studio needed

## 🛠️ Tech Stack

### Backend
- Next.js 14
- OpenAI API (Whisper + GPT-3.5)
- TypeScript
- Vercel

### Android
- Kotlin
- Retrofit (API calls)
- Material Design 3
- RecyclerView
- Android TTS

### DevOps
- GitHub Actions (APK build)
- Vercel (Backend hosting)

## 📖 Usage

1. **Launch the app** on your Android device
2. **Grant microphone permission** when prompted
3. **Tap the microphone button** to start recording
4. **Speak your message** (e.g., "Hello, how are you?")
5. **Tap again to stop** recording
6. **Watch the magic:**
   - Your speech is transcribed
   - AI generates a response
   - Response is displayed in chat
   - Response is read aloud via TTS

## 🔧 Configuration

### Backend Environment Variables

Create `backend/.env.local`:
```env
OPENAI_API_KEY=sk-your-openai-api-key
```

Get API key: https://platform.openai.com/api-keys

### Android Network Configuration

Edit `app/src/main/java/com/example/myvoicebackend/network/NetworkConfig.kt`:
```kotlin
private const val BASE_URL = "https://YOUR-APP.vercel.app/"
```

## 🎨 Customization

### Change App Name
Edit `app/src/main/res/values/strings.xml`

### Change Colors
Edit `app/src/main/res/values/colors.xml`

### Change AI Model
Edit `backend/app/api/chat/route.ts`:
```typescript
model: 'gpt-4' // or 'gpt-3.5-turbo'
```

## 🐛 Troubleshooting

### Backend Issues

**Error: "Missing API key"**
- Add `OPENAI_API_KEY` to Vercel environment variables

**Error: "CORS"**
- Check `backend/middleware.ts` is configured correctly

### Android Issues

**Build fails on GitHub Actions**
- Check Java version is 17
- Ensure `gradlew` has execute permissions

**App crashes on launch**
- Check `BASE_URL` in `NetworkConfig.kt`
- Verify Vercel deployment is successful

**No audio recording**
- Grant microphone permission in app settings
- Check Android version is 7.0+

## 📄 License

MIT License - Feel free to use for your projects!

## 🙏 Credits

Built with ❤️ using OpenAI, Next.js, and Android
