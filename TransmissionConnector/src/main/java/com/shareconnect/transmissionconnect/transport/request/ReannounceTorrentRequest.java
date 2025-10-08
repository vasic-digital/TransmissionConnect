package com.shareconnect.transmissionconnect.transport.request;

public class ReannounceTorrentRequest extends TorrentActionRequest {

    public ReannounceTorrentRequest(int... torrentIds) {
        super("torrent-reannounce", torrentIds);
    }
}
