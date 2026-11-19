package com.urntt.hiteffect.effect;

import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.config.EffectSettings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

/**
 * Shows sharpness and critical hit particles on every melee hit of the local player.
 *
 * <p>Vanilla computes both on the server and sends them as animation packets, so the client cannot predict them. A
 * hit counts once the server confirms it: a {@code player_attack} damage event caused by the local player on the
 * entity it attacked last. Entities hit by a sweep are not the attacked entity, so they get no particles, as in
 * vanilla. When vanilla also shows a particle for that hit, its animation packet arrives right after the damage event
 * and is skipped, so no hit shows a particle twice.
 */
public final class HitParticles {
	/** How long an attack waits for the server to confirm the hit. */
	private static final int CONFIRMATION_TICKS = 40;

	/** How long after showing a particle the matching vanilla animation is skipped. */
	private static final int DUPLICATE_TICKS = 5;

	private static final int NO_TARGET = -1;

	private static final List<Shown> SHOWN = new ArrayList<>();
	private static int tick;
	private static int attackedEntityId = NO_TARGET;
	private static int attackTick;

	private HitParticles() {
	}

	public static void tick() {
		tick++;
		SHOWN.removeIf(shown -> tick - shown.tick > DUPLICATE_TICKS);
	}

	/** Called when the local player attacks {@code target}. */
	public static void onAttack(final Entity target) {
		attackedEntityId = target.getId();
		attackTick = tick;
	}

	/** Called when the server reports that {@code entity} took damage. */
	public static void onDamage(final Minecraft minecraft, final Entity entity, final DamageSource source) {
		LocalPlayer player = minecraft.player;
		if (player == null || !isConfirmedHit(player, entity, source)) {
			return;
		}
		attackedEntityId = NO_TARGET;

		EffectSettings settings = HitEffectClient.settings();
		if (settings.sharpnessParticles()) {
			show(minecraft, entity, ParticleTypes.ENCHANTED_HIT);
		}
		if (settings.critParticles()) {
			show(minecraft, entity, ParticleTypes.CRIT);
		}
	}

	/**
	 * Whether vanilla should skip showing {@code particle} on {@code entity}, because this mod already showed it for
	 * the same hit.
	 */
	public static boolean isDuplicate(final Entity entity, final ParticleOptions particle) {
		for (Iterator<Shown> iterator = SHOWN.iterator(); iterator.hasNext(); ) {
			Shown shown = iterator.next();
			if (shown.entityId == entity.getId() && shown.particle == particle) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	private static boolean isConfirmedHit(final LocalPlayer player, final Entity entity, final DamageSource source) {
		return entity.getId() == attackedEntityId
				&& tick - attackTick <= CONFIRMATION_TICKS
				&& source.is(DamageTypes.PLAYER_ATTACK)
				&& isPlayer(source.getEntity(), player)
				&& isPlayer(source.getDirectEntity(), player);
	}

	private static boolean isPlayer(final @Nullable Entity entity, final LocalPlayer player) {
		return entity != null && entity.getId() == player.getId();
	}

	private static void show(final Minecraft minecraft, final Entity entity, final ParticleOptions particle) {
		minecraft.particleEngine.createTrackingEmitter(entity, particle);
		SHOWN.add(new Shown(entity.getId(), particle, tick));
	}

	private record Shown(int entityId, ParticleOptions particle, int tick) {
	}
}
