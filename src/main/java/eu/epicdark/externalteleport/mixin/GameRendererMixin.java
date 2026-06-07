package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    //prevent first-person hand rendering
    @Inject(method = "renderItemInHand", at = @At("HEAD"), cancellable = true)
    private void preventHandRender(CallbackInfo ci) {
        if(ExternalTeleport.enabled && !ExternalTeleport.config.showHand) ci.cancel();
    }

    //prevent block outlines
    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true)
    private void preventBlockOutlines(CallbackInfoReturnable<Boolean> ci) {
        if(ExternalTeleport.enabled) ci.setReturnValue(false);
    }
}
