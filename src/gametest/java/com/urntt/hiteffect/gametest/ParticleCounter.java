package com.urntt.hiteffect.gametest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.particles.ParticleOptions;

/**
 * How many tracking emitters were created per particle type since the last {@link #reset()}.
 */
public final class ParticleCounter {
	private static final Map<ParticleOptions, Integer> COUNTS = new ConcurrentHashMap<>();

	private ParticleCounter() {
	}

	public static void record(final ParticleOptions particle) {
		COUNTS.merge(particle, 1, Integer::sum);
	}

	public static int count(final ParticleOptions particle) {
		return COUNTS.getOrDefault(particle, 0);
	}

	public static void reset() {
		COUNTS.clear();
	}
}
