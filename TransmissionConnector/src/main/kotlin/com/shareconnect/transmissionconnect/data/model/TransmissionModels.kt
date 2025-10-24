package com.shareconnect.transmissionconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * Transmission RPC response wrapper
 */
data class TransmissionResponse<T>(
    @SerializedName("arguments")
    val arguments: T?,

    @SerializedName("result")
    val result: String,

    @SerializedName("tag")
    val tag: Int? = null
)

/**
 * Transmission torrent model
 */
data class TransmissionTorrent(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("hashString")
    val hashString: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("percentDone")
    val percentDone: Double,

    @SerializedName("rateDownload")
    val rateDownload: Long,

    @SerializedName("rateUpload")
    val rateUpload: Long,

    @SerializedName("downloadedEver")
    val downloadedEver: Long,

    @SerializedName("uploadedEver")
    val uploadedEver: Long,

    @SerializedName("totalSize")
    val totalSize: Long,

    @SerializedName("eta")
    val eta: Long,

    @SerializedName("peersConnected")
    val peersConnected: Int,

    @SerializedName("peersGettingFromUs")
    val peersGettingFromUs: Int,

    @SerializedName("peersSendingToUs")
    val peersSendingToUs: Int,

    @SerializedName("seeders")
    val seeders: Int? = null,

    @SerializedName("leechers")
    val leechers: Int? = null,

    @SerializedName("uploadRatio")
    val uploadRatio: Double,

    @SerializedName("addedDate")
    val addedDate: Long,

    @SerializedName("activityDate")
    val activityDate: Long,

    @SerializedName("doneDate")
    val doneDate: Long,

    @SerializedName("downloadDir")
    val downloadDir: String,

    @SerializedName("error")
    val error: Int,

    @SerializedName("errorString")
    val errorString: String,

    @SerializedName("files")
    val files: List<TransmissionFile>? = null,

    @SerializedName("trackers")
    val trackers: List<TransmissionTracker>? = null,

    @SerializedName("trackerStats")
    val trackerStats: List<TransmissionTrackerStat>? = null,

    @SerializedName("peers")
    val peers: List<TransmissionPeer>? = null,

    @SerializedName("bandwidthPriority")
    val bandwidthPriority: Int,

    @SerializedName("honorsSessionLimits")
    val honorsSessionLimits: Boolean,

    @SerializedName("downloadLimit")
    val downloadLimit: Long,

    @SerializedName("downloadLimited")
    val downloadLimited: Boolean,

    @SerializedName("uploadLimit")
    val uploadLimit: Long,

    @SerializedName("uploadLimited")
    val uploadLimited: Boolean,

    @SerializedName("queuePosition")
    val queuePosition: Int,

    @SerializedName("isFinished")
    val isFinished: Boolean,

    @SerializedName("isPrivate")
    val isPrivate: Boolean,

    @SerializedName("isStalled")
    val isStalled: Boolean,

    @SerializedName("labels")
    val labels: List<String>? = null,

    @SerializedName("magnetLink")
    val magnetLink: String? = null,

    @SerializedName("metadataPercentComplete")
    val metadataPercentComplete: Double,

    @SerializedName("pieceCount")
    val pieceCount: Int,

    @SerializedName("pieceSize")
    val pieceSize: Long
)

/**
 * Transmission file model
 */
data class TransmissionFile(
    @SerializedName("name")
    val name: String,

    @SerializedName("length")
    val length: Long,

    @SerializedName("bytesCompleted")
    val bytesCompleted: Long
)

/**
 * Transmission tracker model
 */
data class TransmissionTracker(
    @SerializedName("id")
    val id: Int,

    @SerializedName("announce")
    val announce: String,

    @SerializedName("scrape")
    val scrape: String,

    @SerializedName("tier")
    val tier: Int
)

/**
 * Transmission tracker stats model
 */
data class TransmissionTrackerStat(
    @SerializedName("id")
    val id: Int,

    @SerializedName("announce")
    val announce: String,

    @SerializedName("announceState")
    val announceState: Int,

    @SerializedName("downloadCount")
    val downloadCount: Int,

    @SerializedName("hasAnnounced")
    val hasAnnounced: Boolean,

    @SerializedName("hasScraped")
    val hasScraped: Boolean,

    @SerializedName("host")
    val host: String,

    @SerializedName("isBackup")
    val isBackup: Boolean,

    @SerializedName("lastAnnouncePeerCount")
    val lastAnnouncePeerCount: Int,

    @SerializedName("lastAnnounceResult")
    val lastAnnounceResult: String,

    @SerializedName("lastAnnounceStartTime")
    val lastAnnounceStartTime: Long,

    @SerializedName("lastAnnounceSucceeded")
    val lastAnnounceSucceeded: Boolean,

    @SerializedName("lastAnnounceTime")
    val lastAnnounceTime: Long,

    @SerializedName("lastAnnounceTimedOut")
    val lastAnnounceTimedOut: Boolean,

    @SerializedName("lastScrapeResult")
    val lastScrapeResult: String,

    @SerializedName("lastScrapeStartTime")
    val lastScrapeStartTime: Long,

    @SerializedName("lastScrapeSucceeded")
    val lastScrapeSucceeded: Boolean,

    @SerializedName("lastScrapeTime")
    val lastScrapeTime: Long,

    @SerializedName("lastScrapeTimedOut")
    val lastScrapeTimedOut: Boolean,

    @SerializedName("leecherCount")
    val leecherCount: Int,

    @SerializedName("nextAnnounceTime")
    val nextAnnounceTime: Long,

    @SerializedName("nextScrapeTime")
    val nextScrapeTime: Long,

    @SerializedName("scrape")
    val scrape: String,

    @SerializedName("scrapeState")
    val scrapeState: Int,

    @SerializedName("seederCount")
    val seederCount: Int,

    @SerializedName("tier")
    val tier: Int
)

/**
 * Transmission peer model
 */
data class TransmissionPeer(
    @SerializedName("address")
    val address: String,

    @SerializedName("clientName")
    val clientName: String,

    @SerializedName("clientIsChoked")
    val clientIsChoked: Boolean,

    @SerializedName("clientIsInterested")
    val clientIsInterested: Boolean,

    @SerializedName("flagStr")
    val flagStr: String,

    @SerializedName("isDownloadingFrom")
    val isDownloadingFrom: Boolean,

    @SerializedName("isEncrypted")
    val isEncrypted: Boolean,

    @SerializedName("isIncoming")
    val isIncoming: Boolean,

    @SerializedName("isUploadingTo")
    val isUploadingTo: Boolean,

    @SerializedName("isUTP")
    val isUTP: Boolean,

    @SerializedName("peerIsChoked")
    val peerIsChoked: Boolean,

    @SerializedName("peerIsInterested")
    val peerIsInterested: Boolean,

    @SerializedName("port")
    val port: Int,

    @SerializedName("progress")
    val progress: Double,

    @SerializedName("rateToClient")
    val rateToClient: Long,

    @SerializedName("rateToPeer")
    val rateToPeer: Long
)

/**
 * Transmission session model
 */
data class TransmissionSession(
    @SerializedName("version")
    val version: String,

    @SerializedName("rpc-version")
    val rpcVersion: Int,

    @SerializedName("rpc-version-minimum")
    val rpcVersionMinimum: Int,

    @SerializedName("alt-speed-down")
    val altSpeedDown: Long,

    @SerializedName("alt-speed-enabled")
    val altSpeedEnabled: Boolean,

    @SerializedName("alt-speed-time-enabled")
    val altSpeedTimeEnabled: Boolean,

    @SerializedName("alt-speed-up")
    val altSpeedUp: Long,

    @SerializedName("blocklist-enabled")
    val blocklistEnabled: Boolean,

    @SerializedName("blocklist-size")
    val blocklistSize: Long,

    @SerializedName("blocklist-url")
    val blocklistUrl: String,

    @SerializedName("cache-size-mb")
    val cacheSizeMb: Long,

    @SerializedName("config-dir")
    val configDir: String,

    @SerializedName("download-dir")
    val downloadDir: String,

    @SerializedName("download-dir-free-space")
    val downloadDirFreeSpace: Long,

    @SerializedName("download-queue-enabled")
    val downloadQueueEnabled: Boolean,

    @SerializedName("download-queue-size")
    val downloadQueueSize: Int,

    @SerializedName("encryption")
    val encryption: String,

    @SerializedName("idle-seeding-limit")
    val idleSeedingLimit: Int,

    @SerializedName("idle-seeding-limit-enabled")
    val idleSeedingLimitEnabled: Boolean,

    @SerializedName("incomplete-dir")
    val incompleteDir: String,

    @SerializedName("incomplete-dir-enabled")
    val incompleteDirEnabled: Boolean,

    @SerializedName("peer-limit-global")
    val peerLimitGlobal: Int,

    @SerializedName("peer-limit-per-torrent")
    val peerLimitPerTorrent: Int,

    @SerializedName("peer-port")
    val peerPort: Int,

    @SerializedName("peer-port-random-on-start")
    val peerPortRandomOnStart: Boolean,

    @SerializedName("pex-enabled")
    val pexEnabled: Boolean,

    @SerializedName("port-forwarding-enabled")
    val portForwardingEnabled: Boolean,

    @SerializedName("queue-stalled-enabled")
    val queueStalledEnabled: Boolean,

    @SerializedName("queue-stalled-minutes")
    val queueStalledMinutes: Int,

    @SerializedName("rename-partial-files")
    val renamePartialFiles: Boolean,

    @SerializedName("script-torrent-done-enabled")
    val scriptTorrentDoneEnabled: Boolean,

    @SerializedName("script-torrent-done-filename")
    val scriptTorrentDoneFilename: String,

    @SerializedName("seed-queue-enabled")
    val seedQueueEnabled: Boolean,

    @SerializedName("seed-queue-size")
    val seedQueueSize: Int,

    @SerializedName("seedRatioLimit")
    val seedRatioLimit: Double,

    @SerializedName("seedRatioLimited")
    val seedRatioLimited: Boolean,

    @SerializedName("speed-limit-down")
    val speedLimitDown: Long,

    @SerializedName("speed-limit-down-enabled")
    val speedLimitDownEnabled: Boolean,

    @SerializedName("speed-limit-up")
    val speedLimitUp: Long,

    @SerializedName("speed-limit-up-enabled")
    val speedLimitUpEnabled: Boolean,

    @SerializedName("start-added-torrents")
    val startAddedTorrents: Boolean,

    @SerializedName("trash-original-torrent-files")
    val trashOriginalTorrentFiles: Boolean,

    @SerializedName("units")
    val units: TransmissionUnits,

    @SerializedName("utp-enabled")
    val utpEnabled: Boolean
)

/**
 * Transmission units model
 */
data class TransmissionUnits(
    @SerializedName("speed-units")
    val speedUnits: List<String>,

    @SerializedName("speed-bytes")
    val speedBytes: Long,

    @SerializedName("size-units")
    val sizeUnits: List<String>,

    @SerializedName("size-bytes")
    val sizeBytes: Long,

    @SerializedName("memory-units")
    val memoryUnits: List<String>,

    @SerializedName("memory-bytes")
    val memoryBytes: Long
)

/**
 * Transmission session stats
 */
data class TransmissionStats(
    @SerializedName("activeTorrentCount")
    val activeTorrentCount: Int,

    @SerializedName("downloadSpeed")
    val downloadSpeed: Long,

    @SerializedName("pausedTorrentCount")
    val pausedTorrentCount: Int,

    @SerializedName("torrentCount")
    val torrentCount: Int,

    @SerializedName("uploadSpeed")
    val uploadSpeed: Long,

    @SerializedName("cumulative-stats")
    val cumulativeStats: TransmissionCumulativeStats,

    @SerializedName("current-stats")
    val currentStats: TransmissionCumulativeStats
)

/**
 * Transmission cumulative stats
 */
data class TransmissionCumulativeStats(
    @SerializedName("uploadedBytes")
    val uploadedBytes: Long,

    @SerializedName("downloadedBytes")
    val downloadedBytes: Long,

    @SerializedName("filesAdded")
    val filesAdded: Long,

    @SerializedName("sessionCount")
    val sessionCount: Long,

    @SerializedName("secondsActive")
    val secondsActive: Long
)

/**
 * Transmission torrent list arguments
 */
data class TransmissionTorrentList(
    @SerializedName("torrents")
    val torrents: List<TransmissionTorrent>
)

/**
 * Transmission torrent add result
 */
data class TransmissionTorrentAddResult(
    @SerializedName("torrent-added")
    val torrentAdded: TransmissionTorrent? = null,

    @SerializedName("torrent-duplicate")
    val torrentDuplicate: TransmissionTorrent? = null
)
