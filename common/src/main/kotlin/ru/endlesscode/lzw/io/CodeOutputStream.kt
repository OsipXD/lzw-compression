/*
 * This file is part of lzw-compression, licensed under the MIT License (MIT).
 *
 * Copyright (c) Osip Fatkullin <osip.fatkullin@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.endlesscode.lzw.io

import ru.endlesscode.lzw.util.Bytes

/**
 * Stream that can write out codes that can't fit to one byte.
 *
 * @param stream Target stream
 * @param codeLength Determines code size. Can't be higher than [bufferSize]
 */
class CodeOutputStream(
        private val stream: OutputStream,
        codeLength: Int
) : BufferedCodeStream(codeLength) {

    fun write(code: Int) {
        val bufferedCode = (code and mask) shl (bufferedBits)
        buffer = buffer or bufferedCode
        bufferedBits += codeLength

        writeBuffer()
    }

    /**
     * Write filled bytes from [buffer] to [stream].
     */
    private fun writeBuffer() {
        while (bufferedBits >= Bytes.BITS_IN_BYTE) {
            writeNextByte()
            buffer = buffer ushr Bytes.BITS_IN_BYTE
            bufferedBits -= Bytes.BITS_IN_BYTE
        }
    }

    fun flush() {
        if (buffer != 0) {
            writeNextByte()
        }

        stream.flush()
    }

    /**
     * Write next byte from [buffer] to [stream].
     */
    private fun writeNextByte() {
        stream.write(buffer and Bytes.BYTE_MASK)
    }
}
