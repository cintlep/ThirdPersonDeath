package com.cintlex.tpdeath.mixin;

import com.cintlex.tpdeath.ThirdPersonDeath;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(
            method = "getFov",
            at = @At("RETURN"),
            cancellable = true
    )
    private void setDeathScreenFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if (ThirdPersonDeath.isZoomEffectActive()) {
            float zoomProgress = ThirdPersonDeath.getZoomProgress();

            double startFov = 80.0;
            double endFov = 105.0;
            double currentFov = startFov + (endFov - startFov) * zoomProgress;

            cir.setReturnValue(currentFov);
        }
    }
}