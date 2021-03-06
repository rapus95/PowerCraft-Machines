package powercraft.energy.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Field;
import powercraft.api.block.PC_TileEntity;
import powercraft.api.energy.PC_EnergyGrid;
import powercraft.api.energy.PC_IEnergyGridBuffer;
import powercraft.api.energy.PC_IEnergyGridTile;
import powercraft.api.gres.PC_Gres;
import powercraft.api.gres.PC_GresBaseWithInventory;
import powercraft.api.gres.PC_IGresGui;
import powercraft.api.gres.PC_IGresGuiOpenHandler;
import powercraft.api.grid.PC_GridHelper;
import powercraft.api.grid.PC_IGridHolder;
import powercraft.api.grid.PC_IGridSided;
import powercraft.api.grid.PC_IGridTile;
import powercraft.energy.gui.PCeg_GuiAccumulator;

public class PCeg_TileEntityAccumulator extends PC_TileEntity implements PC_IEnergyGridBuffer, PC_IGridSided, PC_IGridHolder, PC_IGresGuiOpenHandler {

	public static final int MAX = 100000;
	
	@PC_Field
	private float level;
	
	private PC_EnergyGrid grid;
	
	@Override
	public void setGrid(PC_EnergyGrid grid) {
		this.grid = grid;
	}

	@Override
	public PC_EnergyGrid getGrid() {
		return this.grid;
	}

	@Override
	public void getGridIfNull() {
		PC_GridHelper.getGridIfNull(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 0x3F, this, PC_EnergyGrid.factory, PC_IEnergyGridTile.class);
	}

	@Override
	public void removeFromGrid() {
		PC_GridHelper.removeFromGrid(this.worldObj, (PC_IEnergyGridTile)this);
	}

	@Override
	public <T extends PC_IGridTile<?, T, ?, ?>> T getTile(PC_Direction side, int flags, Class<T> tileClass) {
		if(tileClass.isAssignableFrom(getClass())){
			return tileClass.cast(this);
		}
		return null;
	}

	@Override
	public float getEnergyLevel() {
		return this.level;
	}

	@Override
	public float getEnergyMaxIn() {
		return MAX-10<this.level?MAX-this.level:10;
	}

	@Override
	public float getEnergyMaxOut() {
		return 10>this.level?this.level:10;
	}

	@Override
	public float addEnergy(float energy) {
		this.level+=energy;
		NBTTagCompound nbtTagCompound = new NBTTagCompound();
		nbtTagCompound.setInteger("type", 0);
		nbtTagCompound.setFloat("value1", energy);
		nbtTagCompound.setFloat("value2", this.level);
		sendMessage(nbtTagCompound);
		return this.level;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public PC_IGresGui openClientGui(EntityPlayer player, NBTTagCompound serverData) {
		return new PCeg_GuiAccumulator(this);
	}

	@Override
	public PC_GresBaseWithInventory openServerGui(EntityPlayer player, Object[] params) {
		return null;
	}

	@Override
	public NBTTagCompound sendOnGuiOpenToClient(EntityPlayer player, Object[] params) {
		return null;
	}
	
	@Override
	public void onClientMessage(EntityPlayer player, NBTTagCompound nbtTagCompound) {
		PCeg_GuiAccumulator gui = PC_Gres.getCurrentClientGui(PCeg_GuiAccumulator.class);
		if(gui!=null && gui.getAccumulator()==this){
			switch(nbtTagCompound.getInteger("type")){
			case 0:
				gui.setEnergyPerTick(nbtTagCompound.getFloat("value1"));
				gui.setEnergyLevel(nbtTagCompound.getFloat("value2"));
				break;
			default:
				break;
			}
		}
	}
	
}
