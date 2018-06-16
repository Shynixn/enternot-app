package at.jku.enternot.service

import android.content.Context
import at.jku.enternot.contract.ConnectionService
import at.jku.enternot.contract.SirenService
import java.io.IOException

class SirenServiceImpl(private val connectionService: ConnectionService) : SirenService {
    /**
     * Stops the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    override fun stopSiren(context: Context): Int {
        return connectionService.post<Unit>("/siren/start", context)
    }

    /**
     * Plays the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    @Throws(IOException::class)
    override fun playSiren(context: Context): Int {
        return connectionService.post<Unit>("/siren/stop", context)
    }
}