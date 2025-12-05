import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioManager {
    private Clip backgroundClip;

    public AudioManager() {
    }

    public void playBackgroundLoop(String filepath) {
        stopBackground();
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                System.err.println("Background audio file not found: " + filepath);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(ais);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to play background audio: " + filepath);
            e.printStackTrace();
        }
    }

    public void stopBackground() {
        if (backgroundClip != null) {
            try {
                backgroundClip.stop();
                backgroundClip.flush();
                backgroundClip.close();
            } catch (Exception e) {
                // ignore
            }
            backgroundClip = null;
        }
    }

    public void playEffect(String filepath) {
        new Thread(() -> {
            try {
                File file = new File(filepath);
                if (!file.exists()) {
                    System.err.println("Effect audio file not found: " + filepath);
                    return;
                }

                try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(ais);
                    clip.start();
                    // wait for clip to finish then close
                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("Failed to play effect audio: " + filepath);
                e.printStackTrace();
            }
        }).start();
    }
}
