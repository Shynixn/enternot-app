package at.jku.enternot.service

import at.jku.enternot.contract.ConnectionService
import at.jku.enternot.contract.SirenService
import java.io.IOException

class SirenServiceImpl(private val connectionService: ConnectionService) : SirenService {
    private val playingTime = 15000
    private var lastTimePlayed = 0L

    /**
     * Plays the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    @Throws(IOException::class)
    override fun playSiren() {
        val currentMilliSeconds = System.currentTimeMillis()
        if (currentMilliSeconds - lastTimePlayed < playingTime) {
            return
        }

        lastTimePlayed = currentMilliSeconds
        connectionService.post<Any>("/api/startsiren")
    }
}