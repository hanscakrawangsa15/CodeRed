import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sound {
    private static List<Clip> activeClips = new ArrayList<>();
    private static Clip loopingClip = null;

    public static void playSound(String soundFileName) {
        playSound(soundFileName, null);
    }

    public static void playSound(String soundFileName, Runnable onComplete) {
        stopCurrentSound(); // Stop any currently playing sound
        try {
            File soundFile = new File(soundFileName);
            if (!soundFile.exists()) {
                System.out.println("Sound file not found: " + soundFile.getAbsolutePath());
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            // Add a listener to handle completion
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                    activeClips.remove(clip);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            });
            
            // Start the clip and add it to active clips
            clip.start();
            activeClips.add(clip);
            
            // Keep the clip playing for at least a short duration
            Thread.sleep(100); // Small delay to ensure the sound starts playing
            
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: Unsupported audio file format - " + soundFileName);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error: Could not read sound file - " + soundFileName);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.out.println("Error: Audio line unavailable - " + soundFileName);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Error: Sound playback interrupted - " + soundFileName);
            e.printStackTrace();
        }
    }

    public static void playSoundLoop(String soundFileName) {
        try {
            // Stop current looping sound if any
            if (loopingClip != null) {
                if (loopingClip.isRunning()) {
                    loopingClip.stop();
                }
                loopingClip.close();
            }
            
            File soundFile = new File(soundFileName);
            if (!soundFile.exists()) {
                System.out.println("Sound file not found: " + soundFile.getAbsolutePath());
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            loopingClip = AudioSystem.getClip();
            loopingClip.open(audioStream);
            loopingClip.loop(Clip.LOOP_CONTINUOUSLY);
            
        } catch (Exception e) {
            System.out.println("Error playing looping sound: " + soundFileName);
            e.printStackTrace();
        }
    }

    private static void stopCurrentSound() {
        // Stop and remove all active clips
        for (Clip clip : new ArrayList<>(activeClips)) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
        }
        activeClips.clear();
    }

    public static void stopSound() {
        // Stop regular sounds
        stopCurrentSound();
        
        // Stop looping sound
        if (loopingClip != null) {
            if (loopingClip.isRunning()) {
                loopingClip.stop();
            }
            loopingClip.close();
            loopingClip = null;
        }
    }
}
