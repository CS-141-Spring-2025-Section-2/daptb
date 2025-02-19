package daptb;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;



public class AudioPlayer {
    public static void playSound(String soundFile) {
        try {
            URL soundURL = AudioPlayer.class.getResource("/sounds/" + soundFile);
            if (soundURL == null) {
                System.out.println("Sound file not found: " + soundFile);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    //
    private static Clip currentClip;  // Stores currently playing music

public static void playMusic(String soundFile) {
    stopMusic();  // Stop any existing music before playing new one
    try {
        URL soundURL = AudioPlayer.class.getResource("/sounds/" + soundFile);
        if (soundURL == null) {
            System.out.println("Sound file not found: " + soundFile);
            return;
        }
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);
        currentClip.loop(Clip.LOOP_CONTINUOUSLY);  // Loop the music
        currentClip.start();
    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
        e.printStackTrace();
    }
}

public static void stopMusic() {
    if (currentClip != null && currentClip.isRunning()) {
        currentClip.stop();  // Stop playback
        currentClip.close();  // Release resources
        currentClip = null;  // Clear reference
    }
}
public static void playGameOverMusic(String soundFile) {
    stopMusic();  // 🔇 Stop any existing music

    try {
        URL soundURL = AudioPlayer.class.getResource("/sounds/" + soundFile);
        if (soundURL == null) {
            System.out.println("Game Over music not found: " + soundFile);
            return;
        }

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);
        currentClip.start();  // ▶️ Play music once (no looping)
    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
        e.printStackTrace();
    }
}

}
