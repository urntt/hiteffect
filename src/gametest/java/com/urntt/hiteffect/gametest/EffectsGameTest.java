package com.urntt.hiteffect.gametest;

import static com.urntt.hiteffect.gametest.GameTestSupport.check;
import static com.urntt.hiteffect.gametest.GameTestSupport.countFlashPixels;

import com.mojang.blaze3d.platform.InputConstants;
import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.compat.OneConfigBridge;
import com.urntt.hiteffect.config.ConfigMode;
import com.urntt.hiteffect.config.EffectValues;
import com.urntt.hiteffect.config.FlashPart;
import com.urntt.hiteffect.config.HitEffectConfig;
import com.urntt.hiteffect.config.HurtColors;
import com.urntt.hiteffect.effect.HurtOverlayColor;
import com.urntt.hiteffect.gui.HitEffectConfigScreen;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks the open-configuration key and the effects in a world: the hurt overlay color, which parts of a hurt entity and of the first-person view
 * flash (by counting pixels of the hurt color in screenshots), and the hit particles.
 */
@SuppressWarnings("UnstableApiUsage")
public final class EffectsGameTest implements FabricClientGameTest {
	private static final Logger LOGGER = LoggerFactory.getLogger("hiteffect-gametest");

	/** Vanilla's hurt rows hold this ARGB value. */
	private static final int VANILLA_OVERLAY = 0xB2FF0000;

	/** The flash tests use pure blue at full strength, which replaces the texture color of flashing parts. */
	private static final int FLASH_TEST_COLOR = 0x0000FF;

	/** A flashing part must add at least this share of the screenshot's pixels in the hurt color. */
	private static final double MIN_FLASH_GAIN = 0.005;

	/** The same for the held sword, which covers far fewer pixels than armor or a carved pumpkin. */
	private static final double MIN_HELD_ITEM_FLASH_GAIN = 0.001;

	/** Ticks to wait until the target can be hurt again and the player's attack is fully charged. */
	private static final int ATTACK_COOLDOWN_TICKS = 30;

	/** Ticks to wait after a hit for vanilla's animation packets to arrive. */
	private static final int PACKET_SETTLE_TICKS = 5;

	/** Selects the test zombie; {@code damage} accepts only a single target. */
	private static final String ZOMBIE_SELECTOR = "@e[type=minecraft:zombie,limit=1]";

	@Override
	public void runTest(final ClientGameTestContext context) {
		if (FabricLoader.getInstance().isModLoaded(OneConfigBridge.MOD_ID)) {
			// OneConfig's notifications cover parts of the screenshots, and these checks do not depend on OneConfig;
			// the run with OneConfig covers its integration in ConfigScreenGameTest.
			LOGGER.info("Skipping the effect checks because OneConfig is installed");
			return;
		}

		configure(context, c -> {
			c.setMode(ConfigMode.CUSTOM);
			applyDefaults(c);
		});

		checkOverlayTexture(context);

		try (TestSingleplayerContext singleplayer = context.worldBuilder().create()) {
			singleplayer.getConnection().waitForChunksRender();
			TestServerContext server = singleplayer.getServer();
			server.runCommand("gamemode survival @a");
			server.runCommand("time set noon");
			context.waitFor(client -> client.player.onGround());

			checkOpenConfigKey(context);
			checkEquipmentAndItemFlash(context, server);
			checkFirstPersonArmFlash(context, server);
			checkHitParticles(context, server);
		}

		configure(context, EffectsGameTest::applyDefaults);
	}

	/** Key bindings only work in a world, so the open-configuration key is checked here. */
	private static void checkOpenConfigKey(final ClientGameTestContext context) {
		KeyMapping openKey = Objects.requireNonNull(KeyMapping.get(HitEffectClient.OPEN_CONFIG_KEY_NAME), "open config key mapping");
		check(openKey.getDefaultKey().equals(InputConstants.UNKNOWN), "the open config key should be unbound by default");
		context.runOnClient(client -> {
			openKey.setKey(InputConstants.getKey("key.keyboard.j"));
			KeyMapping.resetMapping();
		});
		context.getInput().pressKey(openKey);
		context.waitForScreen(HitEffectConfigScreen.class);
		context.takeScreenshot("hiteffect-config-in-game");
		context.setScreen(() -> null);
		context.runOnClient(client -> {
			openKey.setKey(openKey.getDefaultKey());
			KeyMapping.resetMapping();
		});
	}

	private static void checkOverlayTexture(final ClientGameTestContext context) {
		check(overlayArgb(context) == VANILLA_OVERLAY,
				"the default hurt color should match vanilla, was " + Integer.toHexString(overlayArgb(context)));

		configure(context, c -> {
			c.setHurtColor(0x00FF00);
			c.setHurtStrength(50);
		});
		int custom = overlayArgb(context);
		check(custom == 0x7F00FF00, "green at 50 % should give 0x7f00ff00, was " + Integer.toHexString(custom));

		configure(context, c -> c.setMode(ConfigMode.DISABLED));
		check(overlayArgb(context) == VANILLA_OVERLAY, "disabled mode should restore vanilla's hurt color");

		configure(context, c -> {
			c.setMode(ConfigMode.CUSTOM);
			c.setRainbow(true);
		});
		int first = overlayArgb(context);
		context.waitTicks(10);
		int second = overlayArgb(context);
		check(first != second, "the rainbow should change the hurt color over time");
		LOGGER.info("Hurt overlay: custom {}, rainbow {} -> {}", Integer.toHexString(custom), Integer.toHexString(first), Integer.toHexString(second));

		configure(context, EffectsGameTest::applyDefaults);
		check(overlayArgb(context) == VANILLA_OVERLAY, "the default hurt color should be vanilla again");
	}

	/**
	 * Freezes a hurt zombie that wears armor and a carved pumpkin and holds a sword, then counts the pixels of the
	 * hurt color with each of these parts flashing on its own.
	 */
	private static void checkEquipmentAndItemFlash(final ClientGameTestContext context, final TestServerContext server) {
		Vec3 player = facePositiveZ(context, server, 0);
		server.runCommand(String.format(Locale.ROOT,
				"summon minecraft:zombie %.2f %.2f %.2f {NoAI:1b,Silent:1b,PersistenceRequired:1b,Rotation:[180f,0f],"
						+ "equipment:{head:{id:\"minecraft:carved_pumpkin\"},chest:{id:\"minecraft:netherite_chestplate\"},"
						+ "legs:{id:\"minecraft:netherite_leggings\"},feet:{id:\"minecraft:netherite_boots\"},"
						+ "mainhand:{id:\"minecraft:iron_sword\"}}}",
				player.x, player.y, player.z + 2.5));
		context.waitFor(client -> zombie(client) != null);
		server.runCommand("tick freeze");
		server.runCommand("damage " + ZOMBIE_SELECTOR + " 1");
		context.waitFor(client -> zombie(client).hurtTime > 0);

		int none = flashPixels(context, "hiteffect-flash-none");
		int armor = flashPixels(context, "hiteffect-flash-armor", FlashPart.ARMOR);
		int held = flashPixels(context, "hiteffect-flash-held-items", FlashPart.HELD_ITEMS);
		int head = flashPixels(context, "hiteffect-flash-head-items", FlashPart.HEAD_ITEMS);
		int all = flashPixels(context, "hiteffect-flash-all", FlashPart.values());
		LOGGER.info("Hurt color pixels: body only {}, with armor {}, with held items {}, with head items {}, with all parts {}",
				none, armor, held, head, all);

		int minGain = minFlashGain(context, MIN_FLASH_GAIN);
		int minItemGain = minFlashGain(context, MIN_HELD_ITEM_FLASH_GAIN);
		check(armor - none >= minGain, "armor should flash on its own: " + none + " -> " + armor + " pixels, expected a gain of " + minGain);
		check(head - none >= minGain, "the carved pumpkin should flash on its own: " + none + " -> " + head + " pixels, expected a gain of " + minGain);
		check(held - none >= minItemGain, "the held sword should flash on its own: " + none + " -> " + held + " pixels, expected a gain of " + minItemGain);
		check(all - armor >= minItemGain && all - head >= minItemGain && all - held >= minGain,
				"armor and items should flash together: " + all + " pixels");

		server.runCommand("tick unfreeze");
		server.runCommand("kill " + ZOMBIE_SELECTOR);
		context.waitFor(client -> zombie(client) == null);
	}

	/** Takes a screenshot with only {@code parts} flashing and counts the pixels of the hurt color. */
	private static int flashPixels(final ClientGameTestContext context, final String name, final FlashPart... parts) {
		Set<FlashPart> flashing = Set.of(parts);
		configure(context, c -> {
			c.setHurtColor(FLASH_TEST_COLOR);
			c.setHurtStrength(HurtColors.MAX_STRENGTH);
			for (FlashPart part : FlashPart.values()) {
				c.setFlashes(part, flashing.contains(part));
			}
		});
		Path screenshot = context.takeScreenshot(name);
		return countFlashPixels(screenshot, 0, 0, 1, 1);
	}

	/**
	 * Hurts the player with an empty hand in first person and counts the pixels of the hurt color where the arm is
	 * drawn, with the arm flash on and off.
	 */
	private static void checkFirstPersonArmFlash(final ClientGameTestContext context, final TestServerContext server) {
		facePositiveZ(context, server, 60);
		context.runOnClient(client -> client.options.setCameraType(CameraType.FIRST_PERSON));

		int on = armPixels(context, server, "hiteffect-arm-flash-on", true);
		int off = armPixels(context, server, "hiteffect-arm-flash-off", false);
		LOGGER.info("Hurt color pixels in the first-person arm area: flash on {}, flash off {}", on, off);
		check(on - off >= minFlashGain(context, MIN_FLASH_GAIN), "the first-person arm should flash: " + off + " -> " + on + " pixels");
	}

	private static int armPixels(final ClientGameTestContext context, final TestServerContext server, final String name, final boolean flash) {
		configure(context, c -> {
			c.setHurtColor(FLASH_TEST_COLOR);
			c.setHurtStrength(HurtColors.MAX_STRENGTH);
			c.setFlashes(FlashPart.FIRST_PERSON_ARM, flash);
		});
		context.waitFor(client -> client.player.hurtTime == 0 && client.player.getInvulnerableTime() == 0);
		context.waitTicks(ATTACK_COOLDOWN_TICKS);
		server.runCommand("damage @p 1");
		context.waitFor(client -> client.player.hurtTime > 0);
		Path screenshot = context.takeScreenshot(name);
		// The right hand is drawn in the lower right quarter of the screen.
		return countFlashPixels(screenshot, 0.5, 0.5, 1, 1);
	}

	/**
	 * Hits a zombie with the attack key and counts the hit particle emitters: with the hand empty, with a Sharpness
	 * sword, whose enchanted hit particles vanilla already shows, and on a target the server does not hurt.
	 */
	private static void checkHitParticles(final ClientGameTestContext context, final TestServerContext server) {
		server.runCommand("effect give @p minecraft:instant_health 1 10");
		Vec3 player = facePositiveZ(context, server, 0);
		server.runCommand(String.format(Locale.ROOT,
				"summon minecraft:zombie %.2f %.2f %.2f {NoAI:1b,Silent:1b,PersistenceRequired:1b,Rotation:[180f,0f],"
						+ "Health:1000f,attributes:[{id:\"minecraft:max_health\",base:1000d}],"
						+ "equipment:{head:{id:\"minecraft:carved_pumpkin\"}}}",
				player.x, player.y, player.z + 2.0));
		context.waitFor(client -> zombie(client) != null);

		configure(context, EffectsGameTest::applyDefaults);
		checkHit(context, "empty hand, defaults", 1, 0, true);

		configure(context, c -> c.setCritParticles(true));
		checkHit(context, "empty hand, crit particles on", 1, 1, true);

		configure(context, c -> {
			c.setSharpnessParticles(false);
			c.setCritParticles(false);
		});
		checkHit(context, "empty hand, both off", 0, 0, true);

		server.runCommand("item replace entity @p weapon.mainhand with minecraft:diamond_sword[minecraft:enchantments={\"minecraft:sharpness\":5}]");
		context.waitFor(client -> !client.player.getMainHandItem().isEmpty());

		// Vanilla's own sharpness particle needs the server, so it only appears after the round trip.
		configure(context, c -> c.setMode(ConfigMode.DISABLED));
		checkHit(context, "Sharpness sword, disabled", 1, 0, true);

		configure(context, c -> {
			c.setMode(ConfigMode.CUSTOM);
			applyDefaults(c);
		});
		checkHit(context, "Sharpness sword, defaults", 1, 0, true);

		// The server ignores attacks on an invulnerable zombie, so any particle can only come from the client.
		server.runCommand("item replace entity @p weapon.mainhand with minecraft:air");
		server.runCommand("data merge entity " + ZOMBIE_SELECTOR + " {Invulnerable:1b}");
		context.waitTicks(PACKET_SETTLE_TICKS);
		checkHit(context, "empty hand, target the server does not hurt", 1, 0, false);

		server.runCommand("kill " + ZOMBIE_SELECTOR);
	}

	/**
	 * Attacks the zombie with the attack key and counts the hit particle emitters right after the attack and once the
	 * server's answer has arrived.
	 *
	 * @param serverHurts whether the server hurts the zombie, which the check then waits for
	 */
	private static void checkHit(
			final ClientGameTestContext context,
			final String situation,
			final int expectedSharpness,
			final int expectedCrit,
			final boolean serverHurts) {
		context.waitTicks(ATTACK_COOLDOWN_TICKS);
		context.waitFor(client -> zombie(client).hurtTime == 0);
		boolean modShowsParticles = context.computeOnClient(client -> HitEffectClient.config().effectiveMode() == ConfigMode.CUSTOM);
		ParticleCounter.reset();
		context.getInput().pressKey(options -> options.keyAttack);
		int instantSharpness = ParticleCounter.count(ParticleTypes.ENCHANTED_HIT);
		int instantCrit = ParticleCounter.count(ParticleTypes.CRIT);

		if (serverHurts) {
			context.waitFor(client -> zombie(client).hurtTime > 0);
		}
		context.waitTicks(PACKET_SETTLE_TICKS);
		int sharpness = ParticleCounter.count(ParticleTypes.ENCHANTED_HIT);
		int crit = ParticleCounter.count(ParticleTypes.CRIT);
		boolean hurt = context.computeOnClient(client -> zombie(client).hurtTime > 0);
		LOGGER.info("Hit particles ({}): right after the attack sharpness {}, crit {}; after the server answered sharpness {}, crit {}",
				situation, instantSharpness, instantCrit, sharpness, crit);

		check(hurt == serverHurts, situation + ": the server should " + (serverHurts ? "" : "not ") + "hurt the zombie");
		check(sharpness == expectedSharpness && crit == expectedCrit,
				situation + ": expected " + expectedSharpness + " sharpness and " + expectedCrit + " crit emitters, got " + sharpness + " and " + crit);
		if (modShowsParticles) {
			check(instantSharpness == expectedSharpness && instantCrit == expectedCrit,
					situation + ": the particles should appear right after the attack, got " + instantSharpness + " sharpness and " + instantCrit + " crit emitters");
		}
	}

	/** Turns the player to face south (+Z) with the given pitch and returns its position. */
	private static Vec3 facePositiveZ(final ClientGameTestContext context, final TestServerContext server, final float pitch) {
		Vec3 position = context.computeOnClient(client -> client.player.position());
		server.runCommand(String.format(Locale.ROOT, "tp @p %.2f %.2f %.2f 0 %.1f", position.x, position.y, position.z, pitch));
		context.waitFor(client -> Math.abs(client.player.getXRot() - pitch) < 0.1F && Math.abs(client.player.getYRot()) < 0.1F);
		return position;
	}

	private static @Nullable Zombie zombie(final Minecraft client) {
		for (var entity : client.level.entitiesForRendering()) {
			if (entity instanceof Zombie zombie) {
				return zombie;
			}
		}
		return null;
	}

	private static int overlayArgb(final ClientGameTestContext context) {
		return context.computeOnClient(HurtOverlayColor::currentArgb);
	}

	/** The given share of the screenshot's pixels. */
	private static int minFlashGain(final ClientGameTestContext context, final double share) {
		return context.computeOnClient(client -> (int) (client.getWindow().getWidth() * client.getWindow().getHeight() * share));
	}

	/** Changes the settings on the client thread and waits until the effects have picked them up. */
	private static void configure(final ClientGameTestContext context, final Consumer<HitEffectConfig> change) {
		context.runOnClient(client -> change.accept(HitEffectClient.config()));
		context.waitTicks(2);
	}

	private static void applyDefaults(final HitEffectConfig config) {
		EffectValues defaults = EffectValues.DEFAULTS;
		for (FlashPart part : FlashPart.values()) {
			config.setFlashes(part, defaults.flashes(part));
		}
		config.setHurtColor(defaults.hurtColor());
		config.setHurtStrength(defaults.hurtStrength());
		config.setRainbow(defaults.rainbow());
		config.setSharpnessParticles(defaults.sharpnessParticles());
		config.setCritParticles(defaults.critParticles());
	}
}
