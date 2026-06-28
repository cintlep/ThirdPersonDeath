package cintlex.thirdpersondeath.mixin;

import cintlex.thirdpersondeath.BedrockDeathScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class tpdclientpacketlistener {

    @Inject(method = "handlePlayerCombatKill", at = @At("HEAD"), cancellable = true)
    private void onHandlePlayerCombatKill(ClientboundPlayerCombatKillPacket packet, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client != null) {
            boolean hardcore = false;
            if (client.level != null) {
                hardcore = client.level.getLevelData().isHardcore();
            }
            if (client.player != null && !client.player.shouldShowDeathScreen()) {
                client.player.respawn();
                ci.cancel();
                return;
            }
            client.setScreen(new BedrockDeathScreen(packet.message(), hardcore));
            ci.cancel();
        }
    }
}
