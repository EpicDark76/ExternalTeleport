package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {

    //prevent F5 when in camera
    @Inject(method = "setCameraType", at = @At("HEAD"), cancellable = true)
    private void preventPerspectiveChange(CallbackInfo ci) {
        if(ExternalTeleport.enabled) ci.cancel();
    }

}
