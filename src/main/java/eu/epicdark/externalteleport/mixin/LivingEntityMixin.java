package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    public abstract float getHealth();

    @Inject(method = "setHealth", at = @At("HEAD"))
    private void onHealthUpdate(float health, CallbackInfo ci) {
        if(ExternalTeleport.enabled && this.equals(ExternalTeleport.MC.player) && ExternalTeleport.config.disableOnDamage) {
            if(!ExternalTeleport.MC.player.isCreative() && getHealth()>health) {
                ExternalTeleport.onToggleMode();
            }
        }
    }

}
