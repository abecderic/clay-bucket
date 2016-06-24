package claybucket.proxy;

import claybucket.ClayBucketMod;
import claybucket.Items;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerTextures()
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
            //ModelBakery.registerItemVariants(Items.claybucket, loc);
            ModelLoader.setCustomModelResourceLocation(Items.claybucket, i, loc);
        }
    }
}
