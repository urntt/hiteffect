package com.urntt.hiteffect.config;

import java.util.EnumSet;
import java.util.Set;

/**
 * An immutable set of effect settings.
 *
 * @param flashing the parts that flash with the hurt entity
 */
public record EffectValues(
		Set<FlashPart> flashing,
		int hurtColor,
		int hurtStrength,
		boolean rainbow,
		boolean sharpnessParticles,
		boolean critParticles) implements EffectSettings {
	/** The defaults of the mod's own settings and of its OneConfig settings. */
	public static final EffectValues DEFAULTS = new EffectValues(
			EnumSet.allOf(FlashPart.class), HurtColors.VANILLA_COLOR, HurtColors.VANILLA_STRENGTH, false, true, false);

	/** Vanilla behavior, applied while the mod is disabled. */
	public static final EffectValues DISABLED = new EffectValues(
			EnumSet.noneOf(FlashPart.class), HurtColors.VANILLA_COLOR, HurtColors.VANILLA_STRENGTH, false, false, false);

	public EffectValues {
		flashing = Set.copyOf(flashing);
	}

	@Override
	public boolean flashes(final FlashPart part) {
		return this.flashing.contains(part);
	}

	/** Copies the current values of {@code settings}. */
	public static EffectValues copyOf(final EffectSettings settings) {
		Set<FlashPart> flashing = EnumSet.noneOf(FlashPart.class);
		for (FlashPart part : FlashPart.values()) {
			if (settings.flashes(part)) {
				flashing.add(part);
			}
		}
		return new EffectValues(
				flashing,
				settings.hurtColor(),
				settings.hurtStrength(),
				settings.rainbow(),
				settings.sharpnessParticles(),
				settings.critParticles());
	}
}
