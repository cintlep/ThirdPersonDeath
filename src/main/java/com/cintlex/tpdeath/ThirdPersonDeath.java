package com.cintlex.tpdeath;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.option.Perspective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThirdPersonDeath implements ClientModInitializer {
	public static final String MOD_ID = "tpdeath";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static boolean wasInDeathScreen = false;
	private static Perspective previousPerspective = null;
	private static long deathStartTime = 0;
	private static boolean zoomEffectActive = false;

	@Override
	public void onInitializeClient() {
		LOGGER.info("Third Person Death is now making fun of your death");

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.currentScreen instanceof DeathScreen) {
				if (!wasInDeathScreen) {
					onDeathScreenEntered(client);
				}
				wasInDeathScreen = true;
			} else {
				if (wasInDeathScreen) {
					onDeathScreenExited(client);
				}
				wasInDeathScreen = false;
			}
		});
	}

	private void onDeathScreenEntered(MinecraftClient client) {
		previousPerspective = client.options.getPerspective();
		client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
		deathStartTime = System.currentTimeMillis();
		zoomEffectActive = true;
	}

	private void onDeathScreenExited(MinecraftClient client) {
		if (previousPerspective != null) {
			client.options.setPerspective(previousPerspective);
			previousPerspective = null;
		}
		zoomEffectActive = false;
		deathStartTime = 0;
	}

	public static boolean isZoomEffectActive() {
		return zoomEffectActive;
	}

	public static float getZoomProgress() {
		if (!zoomEffectActive || deathStartTime == 0) {
			return 0.0f;
		}

		long currentTime = System.currentTimeMillis();
		long elapsed = currentTime - deathStartTime;

		float timeInSeconds = elapsed / 1000.0f;

		if (timeInSeconds >= 10.0f) {
			return 1.0f;
		}

		float progress = timeInSeconds / 10.0f;

		if (progress <= (8.57f / 10.0f)) {
			return progress * (10.0f / 8.57f) * 0.9f;
		} else {
			float finalPhase = (progress - (8.57f / 10.0f)) / (1.43f / 10.0f);
			float smoothEnd = 1.0f - (float) Math.pow(1.0f - finalPhase, 2.0f);
			return 0.9f + (0.1f * smoothEnd);
		}
	}
}