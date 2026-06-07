package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Unique
    private final boolean allowInteractionsFromPlayer = true;

    @Inject(method = "getViewXRot", at = @At("HEAD"), cancellable = true)
    private void redirectViewXRot(float partialTick, CallbackInfoReturnable<Float> ci) {
        if(ExternalTeleport.enabled && !allowInteractionsFromPlayer) ci.setReturnValue(ExternalTeleport.fakeCamera.getViewXRot(partialTick));
    }

    @Inject(method = "getViewYRot", at = @At("HEAD"), cancellable = true)
    private void redirectViewYRot(float partialTick, CallbackInfoReturnable<Float> ci) {
        if(ExternalTeleport.enabled && !allowInteractionsFromPlayer) ci.setReturnValue(ExternalTeleport.fakeCamera.getViewYRot(partialTick));
    }
}
