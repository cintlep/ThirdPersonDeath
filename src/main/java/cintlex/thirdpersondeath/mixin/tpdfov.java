package cintlex.thirdpersondeath.mixin;
import cintlex.thirdpersondeath.ThirdPersonDeath;
import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
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
	@Shadow private Vec3 position;
	@Shadow private net.minecraft.world.entity.Entity entity;

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
			// Yaw + pitch drift for cinematic lean
			this.yRot += tilt * 0.85f;
			this.xRot += tilt * 0.2f;
		}
	}

	@Inject(method = "update", at = @At("RETURN"))
	private void applySmoothDeathCamPullback(net.minecraft.client.DeltaTracker delta, CallbackInfo ci) {
		if (ThirdPersonDeath.isZoom()) {
			float dist = ThirdPersonDeath.deathCamDist();
			if (dist > 0.01f && this.entity != null) {
				// Force detached for 3p body view
				this.detached = true;
				// Calculate eye position
				Vec3 entityPos = this.entity.position();
				float eyeH = this.entity.getEyeHeight();
				Vec3 eye = entityPos.add(0, eyeH, 0);
				// Look direction (forward)
				Vec3 look = this.entity.getViewVector(1.0f);
				// Pull camera back from eye along -look
				Vec3 newPos = eye.add(look.scale(-dist));
				this.position = newPos;
			}
		}
	}
}
