/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.transmissionconnect.transport.request;

import android.util.Log;

import com.shareconnect.transmissionconnect.model.json.Torrent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;

public abstract class TorrentActionRequest extends Request<Void> {

    private static final String TAG = TorrentActionRequest.class.getSimpleName();

    private String method;
    private int[] torrentIds;

    public TorrentActionRequest(String method, int... torrentIds) {
        super(Void.class);
        this.method = method;
        this.torrentIds = torrentIds;
    }

    @Override
    protected String getMethod() {
        return method;
    }

    @Override
    protected JSONObject getArguments() {
        JSONObject args = new JSONObject();

        JSONArray ids = new JSONArray();
        for (int id : torrentIds) {
            ids.put(id);
        }

        try {
            args.put("ids", ids);
        } catch (JSONException e) {
            Log.e(TAG, "Failed to form arguments JSON object for '" + getMethod() + "' request", e);
        }
        return args;
    }

    public static int[] toIds(Collection<Torrent> torrents) {
        int[] ids = new int[torrents.size()];
        int i = 0;
        for (Torrent torrent : torrents) {
            ids[i++] = torrent.getId();
        }
        return ids;
    }
}
