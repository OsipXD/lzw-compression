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

package ru.endlesscode.lzw.util

/**
 * It's like [ByteArray], but it can be used as key in [Map], because
 * ByteWord calculates hash from content of byte array.
 */
class ByteWord(private val inner: ByteArray = byteArrayOf()) {

    operator fun plus(byte: Byte): ByteWord = ByteWord(inner + byte)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ByteWord) return false

        return inner.contentEquals(other.inner)
    }

    override fun hashCode(): Int = inner.contentHashCode()

    override fun toString(): String {
        return inner.contentToString()
    }
}

fun wordFromBytes(vararg bytes: Byte) = ByteWord(bytes)