package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void preventAttack(CallbackInfoReturnable<Boolean> ci) {
        if(ExternalTeleport.enabled) ci.cancel();
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void preventBlockBreak(CallbackInfo ci) {
        if(ExternalTeleport.enabled) ci.cancel();
    }

    @Inject(method = "pickBlockOrEntity", at = @At("HEAD"), cancellable = true)
    private void preventAttack(CallbackInfo ci) {
        if(ExternalTeleport.enabled) ci.cancel();
    }

    @ModifyVariable(method = "pick(F)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/Minecraft;getCameraEntity()Lnet/minecraft/world/entity/Entity;"), name = "cameraEntity")
    private Entity hitTargetSource(Entity cameraEntity) {
        if(ExternalTeleport.enabled && ExternalTeleport.interactionsFromPlayer) {
            return ExternalTeleport.MC.player;
        }
        return cameraEntity;
    }

    @Inject(method = "disconnect*", at = @At("HEAD"))
    private void onDsconnect(Screen screen, boolean keepResourcePacks, CallbackInfo ci) {
        ExternalTeleport.onDisconnect();
    }
}
