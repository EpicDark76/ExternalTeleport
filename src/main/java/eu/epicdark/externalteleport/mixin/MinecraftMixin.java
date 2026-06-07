package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.security.auth.callback.Callback;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "disconnect*", at = @At("HEAD"))
    private void onDsconnect(Screen screen, boolean keepResourcePacks, CallbackInfo ci) {
        ExternalTeleport.onDisconnect();
    }
}
