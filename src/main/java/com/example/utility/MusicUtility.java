package com.example.utility;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicUtility {

    public static Clip loadMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        String currentDirectory = System.getProperty("user.dir");
        File musicFile = new File(currentDirectory + "/images/hearts-music.wav");
        if (!musicFile.exists()) {
            throw new RuntimeException("Audio file not found: ");
        }
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }
}