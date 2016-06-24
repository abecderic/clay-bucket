package claybucket;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUnfiredClaybucket extends Item
{
    public ItemUnfiredClaybucket()
    {
        this.setCreativeTab(CreativeTabs.MISC);
        this.setMaxStackSize(1);
        this.setRegistryName(Items.UNFIRED_CLAYBUCKET);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + ClayBucketMod.MODID + ":" + Items.UNFIRED_CLAYBUCKET;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }
}
