package powercraft.transport.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import powercraft.api.PC_Direction;
import powercraft.api.PC_IconRegistry;
import powercraft.api.block.PC_BlockTileEntity;
import powercraft.api.block.PC_TileEntity;
import powercraft.transport.tileentity.PCtr_TileEntityEjector;


public class PCtr_BlockEjector extends PC_BlockTileEntity {

	public static IIcon[] icons = new IIcon[2];
	
	public PCtr_BlockEjector() {
		super(Material.ground);
		setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public Class<? extends PC_TileEntity> getTileEntityClass() {
		return PCtr_TileEntityEjector.class;
	}

	@Override
	public IIcon getIcon(PC_Direction side, int metadata) {
		if(side==PC_Direction.EAST){
			return PCtr_BlockEjector.icons[0];
		}
		return PCtr_BlockEjector.icons[1];
	}
	
	@Override
	public void registerIcons(PC_IconRegistry iconRegistry) {
		PCtr_BlockEjector.icons[0] = iconRegistry.registerIcon("front");
		PCtr_BlockEjector.icons[1] = iconRegistry.registerIcon("side");
	}
	
	@Override
	public boolean canRotate(){
		return true;
	}
	
}
