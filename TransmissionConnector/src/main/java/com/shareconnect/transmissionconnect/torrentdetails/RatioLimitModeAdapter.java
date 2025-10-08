package com.shareconnect.transmissionconnect.torrentdetails;

import com.shareconnect.transmissionconnect.model.limitmode.LimitMode;
import com.shareconnect.transmissionconnect.model.limitmode.RatioLimitMode;

public class RatioLimitModeAdapter extends LimitModeAdapter {
    @Override
    public int getCount() {
        return RatioLimitMode.values().length;
    }

    @Override
    public LimitMode getItem(int position) {
        return RatioLimitMode.values()[position];
    }
}
