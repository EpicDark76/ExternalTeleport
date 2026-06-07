package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getViewXRot(F)F"))
    private float redirectViewXRot(LocalPlayer player, float a) {
        return ExternalTeleport.enabled ? ExternalTeleport.fakeCamera.getViewXRot(a) : player.getViewXRot(a);
    }

    @Redirect(method = "renderHandsWithItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getViewYRot(F)F"))
    private float redirectViewYRot(LocalPlayer player, float a) {
        return ExternalTeleport.enabled ? ExternalTeleport.fakeCamera.getViewYRot(a) : player.getViewYRot(a);
    }
}
