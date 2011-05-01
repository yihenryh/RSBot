package org.rsbot.script;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Methods;

import java.awt.*;
import java.util.EventListener;

public abstract class PassiveScript extends Methods implements EventListener, Runnable {
	protected String name = "";
	private volatile boolean enabled = true;
	private volatile boolean running = false;
	private boolean runningL = false;
	private int id = -1;

	public abstract boolean activateCondition();

	public abstract int loop();

	public boolean onStart() {
		return true;
	}

	public void onFinish() {

	}

	@Override
	public final void init(MethodContext ctx) {
		super.init(ctx);
		onStart();
	}

	public final boolean isEnabled() {
		return enabled;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final void run() {
		name = getClass().getAnnotation(PassiveScriptManifest.class).name();
		ctx.bot.getEventManager().addListener(this);
		running = true;
		try {
			while (running) {
				if (activateCondition()) {
					boolean start = onStart();
					if (start) {
						while (running) {
							int timeOut = loop();
							if (timeOut == -1) {
								break;
							}
							Thread.sleep(timeOut);
						}
						onFinish();
					}
				}
			}
		} catch (Exception ignored) {
		}
		ctx.bot.getEventManager().removeListener(this);
		running = false;
	}

	public final void onRepaint(Graphics g) {
		g.setColor(Color.RED);
		g.drawString("Passive Script active: " + name, 540, 20);
	}

	public final void deactivate(int id) {
		if (id != this.id) {
			throw new IllegalStateException("Invalid id!");
		}
		this.running = false;
	}

	public final void setID(int id) {
		if (this.id != -1) {
			throw new IllegalStateException("Already added to pool!");
		}
		this.id = id;
	}

	public final boolean isRunning() {
		return running;
	}
}