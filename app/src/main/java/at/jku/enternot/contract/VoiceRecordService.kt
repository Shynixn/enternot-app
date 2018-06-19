package at.jku.enternot.contract

import android.arch.lifecycle.LiveData
import android.content.Context

interface VoiceRecordService : AutoCloseable {

    /**
     * Enables or disables the voice recording.
     * @param b: True if the voice recording should be enabled otherwise false.
     */
    fun enableVoiceRecording(b: Boolean, context: Context)

    /**
     * Observable audio data.
     */
    fun getAudioData(): LiveData<ByteArray>

}