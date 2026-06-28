package cintlex.thirdpersondeath.mixin;
import cintlex.thirdpersondeath.ThirdPersonDeath;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class tpdfov {
	@Shadow private boolean detached;
	@Shadow private float yRot;
	@Shadow private float xRot;

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

	// Apply cinematic tilt (lean) after the camera has done its normal rotation setup.
	// This gives the Bedrock-style slight held tilt in third person.
	@Inject(method = "setRotation", at = @At("RETURN"))
	private void applyCinematicDeathTilt(float originalYRot, float originalXRot, CallbackInfo ci) {
		if (ThirdPersonDeath.isZoom() && detached) {
			float tilt = ThirdPersonDeath.getTilt();
			// Slow yaw drift + slight extra downward pitch for a nice "cinematic held tilt"
			this.yRot += tilt * 0.9f;
			this.xRot += tilt * 0.25f;
		}
	}
}
