package btl.phamphilong.carogame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvResult = findViewById(R.id.tvResult);
        TextView tvMessage = findViewById(R.id.tvMessage);
        Button btnPlayAgain = findViewById(R.id.btnPlayAgain);
        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);

        // Nhận kết quả từ Intent
        String result = getIntent().getStringExtra("RESULT");
        String message = getIntent().getStringExtra("MESSAGE");

        tvResult.setText(result);
        tvMessage.setText(message);

        // Xử lý nút chơi lại
        btnPlayAgain.setOnClickListener(v -> {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        });

        // Xử lý nút về menu
        btnBackToMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

    }
}