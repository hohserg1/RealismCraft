package com.legendgamer.realism.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileWorkbench extends TileEntity {

    public final ItemStackHandler inv = new ItemStackHandler(9);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound r = super.writeToNBT(compound);
        r.merge(inv.serializeNBT());
        return r;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        inv.deserializeNBT(compound);
    }

    public int getSlotIndexFromClickCoords(float hitX, float hitZ) {
        if (hitX > 0.0625 * 3.5 && hitZ > 0.0625 * 3.5 && hitX < (1 - 0.0625 * 3.5) && hitZ < (1 - 0.0625 * 3.5)) {
            int x = Math.min((int) ((hitX - 0.0625 * 3.5) / (0.0625 * 3)), 2);
            int z = Math.min((int) ((hitZ - 0.0625 * 3.5) / (0.0625 * 3)), 2);

            return x * 3 + z;
        } else
            return -1;
    }

    public void tryExtrctItemStack(EntityPlayer playerIn, float hitX, float hitZ) {
        int slotIndex = getSlotIndexFromClickCoords(hitX, hitZ);
        if (slotIndex != -1) {
            ItemStack stackInSlot = inv.getStackInSlot(slotIndex);
            if (!stackInSlot.isEmpty()) {
                ItemStack take = stackInSlot.copy();
                inv.setStackInSlot(slotIndex, ItemStack.EMPTY);
                if (!playerIn.inventory.addItemStackToInventory(take))
                    world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, take));
                sendUpdates();
                markDirty();
            }
        }
    }

    public boolean areEquals(ItemStack a, ItemStack b) {
        return a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage() && ItemStack.areItemStackTagsEqual(a, b);
    }

    public boolean tryInsertItemStack(EntityPlayer playerIn, EnumHand hand, float hitX, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        int slotIndex = getSlotIndexFromClickCoords(hitX, hitZ);
        if (slotIndex != -1) {
            ItemStack stackInSlot = inv.getStackInSlot(slotIndex);
            if (stackInSlot.isEmpty() || areEquals(stackInSlot, heldItem)) {
                ItemStack itemStack = inv.insertItem(slotIndex, heldItem.copy(), false);
                if (itemStack.isEmpty())
                    heldItem.setCount(itemStack.getCount());
                sendUpdates();
                markDirty();
                return true;
            }
        }
        return false;
    }

    private void sendUpdates() {
        if (world instanceof WorldServer) {
            PlayerChunkMapEntry chunk = ((WorldServer) world).getPlayerChunkMap().getEntry(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk != null)
                chunk.sendPacket(getUpdatePacket());
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 3, getUpdateTag());
    }
}
