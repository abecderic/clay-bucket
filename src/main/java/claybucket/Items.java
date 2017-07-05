package claybucket;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class Items
{
    public static final String UNFIRED_CLAYBUCKET = "unfiredclaybucket";
    public static final String CLAYBUCKET = "claybucket";

    public static final String[] NAMES = {"water", "lava"};
    public static final Block[] BLOCKS = {Blocks.WATER, Blocks.LAVA};
    public static final Fluid[] FLUIDS = {FluidRegistry.WATER, FluidRegistry.LAVA};
    public static final boolean[] DESTROY_BUCKET = {false, true};

    @GameRegistry.ObjectHolder(ClayBucketMod.MODID + ":" + UNFIRED_CLAYBUCKET)
    public static Item unfiredClaybucket;

    @GameRegistry.ObjectHolder(ClayBucketMod.MODID + ":" + CLAYBUCKET)
    public static Item claybucket;
}
