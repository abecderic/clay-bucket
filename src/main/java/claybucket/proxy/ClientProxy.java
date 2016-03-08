package claybucket.proxy;

import claybucket.ClayBucketMod;
import claybucket.Items;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerTextures()
    {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(Items.unfiredClaybucket, 0, new ModelResourceLocation(ClayBucketMod.MODID + ":" + Items.UNFIRED_CLAYBUCKET, "inventory"));

        for (int i = 0; i < Items.NAMES.length + 1; i++)
        {
            String name = ClayBucketMod.MODID + ":" + Items.CLAYBUCKET;
            if (i != 0)
            {
                name += "_" + Items.NAMES[i - 1];
            }

            ModelResourceLocation loc = new ModelResourceLocation(name, "inventory");
            ModelBakery.registerItemVariants(Items.claybucket, loc);
            renderItem.getItemModelMesher().register(Items.claybucket, i, loc);
        }
    }
}
