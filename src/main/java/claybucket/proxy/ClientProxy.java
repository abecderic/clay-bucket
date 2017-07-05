package claybucket.proxy;

import claybucket.ClayBucketMod;
import claybucket.Items;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(Items.unfiredClaybucket, 0, new ModelResourceLocation(ClayBucketMod.MODID + ":" + Items.UNFIRED_CLAYBUCKET));

        for (int i = 0; i < Items.NAMES.length + 1; i++)
        {
            String name = ClayBucketMod.MODID + ":" + Items.CLAYBUCKET;
            if (i != 0)
            {
                name += "_" + Items.NAMES[i - 1];
            }

            ModelResourceLocation loc = new ModelResourceLocation(name);
            ModelLoader.setCustomModelResourceLocation(Items.claybucket, i, loc);
        }
    }
}
