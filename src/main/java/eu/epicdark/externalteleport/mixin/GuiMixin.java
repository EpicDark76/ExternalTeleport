package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public class GuiMixin {

    //make hud relate to player instead of camera entity
    @Inject(method = "getCameraPlayer", at = @At("HEAD"), cancellable = true)
    private void getFakeCamera(CallbackInfoReturnable<Player> ci) {
        if(ExternalTeleport.enabled) ci.setReturnValue(ExternalTeleport.MC.player);
    }

    //prevent equipped-item overlays (e.g. pumpkin blur)
    @Inject(method = "extractTextureOverlay", at = @At("HEAD"), cancellable = true)
    private void preventItemOverlay(GuiGraphicsExtractor graphics, Identifier texture, float alpha, CallbackInfo ci) {
        if(ExternalTeleport.enabled) ci.cancel();
    }

}
