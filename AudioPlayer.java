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
}
