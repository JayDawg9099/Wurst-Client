/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.events.listeners;

import tk.wurst_client.events.ChatInputEvent;

public interface ChatInputListener extends Listener
{
	public void onReceivedMessage(ChatInputEvent event);
}
