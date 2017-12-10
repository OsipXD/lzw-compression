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

package ru.endlesscode.lzw

import ru.endlesscode.lzw.LzwCompressor.Companion.INIT_DICT_SIZE
import ru.endlesscode.lzw.io.CodeOutputStream
import ru.endlesscode.lzw.io.InputStream
import ru.endlesscode.lzw.io.OutputStream
import ru.endlesscode.lzw.io.readEachByte
import ru.endlesscode.lzw.util.ByteWord
import ru.endlesscode.lzw.util.wordFromBytes

/**
 * Compressor that uses LZW algorithm to compress and decompress streams.
 *
 * @param codeLength Code length in bits. It determines how much records can be in [codeTable].
 * Available number of records can be calculated as 2^[codeLength] - [INIT_DICT_SIZE]. With current
 * implementation of [CodeOutputStream] and [CodeInputStream] it can't be higher than 64.
 */
class LzwCompressor(
        val codeLength: Int = DEFAULT_CODE_LENGTH
) : Compressor {

    companion object {

        /**
         * Initial size of compression dictionary.
         * On starting compression each byte mapping to itself.
         */
        private const val INIT_DICT_SIZE = 256

        /**
         * By default we can store in [codeTable] 2^12 records. But 256 of records occupied
         * by [INIT_DICT_SIZE]. As result we have 3840 places for records in [codeTable].
         */
        private const val DEFAULT_CODE_LENGTH = 12
    }

    /**
     * Table that maps bytes sequence to its position in decode table.
     */
    private lateinit var codeTable: MutableMap<ByteWord, Int>

    /**
     * List that contains bytes sequences
     */
    private lateinit var decodeTable: MutableList<ByteWord>


    override fun compress(input: InputStream, output: OutputStream) {
        initTables()

        var nextCode = INIT_DICT_SIZE
        var word = wordFromBytes(input.read().toByte())
        val codeOutput = CodeOutputStream(output, codeLength)

        input.readEachByte { byte ->
            val newWord = word + byte
            word = if (codeTable.contains(newWord)) {
                newWord
            } else {
                codeOutput.write(codeTable[word] ?: throw Error("Word '$word' must be in table."))
                codeTable.put(newWord, nextCode++)
                wordFromBytes(byte)
            }
        }

        codeTable[word]?.let(output::write)
        codeOutput.flush()
    }

    override fun decompress(input: InputStream, output: OutputStream) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initTables() {
        codeTable = hashMapOf()
        decodeTable = arrayListOf()

        for (i in 0 until INIT_DICT_SIZE) {
            codeTable.put(wordFromBytes(i.toByte()), i)
            decodeTable.add(wordFromBytes(i.toByte()))
        }
    }
}