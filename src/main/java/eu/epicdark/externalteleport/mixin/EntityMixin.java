package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	public abstract boolean equals(Object obj);

	//makes mouse input rotate the fake camera
	@Inject(method = "turn", at = @At("HEAD"), cancellable = true)
	private void fakeTurn(double rotation, double pitch, CallbackInfo ci) {
		if(ExternalTeleport.enabled && this.equals(ExternalTeleport.MC.player)) {
			ExternalTeleport.fakeCamera.turn(rotation, pitch);
			ci.cancel();
		}
	}

	// Prevents fakeCamera from pushing/getting pushed by entities.
	@Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void onPushAwayFrom(Entity entity, CallbackInfo ci) {
		if (ExternalTeleport.enabled && (entity.equals(ExternalTeleport.fakeCamera) || this.equals(ExternalTeleport.fakeCamera))) {
			ci.cancel();
		}
	}
}