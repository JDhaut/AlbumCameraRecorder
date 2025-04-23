package com.zhongjh.common.enums

import android.content.ContentResolver
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import com.zhongjh.common.utils.BasePhotoMetadataUtils
import java.util.*

/**
 * 媒体类型的 MIME 类型枚举，定义图片、音频、视频的 MIME 类型和文件扩展名。
 * 用于验证文件类型（如通过 Uri 判断是否为 JPEG、HEIC、MP4 等）。
 *
 * @param mimeTypeName MIME 类型名称（如 "image/jpeg"）
 * @param extensions 文件扩展名集合（如 ["jpg", "jpeg"]）
 * @author zhongjh
 * @date 2021/11/12
 */
enum class MimeType(val mimeTypeName: String, private val extensions: Set<String>) {

    // ============== 图片类型 ==============
    /** JPEG 图片，扩展名：jpg, jpeg */
    JPEG("image/jpeg", setOf("jpg", "jpeg")),
    /** PNG 图片，扩展名：png */
    PNG("image/png", setOf("png")),
    /** GIF 图片，扩展名：gif */
    GIF("image/gif", setOf("gif")),
    /** BMP 图片，扩展名：bmp */
    BMP("image/x-ms-bmp", setOf("bmp")),
    /** WebP 图片，扩展名：webp */
    WEBP("image/webp", setOf("webp")),
    /** HEIC 图片，扩展名：heic（iOS 11+ 默认格式） */
    HEIC("image/heic", setOf("heic")),

    // ============== 音频类型 ==============
    /** AAC 音频，扩展名：aac */
    AAC("audio/aac", setOf("aac")),
    /** MP3 音频，扩展名：mp3 */
    MP3("audio/mpeg", setOf("mp3")),
    /** WAV 音频，扩展名：wav */
    WAV("audio/wav", setOf("wav")),

    // ============== 视频类型 ==============
    /** MPEG 视频，扩展名：mpeg, mpg */
    MPEG("video/mpeg", setOf("mpeg", "mpg")),
    /** MP4 视频，扩展名：mp4, m4v */
    MP4("video/mp4", setOf("mp4", "m4v")),
    /** QuickTime 视频，扩展名：mov */
    QUICKTIME("video/quicktime", setOf("mov")),
    /** 3GPP 视频，扩展名：3gp, 3gpp */
    THREEGPP("video/3gpp", setOf("3gp", "3gpp")),
    /** 3GPP2 视频，扩展名：3g2, 3gpp2 */
    THREEGPP2("video/3gpp2", setOf("3g2", "3gpp2")),
    /** Matroska 视频，扩展名：mkv */
    MKV("video/x-matroska", setOf("mkv")),
    /** WebM 视频，扩展名：webm */
    WEBM("video/webm", setOf("webm")),
    /** MPEG-2 TS 视频，扩展名：ts */
    TS("video/mp2ts", setOf("ts")),
    /** AVI 视频，扩展名：avi */
    AVI("video/avi", setOf("avi"));

    /**
     * 返回 MIME 类型名称。
     */
    override fun toString(): String = mimeTypeName

    /**
     * 检查给定的 Uri 是否匹配当前 MIME 类型。
     * 通过 ContentResolver 获取 MIME 类型，或通过文件路径的扩展名判断。
     *
     * @param resolver ContentResolver 用于查询 Uri 的 MIME 类型
     * @param uri 要检查的 Uri，可能为 null
     * @return 如果 Uri 匹配当前 MIME 类型或扩展名，返回 true；否则返回 false
     *
     * 示例：
     * - Uri: "file:///storage/emulated/0/test.heic"
     *   - resolver.getType(uri) 返回 "image/heic"，匹配 HEIC.mimeTypeName，返回 true。
     *   - 或路径以 ".heic" 结尾，匹配 extensions，返回 true。
     * - Uri: "file:///storage/emulated/0/test.jpg"
     *   - resolver.getType(uri) 返回 "image/jpeg"，匹配 JPEG.mimeTypeName，返回 true。
     * - Uri: "file:///storage/emulated/0/test.txt"
     *   - 无匹配类型或扩展名，返回 false。
     */
    fun checkType(resolver: ContentResolver, uri: Uri?): Boolean {
        if (uri == null) {
            Log.w("MimeType", "Uri is null")
            return false
        }

        val mimeTypeMap = MimeTypeMap.getSingleton()
        // 获取 MIME 类型对应的扩展名，统一小写
        val type = mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri))?.toLowerCase(Locale.US)
        // 提前解析路径，避免重复
        val path = try {
            BasePhotoMetadataUtils.getPath(resolver, uri)?.toLowerCase(Locale.US)
        } catch (e: Exception) {
            Log.e("MimeType", "Failed to get path for $uri", e)
            null
        }

        // 检查 MIME 类型扩展名
        if (type != null && extensions.contains(type)) {
            Log.d("MimeType", "Uri $uri matches $mimeTypeName via MIME type")
            return true
        }

        // 检查文件路径扩展名
        if (path != null) {
            for (extension in extensions) {
                if (path.endsWith(".$extension")) {
                    Log.d("MimeType", "Uri $uri matches $mimeTypeName via extension .$extension")
                    return true
                }
            }
        }

        Log.d("MimeType", "Uri $uri does not match $mimeTypeName")
        return false
    }

    companion object {
        /**
         * 返回所有 MIME 类型集合。
         */
        @JvmStatic
        fun ofAll(): Set<MimeType> = EnumSet.allOf(MimeType::class.java)

        /**
         * 返回指定 MIME 类型的集合。
         *
         * @param type 第一个 MIME 类型
         * @param rest 其他 MIME 类型
         */
        @JvmStatic
        fun of(type: MimeType, vararg rest: MimeType): Set<MimeType> = EnumSet.of(type, *rest)

        /**
         * 返回所有图片类型的集合，包括 HEIC。
         */
        @JvmStatic
        fun ofImage(): Set<MimeType> = EnumSet.of(JPEG, PNG, GIF, BMP, WEBP, HEIC)

        /**
         * 返回所有音频类型的集合。
         */
        @JvmStatic
        fun ofAudio(): Set<MimeType> = EnumSet.of(AAC, MP3, WAV)

        /**
         * 返回所有视频类型的集合。
         */
        @JvmStatic
        fun ofVideo(): Set<MimeType> = EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI)

        /**
         * 判断 MIME 类型是否为图片（包括 HEIC 和 GIF）。
         *
         * @param mimeType MIME 类型字符串，可能为 null
         */
        @JvmStatic
        fun isImage(mimeType: String?): Boolean = mimeType?.startsWith("image") ?: false

        /**
         * 判断 MIME 类型是否为视频。
         *
         * @param mimeType MIME 类型字符串，可能为 null
         */
        @JvmStatic
        fun isVideo(mimeType: String?): Boolean = mimeType?.startsWith("video") ?: false

        /**
         * 判断 MIME 类型是否为音频。
         *
         * @param mimeType MIME 类型字符串，可能为 null
         */
        @JvmStatic
        fun isAudio(mimeType: String?): Boolean = mimeType?.startsWith("audio") ?: false
    }
}