package cintlex.thirdpersondeath.mixin;

import cintlex.thirdpersondeath.BedrockDeathScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class tpdclientpacketlistener {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "handlePlayerCombatKill", at = @At("HEAD"), cancellable = true)
    private void onHandlePlayerCombatKill(ClientboundPlayerCombatKillPacket packet, CallbackInfo ci) {
        if (this.minecraft != null) {
            boolean hardcore = false;
            if (this.minecraft.level != null) {
                hardcore = this.minecraft.level.getLevelData().isHardcore();
            }
            // Also respect shouldShowDeathScreen like vanilla if player present
            if (this.minecraft.player != null && !this.minecraft.player.shouldShowDeathScreen()) {
                this.minecraft.player.respawn();
                ci.cancel();
                return;
            }
            this.minecraft.setScreen(new BedrockDeathScreen(packet.message(), hardcore));
            ci.cancel();
        }
    }
}
