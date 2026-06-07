package eu.epicdark.externalteleport;

import com.mojang.blaze3d.platform.InputConstants;
import eu.epicdark.externalteleport.util.FakeCamera;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalTeleport implements ClientModInitializer {
	public static final String MOD_ID = "externalteleport";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final KeyMapping KEY_MODE = KeyMappingHelper.registerKeyMapping(new KeyMapping("key." + MOD_ID + ".toggle_mode", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, KeyMapping.Category.MOVEMENT));
	public static final KeyMapping KEY_TELEPORT = KeyMappingHelper.registerKeyMapping(new KeyMapping("key." + MOD_ID + ".teleport", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, KeyMapping.Category.MOVEMENT));

	public static final boolean interactionsFromPlayer = true;
	public static final Identifier INVERT = Identifier.fromNamespaceAndPath("minecraft", "invert");

	public static Minecraft MC;
	public static ModConfig config;
	public static boolean enabled = false;
	public static FakeCamera fakeCamera;
	private static CameraType previousCam;

	@Override
	public void onInitializeClient() {
		MC = Minecraft.getInstance();
		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			if(!enabled) return;
			if(client.player == null) return;
			if(!(client.player.input instanceof KeyboardInput)) return;

			ClientInput input = new ClientInput();
			Input keyPresses = client.player.input.keyPresses;
			input.keyPresses = new Input(false, false, false, false, false, keyPresses.shift(), false);
			client.player.input = input;
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if(client.player == null) return;

			if(enabled && client.level.getGameTime() % 5 == 0) spawnParticleLine(client);

			if(client.screen != null) return;

			while(KEY_MODE.consumeClick()) {
				if(client.player.gameMode() == GameType.SPECTATOR) {
					client.player.sendOverlayMessage(Component.translatable("msg.externalteleport.no_spectator").withStyle(ChatFormatting.RED));
					return;
				}
				onToggleMode();
			}


			while(KEY_TELEPORT.consumeClick()) {
				if(!enabled) {
					client.player.sendOverlayMessage(Component
							.translatable("msg.externalteleport.no_prepare", Component.keybind(KEY_MODE.getName()))
							.withStyle(ChatFormatting.RED));
					return;
				}
				if(client.player.gameMode() == GameType.SPECTATOR) return;

				if(fakeCamera == null) {
					client.player.sendOverlayMessage(Component.translatable("msg.externalteleport.no_camera").withStyle(ChatFormatting.RED));
					return;
				}
				onTeleport();
			}
		});
	}

	private static void spawnParticleLine(Minecraft client) {
		if(fakeCamera == null) return;
		Vec3 pos1 = client.player.getEyePosition(0.5f);
		Vec3 pos2 = fakeCamera.getEyePosition(0.5f);
		double distance = client.player.distanceTo(fakeCamera);
		if(distance<=0) return;
		double spacing = 1 / (0.03*distance);
		Vec3 dir = pos1.subtract(pos2).normalize().scale(spacing);
		for(double i = 0; i < distance; i+= spacing) {
			Vec3 currentPos = pos1.subtract(dir.scale(i / spacing));
			client.level.addParticle(ParticleTypes.END_ROD, currentPos.x, currentPos.y, currentPos.z, 0, 0, 0);
		}
	}

	public static void onToggleMode() {
		if(enabled) {
			onDisable();
		}else{
			onEnable();
		}
	}

	private static void onEnable() {
		previousCam = MC.options.getCameraType();
		if(MC.gameRenderer.getMainCamera().isDetached()) {
			MC.options.setCameraType(CameraType.FIRST_PERSON);
		}

		fakeCamera = new FakeCamera(-420);
		fakeCamera.copyPosition(MC.player);
		fakeCamera.spawn();
		if(config.playSound) MC.player.playSound(SoundEvents.BEACON_ACTIVATE, 1, 1);
		MC.setCameraEntity(fakeCamera);
		MC.gameRenderer.setPostEffect(INVERT);
		enabled = true;
		MC.player.sendOverlayMessage(Component.translatable("msg.externalteleport.enabled", Component.keybind(KEY_TELEPORT.getName())));
	}

	private static void onTeleport() {
		if(fakeCamera == null) return;
		if(!MC.player.permissions().hasPermission(Permissions.COMMANDS_MODERATOR)) {
			MC.player.sendOverlayMessage(Component.translatable("msg.externalteleport.no_permission").withStyle(ChatFormatting.RED));
			return;
		}

		String command = config.command
				.replaceFirst("\\$x", "" + fakeCamera.getX())
				.replaceFirst("\\$y", "" + fakeCamera.getY())
				.replaceFirst("\\$z", "" + fakeCamera.getZ())
				.replaceFirst("\\$yaw", "" + fakeCamera.getYRot())
				.replaceFirst("\\$pitch", "" + fakeCamera.getXRot());

		MC.player.connection.sendCommand(command);
		MC.player.sendOverlayMessage(Component.translatable("msg.externalteleport.teleport"));
		onDisable(false);
		if(config.playSound) {
			//MC.player.playSound(SoundEvents.PLAYER_TELEPORT, 1, 1);
			MC.level.playSound(null, MC.player.blockPosition(), SoundEvents.PLAYER_TELEPORT, SoundSource.PLAYERS, 1, 1);
		}
	}

	private static void onDisable() {
		onDisable(true);
	}

	private static void onDisable(boolean msg) {
		if(fakeCamera == null) return;

		MC.gameRenderer.clearPostEffect();
		MC.setCameraEntity(MC.player);
		fakeCamera.despawn();
		fakeCamera.input = new ClientInput();
		fakeCamera = null;
		MC.player.input = new KeyboardInput(MC.options);
		if(previousCam != null) MC.options.setCameraType(previousCam);
		enabled = false;
		if(msg) {
			MC.player.sendOverlayMessage(Component.translatable("msg.externalteleport.disabled"));
			if(config.playSound) MC.player.playSound(SoundEvents.BEACON_DEACTIVATE, 1, 1);
		}
	}

	public static void onDisconnect() {
		if(enabled) {
			onDisable();
		}
	}
}