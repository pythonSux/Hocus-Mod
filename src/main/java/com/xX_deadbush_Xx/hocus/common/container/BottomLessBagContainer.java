package com.xX_deadbush_Xx.hocus.common.container;

import com.xX_deadbush_Xx.hocus.api.inventory.BottomlessBagInventory;
import com.xX_deadbush_Xx.hocus.api.inventory.BottomlessBagSlot;
import com.xX_deadbush_Xx.hocus.common.register.ModContainers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntReferenceHolder;

public class BottomLessBagContainer extends Container {

	private PlayerInventory playerinv;
    private BottomlessBagInventory bagInventory;
    private int prevbagstackcount = 0;
    
    public BottomLessBagContainer(final int windowId, final PlayerInventory playerInventory, PacketBuffer packetBuffer) {
        this(windowId, playerInventory, new BottomlessBagInventory(playerInventory.getCurrentItem()), packetBuffer.readVarInt());
    }

    public BottomLessBagContainer(int windowId, PlayerInventory playerInventory, BottomlessBagInventory inventory, int selectedSlot) {
        super(ModContainers.BOTTOM_LESS_BAG.get(), windowId);
        this.bagInventory = inventory;
        this.playerinv = playerInventory;
        this.addSlot(new BottomlessBagSlot(bagInventory, 0, 81, 35));

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
        
        this.trackInt(IntReferenceHolder.create(this.bagInventory.amount, 0));
        if(bagInventory.isEmpty()) detectAndSendChanges();
    }

    public ItemStack getStoredItem() {
        return bagInventory.getStack();
    }

    public int getAmount() {
        return bagInventory.getAmount();
    }
    
    

	@Override
	public void detectAndSendChanges() {
		for (int i = 1; i < this.inventorySlots.size(); ++i) {
			ItemStack itemstack = this.inventorySlots.get(i).getStack();
			ItemStack prevstack = this.inventoryItemStacks.get(i);
			if (!ItemStack.areItemStacksEqual(prevstack, itemstack)) {
				boolean clientStackChanged = !prevstack.equals(itemstack, true);
				prevstack = itemstack.copy();
				this.inventoryItemStacks.set(i, prevstack);

				if (clientStackChanged)
					for (IContainerListener icontainerlistener : this.listeners) {
						icontainerlistener.sendSlotContents(this, i, prevstack);
					}
			}
		}
		
		//bottomless bag slot
		ItemStack bagstack = this.inventorySlots.get(0).getStack();
		ItemStack prevbagstack = this.inventoryItemStacks.get(0);
		if(!areItemsAndTagsEqual(prevbagstack, bagstack) || this.bagInventory.getAmount() != this.prevbagstackcount) {
			prevbagstack = bagstack.copy();
			this.prevbagstackcount = this.bagInventory.getAmount();
			this.inventoryItemStacks.set(0, prevbagstack);

			for (IContainerListener icontainerlistener : this.listeners) {
				icontainerlistener.sendSlotContents(this, 0, prevbagstack);
			}
		}
		
		for (int j = 0; j < this.trackedIntReferences.size(); ++j) {
			IntReferenceHolder intreferenceholder = this.trackedIntReferences.get(j);
			if (intreferenceholder.isDirty()) {
				for (IContainerListener icontainerlistener1 : this.listeners) {
					icontainerlistener1.sendWindowProperty(this, j, intreferenceholder.get());
				}
			}
		}
	}

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);

        ItemStack itemstack = slot.getStack();
        if (!itemstack.isEmpty()) {
            if (bagInventory.getStack().isEmpty()) {
                bagInventory.insertItem(0, itemstack, false);
                slot.putStack(ItemStack.EMPTY);
                this.inventorySlots.get(0).onSlotChanged();
                return ItemStack.EMPTY;
            } else if (itemstack.isItemEqual(bagInventory.getStack())) {
                bagInventory.insertItem(0, itemstack, false);
                itemstack.setCount(0);
                slot.putStack(ItemStack.EMPTY);
                return ItemStack.EMPTY;
            }
        }

        return ItemStack.EMPTY;
    }


    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        this.bagInventory.saveData();
        super.onContainerClosed(playerIn);
    }


    @Override
    public void putStackInSlot(int slotid, ItemStack stack) {
    	if(slotid == 0) bagInventory.insertItem(slotid, stack, false);
    	else playerinv.setInventorySlotContents(slotid + 8	, stack);
    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickType, PlayerEntity player) {
    	
        if (slotId == 0) {
            ItemStack dragged = player.inventory.getItemStack();
            BottomlessBagSlot bagslot = (BottomlessBagSlot) this.inventorySlots.get(slotId);
            ItemStack bagStack = bagslot.getStack().copy();
            boolean bagEmpty = !bagslot.getHasStack();
            int maxstacksize = bagStack.getMaxStackSize();
            PlayerInventory playerinventory = player.inventory;
            
            //shift click
            if(clickType == ClickType.QUICK_MOVE) {
                ItemStack extracted = bagInventory.extractItem(0, maxstacksize, false);
                
                if(!this.mergeItemStack(extracted, 1, this.inventorySlots.size(), true)) {
                    bagInventory.insertItem(0, extracted, false);
                } else bagslot.onTake(player, extracted);
            //regular click
            } else if (clickType == ClickType.PICKUP) {
            	if(areItemsAndTagsEqual(dragged, bagStack) || dragged.isEmpty() || bagEmpty) {
            		//If stacks can merge
    				if (dragged.isEmpty()) {
    					//pick up from bagslot
                        int amountToExtract = (int) (dragType == 0 ? Math.min(maxstacksize, bagInventory.getAmount()) : Math.ceil(Math.min((double)maxstacksize/2, (double)bagInventory.getAmount()/2))); //Calculating the amount to extract, if the player right click then dragType == 1 and its a right click otherwise a left click
                        ItemStack extracted = bagInventory.extractItem(0, amountToExtract, false);
    					bagslot.onTake(player, extracted);
    					player.inventory.setItemStack(extracted);
    				} else {
    					if(dragType == 1) {
    						player.inventory.getItemStack().shrink(1);
    						bagInventory.insertItem(0, new ItemStack(dragged.getItem(), 1), false);
    					} else {
    						player.inventory.setItemStack(ItemStack.EMPTY);
							bagInventory.insertItem(0, dragged, false);
    					}
    				}
            	} else if(bagInventory.getAmount() <= bagStack.getMaxStackSize()) { 
            		//swap stacks if possible
            		bagStack.setCount(bagInventory.getAmount());
					bagslot.onTake(player, bagStack);
					player.inventory.setItemStack(bagStack);
            		bagInventory.setCurrentStack(dragged, 0);
            	}
            //mouse wheel
            } else if (clickType == ClickType.CLONE && player.abilities.isCreativeMode && playerinventory.getItemStack().isEmpty()) {
                if (!bagEmpty) {
                    bagStack.setCount(maxstacksize);
                    playerinventory.setItemStack(bagStack);
                 }
            //Q
            } else if (clickType == ClickType.THROW && playerinventory.getItemStack().isEmpty()) {
                if (bagslot != null && !bagEmpty && bagslot.canTakeStack(player)) {
                   ItemStack extracted = bagInventory.extractItem(0, dragType == 0 ? 1 : Math.min(this.bagInventory.getAmount(), bagStack.getMaxStackSize()), false);
                   bagslot.onTake(player, extracted);
                   player.dropItem(extracted, true);
                }
            }
            
            detectAndSendChanges();
        } else return super.slotClick(slotId, dragType, clickType, player);
        return bagInventory.getShowStack();
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
    	return super.mergeItemStack(stack, Math.min(startIndex, 1), endIndex, reverseDirection);
    }
    

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

	public void setAmount(int count) {
		this.bagInventory.setAmount(count);
	}
}
