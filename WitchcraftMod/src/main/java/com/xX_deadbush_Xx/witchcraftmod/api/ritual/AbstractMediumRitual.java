package com.xX_deadbush_Xx.witchcraftmod.api.ritual;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.xX_deadbush_Xx.witchcraftmod.api.ritual.effect.RitualEffectHandler;
import com.xX_deadbush_Xx.witchcraftmod.api.util.helpers.ListHelper;
import com.xX_deadbush_Xx.witchcraftmod.api.util.helpers.RitualHelper;
import com.xX_deadbush_Xx.witchcraftmod.api.util.helpers.RitualHelper.RitualPositionHolder;
import com.xX_deadbush_Xx.witchcraftmod.common.blocks.ChalkBlock;
import com.xX_deadbush_Xx.witchcraftmod.common.blocks.RitualStone;
import com.xX_deadbush_Xx.witchcraftmod.common.blocks.blockstate.GlowType;
import com.xX_deadbush_Xx.witchcraftmod.common.tile.RitualStoneTile;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractMediumRitual implements IRitual {
	protected final PlayerEntity performingPlayer;
	protected final RitualStoneTile tile;
	protected final  World worldIn;
	protected List<BlockPos> chalkpositions = new ArrayList<>();
	protected List<BlockPos> nonRitualBlocks = new ArrayList<>();
	protected List<BlockPos> junctionBlocks = new ArrayList<>();
	private List<BlockPos[]> totems;
	protected RitualEffectHandler effecthandler;
	
	//Need to be saved to TE nbt
	protected int age = 0;
	protected boolean doespowerdownanimation = false;
	
	public AbstractMediumRitual(RitualStoneTile tile, PlayerEntity player) {
		this.worldIn = tile.getWorld();
		this.tile = tile;
		this.performingPlayer = player;
		
		RitualPositionHolder positions = RitualHelper.getRitualPositionsMedium(tile.getWorld(), tile.getPos());
		this.chalkpositions = positions.chalkpositions; this.nonRitualBlocks = positions.nonRitualBlocks; this.junctionBlocks = positions.junctionBlocks; this.totems = positions.totems;
	}
	
	public void tick() {
		this.age++;
		int stonepower = worldIn.getBlockState(tile.getPos()).get(RitualStone.POWER);
		GlowType stonetype = worldIn.getBlockState(tile.getPos()).get(RitualStone.GLOW_TYPE);
		RitualHelper.colorChalk(stonetype, stonepower, this.chalkpositions, this.worldIn, this.tile.getPos());
		
		if(this.doespowerdownanimation) {
			if(this.tile.glowpower == 0) this.stopRitual(false);
			return;
		}

		if(this instanceof IStaticRitual) this.effecthandler.tick();
		if(this instanceof IContinuousRitual) ((IContinuousRitual)this).effectTick();
	}
	
	@Override
	public boolean conditionsMet() {
		return multiblockComplete((MediumRitualConfig) this.getConfig());
	}
	
	@Override
	public boolean multiblockComplete(IRitualConfig config) {
		for(BlockPos pos : this.nonRitualBlocks) {
			if(RitualHelper.stopsRitual(this.worldIn, pos)) {
				System.out.println("stopped ritual because of block at: " + pos);
				return false;
			}
		}
		return chalkInPlace() && junctionBlocksInPlace((MediumRitualConfig)config);
	}
	
	public boolean chalkInPlace() {
		for(BlockPos pos : this.chalkpositions) {
			if(!RitualHelper.isChalk(this.worldIn, pos)) return false;
		}
		return true;
	}
	
	public boolean junctionBlocksInPlace(MediumRitualConfig config) {
		List<Block> blocks = junctionBlocks.stream().map(this.worldIn::getBlockState).map(s -> s.getBlock()).collect(Collectors.toList());
		return config.matches(ListHelper.toNonNullList(blocks));
	}
	
	protected void resetChalk() {
		for(BlockPos pos : this.chalkpositions) {
			if(RitualHelper.isChalk(worldIn, pos)) worldIn.setBlockState(pos, worldIn.getBlockState(pos).with(ChalkBlock.POWER, 0).with(ChalkBlock.GLOW_TYPE, GlowType.WHITE));
		} 
	}
	
	public int getAge() {
		return this.age;
	}
	
	public void stopRitual(boolean shouldDoChalkAnimation) {
		if(shouldDoChalkAnimation) {
			this.doespowerdownanimation = true;
		} else {
			this.tile.currentritual = null;
			this.resetChalk();
		}
	}
	
	public boolean isPoweringDown() {
		return doespowerdownanimation;
	}
}