package cintlex.thirdpersondeath.mixin;
import cintlex.thirdpersondeath.ThirdPersonDeath;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class tpdfov {
	@Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
	private void scalefovondeathscreen(CallbackInfoReturnable<Float> cir) {
		if (ThirdPersonDeath.isZoom()) {
			float progress = ThirdPersonDeath.zoomprogress();
			float fovs = 90.0f;
			float fove = 115.0f;
			float playerfov = fovs + (fove - fovs) * progress;
			cir.setReturnValue(playerfov);
		}
	}
}
