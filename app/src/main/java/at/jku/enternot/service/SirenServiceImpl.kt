package at.jku.enternot.service

import at.jku.enternot.contract.ConnectionService
import at.jku.enternot.contract.SirenService
import java.io.IOException

class SirenServiceImpl(private val connectionService: ConnectionService) : SirenService {
    /**
     * Plays the siren at the house of the app user.
     * @throws [IOException] when the connection to the server fails.
     */
    @Throws(IOException::class)
    override fun playSiren() {
        Thread.sleep(10000)

        connectionService.post<Any>("/api/startsiren")
    }
}