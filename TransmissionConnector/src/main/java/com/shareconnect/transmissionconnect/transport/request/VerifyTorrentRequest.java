package com.shareconnect.transmissionconnect.transport.request;

public class VerifyTorrentRequest extends TorrentActionRequest {

    public VerifyTorrentRequest(int... torrentIds) {
        super("torrent-verify", torrentIds);
    }
}
