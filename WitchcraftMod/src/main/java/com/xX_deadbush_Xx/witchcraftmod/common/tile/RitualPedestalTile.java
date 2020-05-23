package com.xX_deadbush_Xx.witchcraftmod.common.tile;

import java.util.Random;

import com.xX_deadbush_Xx.witchcraftmod.api.tile.BasicItemHolderTile;
import com.xX_deadbush_Xx.witchcraftmod.api.util.helpers.ItemStackHelper;
import com.xX_deadbush_Xx.witchcraftmod.client.effect.ClientParticleHandler.EffectType;
import com.xX_deadbush_Xx.witchcraftmod.common.network.WitchcraftPacketHandler;
import com.xX_deadbush_Xx.witchcraftmod.common.network.packets.client.WitchcraftParticlePacket;
import com.xX_deadbush_Xx.witchcraftmod.common.register.ModTileEntities;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

public class RitualPedestalTile extends BasicItemHolderTile {
	private Random rand = new Random();
	
	public RitualPedestalTile(final TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn, 1);
	}
	
	public RitualPedestalTile() {
		this(ModTileEntities.RITUAL_PEDESTAL.get());
	}
	
	public void swapItems(World worldIn, BlockPos blockpos, PlayerEntity player) {
		ItemStack playerItems = player.getHeldItemMainhand().copy();
		playerItems.setCount(1);
		Item heldItem = playerItems.getItem();

		if(!getItem().getItem().equals(heldItem)) {
			player.getHeldItemMainhand().shrink(1);
			if(this.hasItem()) {
				if(heldItem.equals(Items.AIR)) player.setHeldItem(Hand.MAIN_HAND, this.getItem());
				else if(!player.inventory.addItemStackToInventory(this.getItem())) { 
					ItemStackHelper.spawnItem(worldIn, this.getItem(), this.pos);
				}
			}
			setItem(playerItems);
		}
	}
	
	public ItemStack getItem() {
		return getStackInSlot(0);
	}
	
	public void setItem(ItemStack stack) {
		setInventorySlotContents(0, stack);
	}
	
	public boolean hasItem() {
		return !this.getItem().isEmpty() && !this.getItem().getItem().equals(Items.AIR);
	}
	
	public void useForCrafting() {
		this.removeStackFromSlot(0);
		this.markDirty();
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.put("item", getItem().write(new CompoundNBT()));
		return super.write(compound);
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		CompoundNBT item = compound.getCompound("item");

		if(item != null && !item.isEmpty()) {
			setItem(ItemStack.read(item));
		}
	}
}
