package eladkay.scanner;

import eladkay.scanner.biome.BlockBiomeScanner;
import eladkay.scanner.biome.TileEntityBiomeScanner;
import eladkay.scanner.compat.MineTweaker;
import eladkay.scanner.misc.NetworkHelper;
import eladkay.scanner.proxy.CommonProxy;
import eladkay.scanner.terrain.BlockAirey;
import eladkay.scanner.terrain.BlockTerrainScanner;
import eladkay.scanner.terrain.TileEntityTerrainScanner;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderOverworld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ScannerMod.MODID, name = "Scanner", dependencies = "required-after:ftbl;required-after:ftbu;after:MineTweaker3;required-after:EnderIO", version = "1.1.2")
public class ScannerMod {
    public static final String MODID = "scanner";

    @SidedProxy(serverSide = "eladkay.scanner.proxy.CommonProxy", clientSide = "eladkay.scanner.proxy.ClientProxy")
    public static CommonProxy proxy;

    public static DimensionType dim;
    public static BlockTerrainScanner terrainScanner;
    public static BlockBiomeScanner biomeScannerBasic;
    public static BlockBiomeScanner biomeScannerAdv;
    public static BlockBiomeScanner biomeScannerElite;
    public static BlockBiomeScanner biomeScannerUltimate;
    @Mod.Instance(MODID)
    public static ScannerMod instance;
    public static CreativeTabs tab;
    public static BlockAirey air;

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        instance = this;
        tab = new CreativeTabs(MODID) {
            @Override
            public Item getTabIconItem() {
                return Item.getItemFromBlock(terrainScanner);
            }
        };
        GameRegistry.register(air = new BlockAirey());

        GameRegistry.register(terrainScanner = new BlockTerrainScanner());
        GameRegistry.register(new ItemBlock(terrainScanner).setRegistryName(MODID + ":terrainScanner").setCreativeTab(tab));
        GameRegistry.registerTileEntity(TileEntityTerrainScanner.class, "terrainScanner");


        GameRegistry.registerTileEntity(TileEntityBiomeScanner.class, "biomeScanner");
        //Biome Scanner Tiers
        GameRegistry.register((biomeScannerBasic = (BlockBiomeScanner) new BlockBiomeScanner(0).setRegistryName(ScannerMod.MODID + ":biomeScannerBasic")));
        GameRegistry.register(new ItemBlock(biomeScannerBasic).setRegistryName(MODID + ":biomeScannerBasic").setCreativeTab(tab));

        GameRegistry.register((biomeScannerAdv = (BlockBiomeScanner) new BlockBiomeScanner(1).setRegistryName(ScannerMod.MODID + ":biomeScannerAdv")));
        GameRegistry.register(new ItemBlock(biomeScannerAdv).setRegistryName(MODID + ":biomeScannerAdv").setCreativeTab(tab));

        GameRegistry.register((biomeScannerElite = (BlockBiomeScanner) new BlockBiomeScanner(2).setRegistryName(ScannerMod.MODID + ":biomeScannerElite")));
        GameRegistry.register(new ItemBlock(biomeScannerElite).setRegistryName(MODID + ":biomeScannerElite").setCreativeTab(tab));

        GameRegistry.register((biomeScannerUltimate = (BlockBiomeScanner) new BlockBiomeScanner(3).setRegistryName(ScannerMod.MODID + ":biomeScannerUltimate")));
        GameRegistry.register(new ItemBlock(biomeScannerUltimate).setRegistryName(MODID + ":biomeScannerUltimate").setCreativeTab(tab));


        FMLInterModComms.sendMessage("Waila", "register", "eladkay.scanner.compat.Waila.onWailaCall");
        MineTweaker.init();
        proxy.init();
        Config.initConfig(event.getSuggestedConfigurationFile());
        dim = DimensionType.register("fakeoverworld", "", Config.dimid, WorldProviderOverworld.class, true);
        DimensionManager.registerDimension(Config.dimid, dim);
        NetworkHelper.init();
    }

    @Mod.EventHandler
    public void fmlLifeCycle(FMLServerStartingEvent event) {
        event.registerServerCommand(new SpeedTickCommand());
    }

    public static class WorldProviderOverworld extends WorldProvider {

        @Override
        public DimensionType getDimensionType() {
            return dim;
        }

        @Override
        public IChunkGenerator createChunkGenerator() {
            return new ChunkProviderOverworld(worldObj, worldObj.getSeed(), worldObj.getWorldInfo().isMapFeaturesEnabled(), TileEntityTerrainScanner.PRESET);
        }
    }

    public static class SpeedTickCommand extends CommandBase {


        @Override
        public String getCommandName() {
            return "speedts";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/speedts";
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
            return sender.getName().matches("Player\\d+");
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            TileEntity te = server.worldServerForDimension(getCommandSenderAsPlayer(sender).dimension).getTileEntity(getCommandSenderAsPlayer(sender).getPosition().down());
            if(te instanceof ITickable) for(int i = 0; i < 100000; i++) ((ITickable) te).update();

        }
    }
}


