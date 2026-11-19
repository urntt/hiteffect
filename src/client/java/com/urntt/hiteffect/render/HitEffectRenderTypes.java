package com.urntt.hiteffect.render;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.urntt.hiteffect.HitEffectClient;
import com.urntt.hiteffect.mixin.RenderSetupAccessor;
import com.urntt.hiteffect.mixin.RenderTypeAccessor;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

/**
 * Overlay-capable variants of vanilla's equipment render types.
 *
 * <p>Vanilla's equipment pipelines define {@code NO_OVERLAY}, so armor ignores the hurt overlay even when it is given
 * one. While an entity shows its hurt overlay, equipment is drawn with copies of those render types whose pipelines
 * sample the overlay; everything else about them stays vanilla.
 */
public final class HitEffectRenderTypes {
	/** The vanilla pipelines used by equipment layers that ignore the overlay. */
	private static final List<RenderPipeline> EQUIPMENT_PIPELINES = List.of(
			RenderPipelines.ARMOR_CUTOUT_NO_CULL,
			RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL,
			RenderPipelines.ARMOR_CUTOUT_NO_CULL_GLINT,
			RenderPipelines.WOLF_ARMOR_CRACKS);

	private static final String OVERLAY_SUFFIX = "_overlay";

	private static final Map<RenderPipeline, RenderPipeline> OVERLAY_PIPELINES = EQUIPMENT_PIPELINES.stream()
			.collect(Collectors.toUnmodifiableMap(Function.identity(), HitEffectRenderTypes::createOverlayPipeline));

	private static final Map<RenderType, RenderType> OVERLAY_RENDER_TYPES = new ConcurrentHashMap<>();

	private HitEffectRenderTypes() {
	}

	/**
	 * Registers the overlay-capable pipelines with vanilla, which compiles them together with its own on every
	 * resource reload. Must run before the first resource reload.
	 */
	public static void registerPipelines() {
		OVERLAY_PIPELINES.values().forEach(RenderPipelines::register);
	}

	/**
	 * Returns the overlay-capable variant of an equipment render type, or {@code renderType} itself if its pipeline
	 * already samples the overlay or is not an equipment pipeline.
	 */
	public static RenderType withOverlay(final RenderType renderType) {
		if (!OVERLAY_PIPELINES.containsKey(renderType.pipeline())) {
			return renderType;
		}
		return OVERLAY_RENDER_TYPES.computeIfAbsent(renderType, HitEffectRenderTypes::createOverlayRenderType);
	}

	private static RenderPipeline createOverlayPipeline(final RenderPipeline original) {
		Identifier location = original.getLocation();
		return new OverlayCapablePipeline(
				original, Identifier.fromNamespaceAndPath(HitEffectClient.MOD_ID, location.getPath() + OVERLAY_SUFFIX));
	}

	private static RenderType createOverlayRenderType(final RenderType original) {
		RenderTypeAccessor type = (RenderTypeAccessor) original;
		RenderSetupAccessor setup = (RenderSetupAccessor) (Object) type.hiteffect$getState();
		RenderSetup copy = RenderSetupAccessor.hiteffect$create(
				OVERLAY_PIPELINES.get(setup.hiteffect$getPipeline()),
				setup.hiteffect$getOitPipelineSet(),
				setup.hiteffect$getTextures(),
				setup.hiteffect$useLightmap(),
				true,
				setup.hiteffect$getLayeringTransform(),
				setup.hiteffect$getTextureTransform(),
				setup.hiteffect$getOutlineProperty(),
				setup.hiteffect$getOutlineTextureName(),
				setup.hiteffect$affectsCrumbling(),
				setup.hiteffect$sortOnUpload(),
				setup.hiteffect$forceSolidModelPhase());
		return RenderType.create(HitEffectClient.MOD_ID + ":" + type.hiteffect$getName() + OVERLAY_SUFFIX, copy);
	}
}
