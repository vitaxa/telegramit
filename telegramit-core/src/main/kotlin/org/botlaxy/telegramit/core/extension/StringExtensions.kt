package org.botlaxy.telegramit.core.extension

import com.vdurmont.emoji.EmojiParser

fun String.emojize(): String {
    return EmojiParser.parseToUnicode(this)
}
