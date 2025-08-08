package cintlex.thirdpersondeath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.screens.DeathScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("thirdpersondeath")
public class ThirdPersonDeath {
    private static boolean deathscreen = false; private static CameraType playerperspective = null; private static long DeathTime = 0; private static boolean zoom = false;
    public ThirdPersonDeath() {if (FMLEnvironment.dist == Dist.CLIENT) {}
    }
    public static void detectscreen() {Minecraft client = Minecraft.getInstance(); if (client.screen instanceof DeathScreen) {if (!deathscreen) {screenisdeath(client);}
        deathscreen = true;
    } else {if (deathscreen) {screenisnotdeath(client);}
        deathscreen = false;}
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
        if (seconds >= 10.0f) {return 1.0f;}
        float progress = seconds / 10.0f; if (progress <= (8.57f / 10.0f)) {return progress * (10.0f / 8.57f) * 0.9f;}
        else {float smoothout = (progress - (8.57f / 10.0f)) / (1.43f / 10.0f); float end = 1.0f - (float) Math.pow(1.0f - smoothout, 2.0f); return 0.9f + (0.1f * end);}
    }
}