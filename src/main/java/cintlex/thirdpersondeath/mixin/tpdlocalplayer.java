package cintlex.thirdpersondeath.mixin;

import cintlex.thirdpersondeath.BedrockDeathScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class tpdlocalplayer {

    @Inject(method = "isDeadOrDying", at = @At("HEAD"), cancellable = true)
    private void keepPlayerAliveForBedrockDeath(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof BedrockDeathScreen) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    private void keepHealthForDeathScreen(CallbackInfoReturnable<Float> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof BedrockDeathScreen) {
            // return at least a tiny bit so renderer treats as alive
            cir.setReturnValue(Math.max(1.0f, cir.getReturnValue()));
        }
    }
}