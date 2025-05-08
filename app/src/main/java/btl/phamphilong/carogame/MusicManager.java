package btl.phamphilong.carogame;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;

    public static void startBackgroundMusic(Context context) {
        if (!isMusicEnabled(context)) {
            stopMusic();
            return;
        }

        stopMusic(); // Đảm bảo không phát đè lên

        mediaPlayer = MediaPlayer.create(context, R.raw.background_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public static void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void resumeMusic(Context context) {
        startBackgroundMusic(context); // Luôn phát lại từ đầu
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private static boolean isMusicEnabled(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return preferences.getBoolean("musicEnabled", true);
    }
}
