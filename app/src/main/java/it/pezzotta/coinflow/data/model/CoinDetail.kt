package it.pezzotta.coinflow.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CoinDetail(
    @SerialName("id") val id: String? = null,
    @SerialName("symbol") val symbol: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: Description? = null,
    @SerialName("links") val links: Links? = null,
    @SerialName("image") val image: Image? = null
) : Parcelable

@Parcelize
@Serializable
data class Description(
    @SerialName("en") val en: String? = null
    // Add more languages if needed (eg @SerialName("it") val it: String? = null)
) : Parcelable

@Parcelize
@Serializable
data class Links(
    @SerialName("homepage") val homepage: List<String>? = null,
    @SerialName("whitepaper") val whitepaper: String? = null,
    @SerialName("blockchain_site") val blockchainSite: List<String>? = null,
    @SerialName("official_forum_url") val officialForumUrl: List<String>? = null,
    @SerialName("chat_url") val chatUrl: List<String>? = null,
    @SerialName("announcement_url") val announcementUrl: List<String>? = null,
    @SerialName("snapshot_url") val snapshotUrl: String? = null,
    @SerialName("twitter_screen_name") val twitterScreenName: String? = null,
    @SerialName("facebook_username") val facebookUsername: String? = null,
    @SerialName("bitcointalk_thread_identifier") val bitcointalkThreadIdentifier: Long? = null,
    @SerialName("telegram_channel_identifier") val telegramChannelIdentifier: String? = null,
    @SerialName("subreddit_url") val subredditUrl: String? = null,
    @SerialName("repos_url") val reposUrl: ReposUrl? = null
) : Parcelable

@Parcelize
@Serializable
data class ReposUrl(
    @SerialName("github") val github: List<String>? = null,
    @SerialName("bitbucket") val bitbucket: List<String>? = null
) : Parcelable

@Parcelize
@Serializable
data class Image(
    @SerialName("thumb") val thumb: String? = null,
    @SerialName("small") val small: String? = null,
    @SerialName("large") val large: String? = null
) : Parcelable
