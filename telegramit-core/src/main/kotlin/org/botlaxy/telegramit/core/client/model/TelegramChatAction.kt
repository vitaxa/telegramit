package org.botlaxy.telegramit.core.client.model

import com.fasterxml.jackson.annotation.JsonProperty

enum class TelegramChatAction {
    @JsonProperty("typing")
    TYPING,
    @JsonProperty("upload_photo")
    UPLOAD_PHOTO,
    @JsonProperty("record_video")
    RECORD_VIDEO,
    @JsonProperty("upload_video")
    UPLOAD_VIDEO,
    @JsonProperty("record_audio")
    RECORD_AUDIO,
    @JsonProperty("upload_audio")
    UPLOAD_AUDIO,
    @JsonProperty("upload_document")
    UPLOAD_DOCUMENT,
    @JsonProperty("find_location")
    FIND_LOCATION,
    @JsonProperty("record_video_note")
    RECORD_VIDEO_NOTE,
    @JsonProperty("upload_video_note")
    UPLOAD_VIDEO_NOTE
}
