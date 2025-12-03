package com.foysaldev.textonlywith.geminiapi;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.foysaldev.textonlywith.R;

public class GeminiMainAc extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText queryEditText;
    private TextView StatusTv;
    private ProgressBar sendPromptProgressBar;
    private Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.gemini_main_ac);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize UI elements
        queryEditText = findViewById(R.id.queryEditText);
        StatusTv = findViewById(R.id.StatusTv);
        sendPromptProgressBar = findViewById(R.id.sendPromptProgressBar);
        send_btn = findViewById(R.id.send_button);

        if (sendPromptProgressBar != null) {
            sendPromptProgressBar.setVisibility(View.GONE);
        }

        // Request focus and show keyboard
        queryEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(queryEditText, InputMethodManager.SHOW_IMPLICIT);


        // Handle keyboard "Go"/"Send" button
        queryEditText.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, "Editor action: " + actionId); // Debug log
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                Log.d(TAG, "Calling Gemini API from keyboard action");
                callGeminiApi();
                InputMethodManager immHide = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                immHide.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callGeminiApi();
            }
        });


    }

    private void callGeminiApi() {
        Log.d(TAG, "callGeminiApi invoked");
        GeminiPro model = new GeminiPro(); // Ensure GeminiPro is correctly implemented
        String query = queryEditText.getText().toString().trim();

        if (query.isEmpty()) {
            Toast.makeText(GeminiMainAc.this, "Please enter a query.", Toast.LENGTH_SHORT).show();
            if (sendPromptProgressBar != null) {
                sendPromptProgressBar.setVisibility(View.GONE);
            }
            return;
        }

        if (sendPromptProgressBar != null) {
            sendPromptProgressBar.setVisibility(View.VISIBLE);
        }

        StatusTv.setText("Thinking...");
        queryEditText.setText("");

        model.getResponse(query, new ResponseCallback() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Gemini API response: " + response);
                StatusTv.setText(response);
                if (sendPromptProgressBar != null) {
                    sendPromptProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Gemini API error: " + throwable.getMessage());
                Toast.makeText(GeminiMainAc.this, "Error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                StatusTv.setText("Error occurred. Please try again.");
                if (sendPromptProgressBar != null) {
                    sendPromptProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}