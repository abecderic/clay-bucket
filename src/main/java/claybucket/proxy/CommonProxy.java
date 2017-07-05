package claybucket.proxy;

import claybucket.ItemClaybucket;
import claybucket.ItemUnfiredClaybucket;
import claybucket.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class CommonProxy
{
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemUnfiredClaybucket());
        event.getRegistry().register(new ItemClaybucket());
    }

    public void registerRecipes()
    {
        GameRegistry.addSmelting(Items.unfiredClaybucket, new ItemStack(Items.claybucket), 0.2f);
    }
}
