package cintlex.thirdpersondeath;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Screen;
import cintlex.thirdpersondeath.BedrockDeathScreen;

public class ThirdPersonDeath implements ClientModInitializer {
	private static boolean deathscreen = false; private static CameraType playerperspective = null; private static long DeathTime = 0; private static boolean zoom = false;
	@Override
	public void onInitializeClient() {}
	public static void detectscreen() {Minecraft client = Minecraft.getInstance(); if (isDeathScreen(client.screen)) {if (!deathscreen) {screenisdeath(client);}
		deathscreen = true;
	} else {if (deathscreen) {screenisnotdeath(client);}
		deathscreen = false;
	}
	}

	private static boolean isDeathScreen(Screen screen) {
		if (screen instanceof DeathScreen) return true;
		return screen instanceof BedrockDeathScreen;
	}
	private static void screenisdeath(Minecraft client) {playerperspective = client.options.getCameraType(); client.options.setCameraType(CameraType.THIRD_PERSON_BACK); DeathTime = System.currentTimeMillis();
		zoom = true;
	}

	private static void screenisnotdeath(Minecraft client) {if (playerperspective != null) {client.options.setCameraType(playerperspective); playerperspective = null;}
		zoom = false; DeathTime = 0;
	}

	public static boolean isZoom() {return zoom;}
	public static float zoomprogress() {if (!zoom || DeathTime == 0) {return 0.0f;}
		long time = System.currentTimeMillis(); long passed = time - DeathTime; float seconds = passed / 1000.0f;
		if (seconds >= 18.0f) {return 1.0f;}
		float progress = seconds / 18.0f; if (progress <= (8.57f / 18.0f)) {return progress * (18.0f / 8.57f) * 0.9f;}
		else {float smoothout = (progress - (8.57f / 18.0f)) / (1.43f / 18.0f); float end = 1.0f - (float) Math.pow(1.0f - smoothout, 2.0f); return 0.9f + (0.1f * end);}
	}

	/** Cinematic camera tilt (degrees) that eases in and holds, matching Bedrock death cam lean. */
	public static float getTilt() {
		if (!zoom || DeathTime == 0) return 0.0f;
		float p = zoomprogress();
		// ease in a nice lean and hold around 10-12 degrees
		float tilt = Math.min(p, 0.85f) / 0.85f;
		return tilt * 11.0f;
	}
}
