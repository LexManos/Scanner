package eladkay.scanner.compat;

import eladkay.scanner.biome.BlockBiomeScanner;
import eladkay.scanner.misc.BaseTE;
import eladkay.scanner.terrain.BlockTerrainScanner;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class Waila {
    public static void onWailaCall(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new Scanner(), BlockTerrainScanner.class);
        registrar.registerNBTProvider(new Scanner(), BlockTerrainScanner.class);

        registrar.registerBodyProvider(new Scanner(), BlockBiomeScanner.class);
        registrar.registerNBTProvider(new Scanner(), BlockBiomeScanner.class);
    }

    public static class Scanner implements IWailaDataProvider {

        @Override
        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return accessor.getStack();
        }

        @Override
        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            TileEntity tileEntity = accessor.getTileEntity();
            if (!(tileEntity instanceof BaseTE)) return currenttip;
                /*int energy = ((TileEntityTerrainScanner) tileEntity).getEnergyStored(accessor.getSide());
                int max = ((TileEntityTerrainScanner) tileEntity).getMaxEnergyStored(accessor.getSide());*/
            int energy = accessor.getNBTData().getInteger("energy");
            int max = accessor.getNBTData().getInteger("max");
            currenttip.add("Energy: " + energy + "/" + max);
            return currenttip;
        }

        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return currenttip;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            BaseTE scanner = (BaseTE) te;
            tag.setInteger("energy", scanner.getEnergyStored(null));
            tag.setInteger("max", scanner.getMaxEnergyStored(null));
            return tag;
        }

    }
}


