package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import eu.epicdark.externalteleport.ModConfig;
import eu.epicdark.externalteleport.util.FakeCamera;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    // Prevents shadow being cast when Iris is enabled + prevent player render if toggled to invis
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void onShouldRender(Entity entity, Frustum culler, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof FakeCamera) {
            cir.setReturnValue(false);
        }else if(entity == ExternalTeleport.MC.player && ExternalTeleport.enabled && ExternalTeleport.config.showPlayer == ModConfig.RenderState.INVISIBLE) {
            cir.setReturnValue(false);
        }
    }
}
