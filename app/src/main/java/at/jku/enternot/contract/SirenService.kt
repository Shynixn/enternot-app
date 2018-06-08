package at.jku.enternot.contract

import java.io.IOException

interface SirenService {

    /**
     * Plays the siren at the house of the app user. Returns the statuscode.
     * @throws [IOException] when the connection to the server fails.
     */
    @Throws(IOException::class)
    fun playSiren() : Int
}