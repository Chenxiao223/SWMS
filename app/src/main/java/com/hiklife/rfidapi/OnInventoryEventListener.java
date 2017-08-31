package com.hiklife.rfidapi;

import java.util.EventListener;

public interface OnInventoryEventListener extends EventListener {
	void RadioInventory(InventoryEvent event);
}
