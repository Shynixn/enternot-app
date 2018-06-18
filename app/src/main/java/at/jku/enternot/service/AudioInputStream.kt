package at.jku.enternot.service

import android.media.AudioRecord
import java.io.InputStream

class AudioInputStream(private var audioRecord: AudioRecord?) : InputStream() {

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an `int` in the range `0` to
     * `255`. If no byte is available because the end of the stream
     * has been reached, the value `-1` is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     *
     *
     *  A subclass must provide an implementation of this method.
     *
     * @return     the next byte of data, or `-1` if the end of the
     * stream is reached.
     * @exception  IOException  if an I/O error occurs.
     */
    override fun read(): Int {
        val buffer = ByteArray(0)
        return audioRecord!!.read(buffer, 0, 1)
    }


    /**
     * Reads some number of bytes from the input stream and stores them into
     * the buffer array `b`. The number of bytes actually read is
     * returned as an integer.  This method blocks until input data is
     * available, end of file is detected, or an exception is thrown.
     *
     *
     *  If the length of `b` is zero, then no bytes are read and
     * `0` is returned; otherwise, there is an attempt to read at
     * least one byte. If no byte is available because the stream is at the
     * end of the file, the value `-1` is returned; otherwise, at
     * least one byte is read and stored into `b`.
     *
     *
     *  The first byte read is stored into element `b[0]`, the
     * next one into `b[1]`, and so on. The number of bytes read is,
     * at most, equal to the length of `b`. Let *k* be the
     * number of bytes actually read; these bytes will be stored in elements
     * `b[0]` through `b[`*k*`-1]`,
     * leaving elements `b[`*k*`]` through
     * `b[b.length-1]` unaffected.
     *
     *
     *  The `read(b)` method for class `InputStream`
     * has the same effect as: <pre>` read(b, 0, b.length) `</pre>
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     * `-1` if there is no more data because the end of
     * the stream has been reached.
     * @exception  IOException  If the first byte cannot be read for any reason
     * other than the end of the file, if the input stream has been closed, or
     * if some other I/O error occurs.
     * @exception  NullPointerException  if `b` is `null`.
     * @see java.io.InputStream.read
     */
    override fun read(b: ByteArray): Int {
        if (audioRecord!!.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            return audioRecord!!.read(b, 0, b.size)
        }

        return -1
    }

    /**
     * Closes this input stream and releases any system resources associated
     * with the stream.
     *
     *
     *  The `close` method of `InputStream` does
     * nothing.
     *
     * @exception  IOException  if an I/O error occurs.
     */
    override fun close() {
        audioRecord = null
        super.close()
    }
}