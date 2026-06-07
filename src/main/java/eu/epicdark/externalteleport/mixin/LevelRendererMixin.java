package eu.epicdark.externalteleport.mixin;

import eu.epicdark.externalteleport.ExternalTeleport;
import eu.epicdark.externalteleport.ModConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    protected abstract EntityRenderState extractEntity(Entity e, float g);

    //render player model when viewing from camera
    @Inject(method = "extractVisibleEntities", at = @At("RETURN"))
    private void extractSelfPlayer(Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState renderState, CallbackInfo ci) {
        if(ExternalTeleport.MC.level != null && ExternalTeleport.enabled && ExternalTeleport.config.showPlayer != ModConfig.RenderState.INVISIBLE) {
            Entity player = ExternalTeleport.MC.player;
            TickRateManager tickRateManager = ExternalTeleport.MC.level.tickRateManager();
            float g = deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(player));
            EntityRenderState entityRenderState = this.extractEntity(player, g);
            if(ExternalTeleport.config.showPlayer == ModConfig.RenderState.TRANSPARENT) entityRenderState.isInvisible = true;
            renderState.entityRenderStates.add(entityRenderState);
        }
    }

}
