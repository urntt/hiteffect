package com.urntt.hiteffect.effect;

import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.config.EffectSettings;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Shows sharpness and critical hit particles the moment the local player attacks, entirely on the client.
 *
 * <p>Only the attacked living entity gets them; entities hit by a sweep do not, as in vanilla. Vanilla computes these
 * particles on the server and sends them back as animation packets. When such a packet arrives for a particle this
 * class already showed for an attack on the same entity, it is skipped, so no hit shows a particle twice.
 */
public final class HitParticles {
	/**
	 * How long a shown particle waits for the server's matching animation, in client ticks. It covers the round trip
	 * to the server; a packet arriving later than this shows its particle again.
	 */
	private static final int SERVER_ANIMATION_TICKS = 40;

	private static final List<Shown> SHOWN = new ArrayList<>();
	private static int tick;

	private HitParticles() {
	}

	public static void tick() {
		tick++;
		SHOWN.removeIf(shown -> tick - shown.tick > SERVER_ANIMATION_TICKS);
	}

	/** Called when the local player attacks {@code target}, after vanilla handled the attack. */
	public static void onAttack(final Minecraft minecraft, final Player player, final Entity target) {
		if (!canHit(player, target)) {
			return;
		}

		EffectSettings settings = HitEffectClient.settings();
		if (settings.sharpnessParticles()) {
			show(minecraft, target, ParticleTypes.ENCHANTED_HIT);
		}
		if (settings.critParticles()) {
			show(minecraft, target, ParticleTypes.CRIT);
		}
	}

	/**
	 * Whether vanilla should skip showing {@code particle} on {@code entity}, because this mod already showed it for
	 * an attack on that entity.
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

	/** The checks vanilla's {@code Player.attack} makes before hurting a target, as far as the client knows them. */
	private static boolean canHit(final Player player, final Entity target) {
		return target instanceof LivingEntity living
				&& living.isAlive()
				&& target.isAttackable()
				&& !target.skipAttackInteraction(player)
				&& !player.isSpectator();
	}

	private static void show(final Minecraft minecraft, final Entity entity, final ParticleOptions particle) {
		minecraft.particleEngine.createTrackingEmitter(entity, particle);
		SHOWN.add(new Shown(entity.getId(), particle, tick));
	}

	private record Shown(int entityId, ParticleOptions particle, int tick) {
	}
}
