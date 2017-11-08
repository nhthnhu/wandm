package com.wandm.speech;

import java.util.List;

/**
 * Speech delegate interface. It contains the methods to receive say events.
 *
 * @author Aleksandar Gotev
 */
public interface SpeechDelegate {

    /**
     * Invoked when the say recognition is started.
     */
    void onStartOfSpeech();

    /**
     * The sound level in the audio stream has changed.
     * There is no guarantee that this method will be called.
     *
     * @param value the new RMS dB value
     */
    void onSpeechRmsChanged(float value);

    /**
     * Invoked when there are partial say results.
     *
     * @param results list of strings. This is ensured to be non null and non empty.
     */
    void onSpeechPartialResults(List<String> results);

    /**
     * Invoked when there is a say result
     *
     * @param result string resulting from say recognition.
     *               This is ensured to be non null.
     */
    void onSpeechResult(String result);

    /**
     * Error when using speech
     *
     * @param code is error code
     */
    void onError(int code);
}
