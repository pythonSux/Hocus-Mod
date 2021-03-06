package com.xX_deadbush_Xx.hocus.common.register;

import com.xX_deadbush_Xx.hocus.common.world.gen.features.BigHellshroomFeature;
import com.xX_deadbush_Xx.hocus.common.world.gen.features.FlowerFeature;
import com.xX_deadbush_Xx.hocus.common.world.gen.features.config.DreadwoodTreeConfig;
import com.xX_deadbush_Xx.hocus.common.world.gen.features.config.FlowerFeatureConfig;

import net.minecraft.world.gen.feature.BigMushroomFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;

public class ModFeatures {
	//public static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, WitchcraftMod.MOD_ID);
	
	public static final Feature<BigMushroomFeatureConfig> HUGE_HELLSHROOM = new BigHellshroomFeature(BigMushroomFeatureConfig::deserialize);
	public static final Feature<TreeFeatureConfig> DREADWOOD_TREE = new DreadwoodTreeConfig((data) -> TreeFeatureConfig.deserializeFoliage(data));
	public static final Feature<FlowerFeatureConfig> FLOWER_FEATURE = new FlowerFeature(FlowerFeatureConfig::deserialize);
}
