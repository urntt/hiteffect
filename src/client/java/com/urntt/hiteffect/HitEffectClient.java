package com.urntt.hiteffect;

import com.mojang.blaze3d.platform.InputConstants;
import com.urntt.hiteffect.config.EffectSettings;
import com.urntt.hiteffect.config.HitEffectConfig;
import com.urntt.hiteffect.effect.HitParticles;
import com.urntt.hiteffect.effect.HurtOverlayColor;
import com.urntt.hiteffect.gui.HitEffectConfigScreen;
import com.urntt.hiteffect.render.HitEffectRenderTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class HitEffectClient implements ClientModInitializer {
	public static final String MOD_ID = "hiteffect";
	public static final String OPEN_CONFIG_KEY_NAME = "key.hiteffect.open_config";

	private static HitEffectConfig config;

	@Override
	public void onInitializeClient() {
		config = HitEffectConfig.load(HitEffectConfig.defaultPath());
		HitEffectRenderTypes.registerPipelines();

		KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "general"));
		KeyMapping openConfigKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping(OPEN_CONFIG_KEY_NAME, InputConstants.UNKNOWN.getValue(), category));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (openConfigKey.consumeClick()) {
				HitEffectConfigScreen.open(client);
			}
			HurtOverlayColor.update(client);
			HitParticles.tick();
		});
	}

	public static HitEffectConfig config() {
		return config;
	}

	/** The settings that currently apply, according to the configuration mode. */
	public static EffectSettings settings() {
		return config.effective();
	}
}
