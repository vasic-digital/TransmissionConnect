package com.shareconnect.transmissionconnect.torrentdetails;

import com.shareconnect.transmissionconnect.model.limitmode.IdleLimitMode;
import com.shareconnect.transmissionconnect.model.limitmode.LimitMode;

public class IdleLimitModeAdapter extends LimitModeAdapter {
    @Override
    public int getCount() {
        return IdleLimitMode.values().length;
    }

    @Override
    public LimitMode getItem(int position) {
        return IdleLimitMode.values()[position];
    }
}
