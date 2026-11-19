package com.urntt.hiteffect.render;

import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.ShaderDefines;
import net.minecraft.resources.Identifier;

/**
 * A copy of a vanilla pipeline that samples the overlay texture: identical except that the {@code NO_OVERLAY} shader
 * define is removed and the overlay sampler ({@code Sampler1}) is bound, as in vanilla's overlay-capable entity
 * pipelines. Reading every other property from the original keeps the copy in step with vanilla.
 */
final class OverlayCapablePipeline extends RenderPipeline {
	static final String NO_OVERLAY_DEFINE = "NO_OVERLAY";

	OverlayCapablePipeline(final RenderPipeline original, final Identifier location) {
		super(
				location,
				original.getShaders(),
				withoutNoOverlay(original.getShaderDefines()),
				withOverlaySampler(original.getBindGroupLayouts()),
				original.getColorTargetStates().toArray(ColorTargetState[]::new),
				original.getDepthStencilState(),
				original.getPolygonMode(),
				original.isCull(),
				original.getVertexFormatBindings().toArray(VertexFormat[]::new),
				original.getPrimitiveTopology(),
				original.pushConstantSize(),
				// Sorting it next to the original keeps the draw order of equipment layers unchanged.
				original.getSortKey());
	}

	private static List<BindGroupLayout> withOverlaySampler(final List<BindGroupLayout> layouts) {
		List<BindGroupLayout> withSampler = new ArrayList<>(layouts);
		withSampler.add(BindGroupLayouts.SAMPLER1);
		return withSampler;
	}

	private static ShaderDefines withoutNoOverlay(final ShaderDefines defines) {
		Set<String> flags = defines.flags().stream()
				.filter(flag -> !flag.equals(NO_OVERLAY_DEFINE))
				.collect(Collectors.toUnmodifiableSet());
		return new ShaderDefines(defines.values(), flags);
	}
}
