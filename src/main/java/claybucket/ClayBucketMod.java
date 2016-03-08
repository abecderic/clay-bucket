package claybucket;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

@Mod(modid = ClayBucketMod.MODID, name = ClayBucketMod.MODNAME, version = ClayBucketMod.VERSION)
public class ClayBucketMod
{
	public static final String MODID = "claybucket";
	public static final String MODNAME = "Clay Bucket";
	public static final String VERSION = "1.1";
	
	public static final String UNFIRED_CLAYBUCKET = "unfiredClaybucket";
	public static final String CLAYBUCKET = "claybucket";
	
	public static final String[] NAMES = {"water", "lava"};
	public static final Block[] BLOCKS = {Blocks.water, Blocks.lava};
	public static final Fluid[] FLUIDS = {FluidRegistry.WATER, FluidRegistry.LAVA};
	public static final boolean[] DESTROY_BUCKET = {false, true};
	
	public static Item unfiredClaybucket;
	public static Item claybucket;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		unfiredClaybucket = new ItemUnfiredClaybucket();
		GameRegistry.registerItem(unfiredClaybucket, UNFIRED_CLAYBUCKET);
		
		claybucket = new ItemClayBucket();
		GameRegistry.registerItem(claybucket, CLAYBUCKET);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		GameRegistry.addRecipe(new ItemStack(unfiredClaybucket), "c c", " c ", 'c', Items.clay_ball);
		GameRegistry.addSmelting(unfiredClaybucket, new ItemStack(claybucket), 0.2f);
	}
}
