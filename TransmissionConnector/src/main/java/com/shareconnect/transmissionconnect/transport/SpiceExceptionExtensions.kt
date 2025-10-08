package com.shareconnect.transmissionconnect.transport

import com.octo.android.robospice.persistence.exception.SpiceException
import com.shareconnect.transmissionconnect.transport.request.ResponseFailureException

val SpiceException.responseFailureMessage: String?
    get() = (this.cause as? ResponseFailureException)?.failureMessage
