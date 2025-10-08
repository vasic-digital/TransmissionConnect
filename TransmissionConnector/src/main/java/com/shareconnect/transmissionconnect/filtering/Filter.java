package com.shareconnect.transmissionconnect.filtering;

import com.shareconnect.transmissionconnect.model.json.Torrent;

import java.util.function.Predicate;

public interface Filter extends Predicate<Torrent> {

    int getNameResId();

    int getEmptyMessageResId();
}
