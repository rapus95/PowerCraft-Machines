package powercraft.laser.block;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_BlockType;
import powercraft.api.block.PC_TileEntity;
import powercraft.laser.tileEntity.PCla_TileEntityLaserHarvester;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PCla_BlockLaserHarvester extends PC_BlockTileEntity {

	public static IIcon side;
	public static IIcon front;

	public PCla_BlockLaserHarvester() {
		super(PC_BlockType.MACHINE);
		setCreativeTab(CreativeTabs.tabBlock);
		setBlockName("Laser");
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCla_TileEntityLaserHarvester.class;
	}

	@SuppressWarnings("hiding")
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(PC_Direction side, int metadata) {
		if (side == PC_Direction.EAST)
			return front;
		return PCla_BlockLaserHarvester.side;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(PC_IconRegistry iconRegistry) {
		side = iconRegistry.registerIcon("side");
		front = iconRegistry.registerIcon("front");
	}

}