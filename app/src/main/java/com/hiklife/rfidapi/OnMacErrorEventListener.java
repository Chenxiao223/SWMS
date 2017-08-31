package com.hiklife.rfidapi;

import java.util.EventListener;

public interface OnMacErrorEventListener extends EventListener {
	void RadioMacError(MacErrorEvent event); //
}
