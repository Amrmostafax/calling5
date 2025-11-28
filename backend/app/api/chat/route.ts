import { NextRequest, NextResponse } from 'next/server';
import { GoogleGenerativeAI } from '@google/generative-ai';
import fs from 'fs';

// Initialize Google Gemini
const genAI = new GoogleGenerativeAI(process.env.GOOGLE_API_KEY || '');

// Disable Next.js body parsing to handle file uploads
export const config = {
  api: {
    bodyParser: false,
  },
};

// Helper function to convert audio to base64
function fileToBase64(filePath: string): string {
  const fileData = fs.readFileSync(filePath);
  return fileData.toString('base64');
}

export async function POST(req: NextRequest) {
  try {
    // Get the form data
    const formData = await req.formData();
    const audioFile = formData.get('audio') as File;

    if (!audioFile) {
      return NextResponse.json(
        { error: 'No audio file provided' },
        { status: 400 }
      );
    }

    // Convert the audio file to a buffer
    const audioBuffer = await audioFile.arrayBuffer();
    const buffer = Buffer.from(audioBuffer);

    // Create a temporary file
    const tempFilePath = `/tmp/audio_${Date.now()}.wav`;
    fs.writeFileSync(tempFilePath, buffer);

    // Get base64 audio
    const audioBase64 = fileToBase64(tempFilePath);

    // Step 1: Use Gemini to transcribe audio and generate response in one call
    console.log('Processing with Gemini Flash...');

    const model = genAI.getGenerativeModel({ model: 'gemini-1.5-flash' });

    const result = await model.generateContent([
      {
        inlineData: {
          mimeType: 'audio/wav',
          data: audioBase64,
        },
      },
      {
        text: `Please do the following:
1. Transcribe the audio to text
2. Provide a helpful and conversational response to what was said
3. Format your response as JSON with two fields: "transcription" (what the user said) and "response" (your reply)

Keep the response concise since it will be read aloud. Be friendly and helpful.

Return ONLY the JSON, no other text.`,
      },
    ]);

    const responseText = result.response.text();
    console.log('Gemini response:', responseText);

    // Try to parse JSON from response
    let parsedResponse;
    try {
      // Extract JSON from markdown code blocks if present
      const jsonMatch = responseText.match(/```(?:json)?\s*(\{[\s\S]*\})\s*```/) ||
        responseText.match(/(\{[\s\S]*\})/);
      const jsonText = jsonMatch ? jsonMatch[1] : responseText;
      parsedResponse = JSON.parse(jsonText);
    } catch (parseError) {
      // Fallback: create a simpler prompt
      console.log('Failed to parse JSON, trying simpler approach...');

      const simpleResult = await model.generateContent([
        {
          inlineData: {
            mimeType: 'audio/wav',
            data: audioBase64,
          },
        },
        { text: 'What did the person say in this audio? Reply with just the transcription.' },
      ]);

      const transcription = simpleResult.response.text().trim();

      // Get AI response
      const chatResult = await model.generateContent(
        `The user said: "${transcription}". Provide a helpful, concise response (1-2 sentences max) since it will be read aloud.`
      );

      const aiResponse = chatResult.response.text().trim();

      parsedResponse = {
        transcription: transcription,
        response: aiResponse,
      };
    }

    // Clean up temporary file
    fs.unlinkSync(tempFilePath);

    // Return the response
    return NextResponse.json({
      transcription: parsedResponse.transcription || 'Could not transcribe audio',
      response: parsedResponse.response || 'I heard you! How can I help?',
    });

  } catch (error: any) {
    console.error('Error processing request:', error);
    return NextResponse.json(
      {
        error: 'Failed to process audio',
        details: error.message
      },
      { status: 500 }
    );
  }
}

export async function GET() {
  return NextResponse.json({
    message: 'Voice Chat API is running (Google Gemini Flash). Use POST to send audio files.',
    endpoints: {
      chat: '/api/chat (POST)',
      health: '/api/health (GET)',
    },
    model: 'gemini-1.5-flash',
  });
}
