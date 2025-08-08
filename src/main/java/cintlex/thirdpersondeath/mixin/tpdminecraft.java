package cintlex.thirdpersondeath.mixin;
import cintlex.thirdpersondeath.ThirdPersonDeath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Minecraft.class)
public class tpdminecraft {@Inject(method = "setScreen", at = @At("TAIL"))
private void onSetScreen(Screen screen, CallbackInfo ci) {ThirdPersonDeath.detectscreen();}
}