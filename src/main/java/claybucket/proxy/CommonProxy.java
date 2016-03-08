package claybucket.proxy;

import claybucket.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy
{
    public void registerItems()
    {
        Items.registerAll();
    }

    public void registerRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(Items.unfiredClaybucket), "c c", " c ", 'c', net.minecraft.init.Items.clay_ball);
        GameRegistry.addSmelting(Items.unfiredClaybucket, new ItemStack(Items.claybucket), 0.2f);
    }

    public void registerTextures()
    {
        /* NO-OP */
    }
}
