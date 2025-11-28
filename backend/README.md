# Voice Chat Backend

Next.js API backend for voice-based chat application using OpenAI.

## Features

- 🎤 Audio-to-text conversion (Whisper)
- 🤖 AI chat responses (GPT-3.5 Turbo)
- 🌐 CORS enabled for Android app
- ☁️ Optimized for Vercel deployment

## Setup

### 1. Install Dependencies

```bash
npm install
```

### 2. Configure Environment Variables

Create a `.env.local` file:

```env
OPENAI_API_KEY=sk-your-api-key-here
```

Get your API key from: https://platform.openai.com/api-keys

### 3. Run Development Server

```bash
npm run dev
```

Server runs at: http://localhost:3000

## API Endpoints

### GET /api/health
Health check endpoint

**Response:**
```json
{
  "status": "healthy",
  "timestamp": "2024-01-01T00:00:00.000Z",
  "service": "Voice Chat Backend"
}
```

### POST /api/chat
Process audio and get AI response

**Request:**
- Method: POST
- Content-Type: multipart/form-data
- Body: audio file (field name: "audio")

**Response:**
```json
{
  "transcription": "Hello, how are you?",
  "response": "I'm doing well, thank you! How can I assist you today?"
}
```

## Deploy to Vercel

### Option 1: Vercel CLI

```bash
npm install -g vercel
vercel login
vercel
```

### Option 2: Vercel Web Interface

1. Go to [vercel.com](https://vercel.com)
2. Click "Import Project"
3. Select your GitHub repository
4. Set root directory to `backend`
5. Add environment variable: `OPENAI_API_KEY`
6. Deploy!

### After Deployment

1. Copy your Vercel URL (e.g., `https://your-app.vercel.app`)
2. Update Android app's `NetworkConfig.kt` with this URL
3. Test the `/api/health` endpoint

## Testing

```bash
# Test health endpoint
curl https://your-app.vercel.app/api/health

# Test chat endpoint (with audio file)
curl -X POST https://your-app.vercel.app/api/chat \
  -F "audio=@test-audio.wav"
```

## Tech Stack

- Next.js 14
- OpenAI API (Whisper + GPT-3.5)
- TypeScript
- Formidable (file uploads)
