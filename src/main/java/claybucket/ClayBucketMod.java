package claybucket;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ClayBucketMod.MODID, name = ClayBucketMod.MODNAME, version = ClayBucketMod.VERSION)
public class ClayBucketMod
{
    public static final String MODID = "claybucket";
    public static final String MODNAME = "Clay Bucket";
    public static final String VERSION = "1.0";

    public static final String UNFIRED_CLAYBUCKET = "unfiredClaybucket";
    public static final String CLAYBUCKET = "claybucket";

    public static final String[] NAMES = {"water", "lava"};
    public static final Block[] BLOCKS = {Blocks.water, Blocks.lava};
    public static final Fluid[] FLUIDS = {FluidRegistry.WATER, FluidRegistry.LAVA};
    public static final boolean[] DESTROY_BUCKET = {false, true};

    public static Item unfiredClaybucket;
    public static Item claybucket;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        unfiredClaybucket = new ItemUnfiredClaybucket();
        GameRegistry.registerItem(unfiredClaybucket, UNFIRED_CLAYBUCKET);

        claybucket = new ItemClayBucket();
        GameRegistry.registerItem(claybucket, CLAYBUCKET);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.addRecipe(new ItemStack(unfiredClaybucket), "c c", " c ", 'c', Items.clay_ball);
        GameRegistry.addSmelting(unfiredClaybucket, new ItemStack(claybucket), 0.2f);

        if (event.getSide() == Side.CLIENT)
        {
            RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
            renderItem.getItemModelMesher().register(unfiredClaybucket, 0, new ModelResourceLocation(MODID + ":" + UNFIRED_CLAYBUCKET, "inventory"));

            for (int i = 0; i < NAMES.length+1; i++)
            {
                String name = MODID + ":" + CLAYBUCKET;
                if (i != 0)
                {
                    name += "_" + NAMES[i-1];
                }

                ModelResourceLocation loc = new ModelResourceLocation(name, "inventory");
                ModelBakery.registerItemVariants(claybucket, loc);
                renderItem.getItemModelMesher().register(claybucket, i, loc);
            }
        }
    }
}
