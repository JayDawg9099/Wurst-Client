/*
 * Copyright � 2014 - 2015 | Alexander01998 | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.client.Minecraft;
import tk.wurst_client.event.events.*;
import tk.wurst_client.event.listeners.*;
import tk.wurst_client.gui.error.GuiError;

public abstract class EventManager<E extends Event, L extends Listener>
{
	private final Set<L> listeners = Collections
		.synchronizedSet(new HashSet<L>());
	private final Queue<E> eventQueue = new ConcurrentLinkedQueue<E>();
	private static final Queue<Runnable> listenerQueue =
		new ConcurrentLinkedQueue<Runnable>();
	private boolean locked;
	
	public static final EventManager<UpdateEvent, UpdateListener> update = new EventManager<UpdateEvent, UpdateListener>()
	{
		@Override
		protected void listen(UpdateListener listener, UpdateEvent event)
			throws Exception
		{
			listener.onUpdate();
		}
	};
	
	public synchronized final void fireEvent(E event)
	{
		if(locked)
		{
			eventQueue.add(event);
			return;
		}
		locked = true;
		try
		{
			Iterator<L> itr = listeners.iterator();
			while(itr.hasNext())
			{
				L listener = itr.next();
				try
				{
					listen(listener, event);
				}catch(Exception e)
				{
					handleException(e, listener, "(unknown action)", "");
				}
			}
			for(Runnable task; (task = listenerQueue.poll()) != null;)
				task.run();
		}catch(Exception e)
		{
			handleException(e, event, "processing events", "Event type: "
				+ event.getClass().getSimpleName());
			eventQueue.clear();
		}finally
		{
			locked = false;
		}
		for(E ev; (ev = eventQueue.poll()) != null;)
			fireEvent(ev);
	}
	
	protected abstract void listen(L listener, E event) throws Exception;

	public static final void handleException(final Exception e,
		final Object cause, final String action, final String comment)
	{
		update.addListener(new UpdateListener()
		{
			@Override
			public void onUpdate()
			{
				Minecraft.getMinecraft().displayGuiScreen(
					new GuiError(e, cause, action, comment));
				EventManager.update.removeListener(this);
			}
		});
	}
	
	public final void addListener(final L listener)
	{
		listenerQueue.add(new Runnable()
		{
			@Override
			public void run()
			{
				listeners.add(listener);
			}
		});
	}
	
	public final void removeListener(final L listener)
	{
		listenerQueue.add(new Runnable()
		{
			@Override
			public void run()
			{
				listeners.remove(listener);
			}
		});
	}
}
