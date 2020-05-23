package com.xX_deadbush_Xx.witchcraftmod.common.items;

import java.util.List;

import com.xX_deadbush_Xx.witchcraftmod.WitchcraftMod;
import com.xX_deadbush_Xx.witchcraftmod.client.WitchcraftItemGroup;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SacrificeKnife extends Item {

	public SacrificeKnife() {
		super(new Properties().maxDamage(50).group(WitchcraftItemGroup.instance));
	    this.addPropertyOverride(new ResourceLocation(WitchcraftMod.MOD_ID, "bloody"), new IItemPropertyGetter() {
			@Override
			@OnlyIn(Dist.CLIENT)
			public float call(ItemStack stack, World p_call_2_, LivingEntity p_call_3_) {
		    	return isBloody(stack) ? 1.0F : 0.0F;
			}
	    });
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, PlayerEntity player) {
		// TODO Auto-generated method stub
		return super.onBlockStartBreak(itemstack, pos, player);
	}

	public float getAttackDamage() {
		return 5.0F;
	}
	
	private boolean isBloody(ItemStack stack) {
		if(stack.hasTag()) {
			String name = stack.getTag().getString("mobname");
			return  name != null && name != "";
		}
		return false;
	}
	
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.damageItem(1, attacker, (player) -> {
	         player.sendBreakAnimation(EquipmentSlotType.MAINHAND);
	    });
		
		String targetname = Registry.ENTITY_TYPE.getKey(target.getType()).getPath();
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("mobname", targetname);
		System.out.println(targetname);
		stack.setTag(nbt);
	    return true;
	}
	
	public String readMob(ItemStack stack) {
		if(stack.hasTag()) {
			CompoundNBT tag = stack.getTag();
			if(!tag.isEmpty()) return tag.getString("mobname");
		} return null;
	}

    public void addInformation(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    	String mob = readMob(stack);
        if(mob != null && mob != "") tooltip.add(new StringTextComponent("\u00A77 Covered in " + mob + " blood"));
    }
}
