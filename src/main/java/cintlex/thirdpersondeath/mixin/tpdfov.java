package cintlex.thirdpersondeath.mixin;
import cintlex.thirdpersondeath.ThirdPersonDeath;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class tpdfov {@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
private void scalefovondeathscreen(Camera camera, float tick, boolean scalefov, CallbackInfoReturnable<Double> cir) {
    if (ThirdPersonDeath.isZoom()) {float progress = ThirdPersonDeath.zoomprogress(); double fovs = 90.0; double fove = 115.0; double playerfov = fovs + (fove - fovs) * progress; cir.setReturnValue(playerfov);}
}
}