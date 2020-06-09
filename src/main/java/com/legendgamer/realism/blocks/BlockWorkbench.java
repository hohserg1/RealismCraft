package com.legendgamer.realism.blocks;

import com.legendgamer.realism.tile.TileWorkbench;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockWorkbench extends BlockContainer {
    public BlockWorkbench() {
        super(Material.WOOD);
        String name = getClass().getSimpleName().toLowerCase();
        setRegistryName(name);
        setUnlocalizedName(name);
        setCreativeTab(CreativeTabs.DECORATIONS);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileWorkbench();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return new AxisAlignedBB(0, 0, 0, 1, 0.75, 1);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        System.out.println("" + hitX + ", " + hitY + ", " + hitZ);

        if (!worldIn.isRemote) {
            if (hitY == 0.75) {
                TileWorkbench tile = (TileWorkbench) worldIn.getTileEntity(pos);
                ItemStack heldItem = playerIn.getHeldItem(hand);
                int slotIndex = tile.getSlotIndexFromClickCoords(hitX, hitZ);
                if (heldItem.isEmpty() || slotIndex != -1 && !tile.inv.getStackInSlot(slotIndex).isEmpty())
                    tile.tryExtrctItemStack(playerIn, hitX, hitZ);
                else
                    tile.tryInsertItemStack(playerIn, hand, hitX, hitZ);
                tile.markDirty();
            }
        }
        return hitY == 0.75;
    }
}
