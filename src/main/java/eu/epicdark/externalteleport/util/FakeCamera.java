package eu.epicdark.externalteleport.util;

import com.mojang.authlib.GameProfile;
import eu.epicdark.externalteleport.ExternalTeleport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec2;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class FakeCamera extends AbstractClientPlayer {

    public ClientInput input;

    public FakeCamera(int id) {
        super(ExternalTeleport.MC.level, new GameProfile(UUID.randomUUID(), "FakeCamera"));

        setId(id);
        getAbilities().mayfly = true;
        getAbilities().mayBuild = false;
        input = new KeyboardInput(ExternalTeleport.MC.options);
    }

    @Override
    public void copyPosition(Entity target) {
        snapTo(target.getX(), target.getY(), target.getZ(), target.getYRot(), target.getXRot());
    }

    public void spawn() {
        clientLevel().addEntity(this);
    }

    public void despawn() {
        clientLevel().removeEntity(getId(), RemovalReason.DISCARDED);
    }

    private ClientLevel clientLevel() {
        return (ClientLevel) level();
    }

    @Override
    public void tick() {
        input.tick();
        super.tick();
    }

    @Override
    protected void checkFallDamage(double ya, boolean onGround, BlockState onState, BlockPos pos) {}

    @Override
    public float getAttackAnim(float a) {
        return Minecraft.getInstance().player.getAttackAnim(a);
    }

    @Override
    public int getUseItemRemainingTicks() {
        return Minecraft.getInstance().player.getUseItemRemainingTicks();
    }

    @Override
    public boolean isUsingItem() {
        return Minecraft.getInstance().player.isUsingItem();
    }

    @Override
    public float getViewXRot(float a) {
        return this.getXRot();
    }

    @Override
    public float getViewYRot(float a) {
        return this.getYRot();
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    public boolean isEffectiveAi() {
        return true;
    }

    @Override
    public boolean canSimulateMovement() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    protected void doWaterSplashEffect() {}

    @Override
    public @Nullable MobEffectInstance getEffect(Holder<MobEffect> effect) {
        return ExternalTeleport.MC.player.getEffect(effect);
    }

    @Override
    public boolean hasEffect(Holder<MobEffect> effect) {
        return ExternalTeleport.MC.player.hasEffect(effect);
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    protected void applyInput() {
        Vec2 vec2 = this.input.getMoveVector();
        if(vec2.lengthSquared() != 0.0f) {
            vec2 = vec2.scale(0.98f);
        }

        this.xxa = vec2.x;
        this.zza = vec2.y;
        this.jumping = this.input.keyPresses.jump();
        this.setSprinting((ExternalTeleport.MC.options.keySprint.isDown() && this.input.keyPresses.forward()) || (this.input.keyPresses.forward() && this.isSprinting()));
    }
}
