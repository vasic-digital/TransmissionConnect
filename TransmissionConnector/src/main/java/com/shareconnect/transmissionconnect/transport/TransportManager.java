package com.shareconnect.transmissionconnect.transport;

import com.octo.android.robospice.request.listener.RequestListener;

import com.shareconnect.transmissionconnect.transport.request.Request;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface TransportManager {
    <T> void doRequest(@Nonnull final Request<T> request, @Nullable RequestListener<T> listener);
    <T> void doRequest(@Nonnull final Request<T> request, @Nullable RequestListener<T> listener, long delay);
    boolean isStarted();
}
