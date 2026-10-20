
package com.charli.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    TextToSpeech speaker;
    SpeechRecognizer recognizer;
    TextView status;
    Button listen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 80, 40, 40);

        TextView title = new TextView(this);
        title.setText("CHARLI Assistant");
        title.setTextSize(28);
        layout.addView(title);

        status = new TextView(this);
        status.setText("Namaste! Main CHARLI hoon.");
        status.setTextSize(18);
        layout.addView(status);

        listen = new Button(this);
        listen.setText("Bolo CHARLI");
        layout.addView(listen);

        setContentView(layout);

        speaker = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                speaker.setLanguage(new Locale("en", "IN"));
                speaker.speak("Hello! I am Charli.", 
                    TextToSpeech.QUEUE_FLUSH, null, "hello");
            }
        });

        listen.setOnClickListener(v -> startListening());
    }

    void startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.setText("Voice recognition available nahi hai.");
            return;
        }

        if (recognizer != null) recognizer.destroy();
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);

        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle b) {
                status.setText("Sun raha hoon...");
            }
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() {}
            public void onError(int e) {
                status.setText("Dobara koshish karo.");
            }
            public void onResults(Bundle b) {
                ArrayList<String> results =
                    b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (results != null && !results.isEmpty()) {
                    String command = results.get(0);
                    status.setText("Tumne kaha: " + command);
                    speaker.speak("You said " + command,
                        TextToSpeech.QUEUE_FLUSH, null, "reply");
                }
            }
            public void onPartialResults(Bundle b) {}
            public void onEvent(int t, Bundle b) {}
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        recognizer.startListening(intent);
    }

    @Override
    protected void onDestroy() {
        if (recognizer != null) recognizer.destroy();
        if (speaker != null) speaker.shutdown();
        super.onDestroy();
    }
}
