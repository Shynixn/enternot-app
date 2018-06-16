package at.jku.enternot.contract

import android.content.Context
import java.io.IOException

interface SirenService {
    /**
     * Plays the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    @Throws(IOException::class)
    fun playSiren(context: Context): Int

    /**
     * Stops the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    @Throws(IOException::class)
    fun stopSiren(context: Context): Int
}