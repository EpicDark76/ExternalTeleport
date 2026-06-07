package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    // Prevents interacting with blocks
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (ExternalTeleport.enabled) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    // Prevents interacting with entities
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteractEntity(
            Player player,
            Entity entity,
            EntityHitResult hitResult,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (entity.equals(ExternalTeleport.MC.player) || ExternalTeleport.enabled) {
            cir.setReturnValue(InteractionResult.PASS);
        }
    }

    //prevent attacking self
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(Player player, Entity target, CallbackInfo ci) {
        if(target.equals(ExternalTeleport.MC.player)) ci.cancel();
    }
}
