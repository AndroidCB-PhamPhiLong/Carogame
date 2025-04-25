package btl.phamphilong.carogame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private Spinner spinnerBoardSize;
    private Button btnSaveSettings;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Ánh xạ view
        spinnerBoardSize = findViewById(R.id.spinnerBoardSize);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        // Khởi tạo SharedPreferences để lưu cài đặt
        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        // Thiết lập Adapter cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.board_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBoardSize.setAdapter(adapter);

        // Tải cài đặt hiện tại từ SharedPreferences
        loadBoardSizeSetting();

        // Thiết lập sự kiện khi người dùng chọn kích thước bàn cờ
        spinnerBoardSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Lưu cài đặt khi người dùng chọn kích thước bàn cờ
                saveBoardSizeSetting(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Không làm gì khi không chọn gì
            }
        });

        // Thiết lập sự kiện cho nút "Lưu cài đặt"
        btnSaveSettings.setOnClickListener(v -> finish());
    }

    // Lưu kích thước bàn cờ vào SharedPreferences
    private void saveBoardSizeSetting(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("board_size", position);  // Lưu chỉ số của lựa chọn
        editor.apply();
    }

    // Tải cài đặt kích thước bàn cờ từ SharedPreferences
    private void loadBoardSizeSetting() {
        int savedPosition = sharedPreferences.getInt("board_size", 0);  // Lấy giá trị mặc định là 0 (3x3)
        spinnerBoardSize.setSelection(savedPosition);  // Chọn giá trị tương ứng trong Spinner
    }
}
