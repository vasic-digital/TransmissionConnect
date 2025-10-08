package com.shareconnect.transmissionconnect.transport.request;

import com.shareconnect.transmissionconnect.model.json.Torrent;

import java.util.Collection;

public class StopTorrentRequest extends TorrentActionRequest {

    public StopTorrentRequest(int... torrentIds) {
        super("torrent-stop", torrentIds);
    }

    public StopTorrentRequest(Collection<Torrent> torrents) {
        this(toIds(torrents));
    }
}
