package claybucket;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUnfiredClaybucket extends Item
{
    public ItemUnfiredClaybucket()
    {
        this.setCreativeTab(CreativeTabs.tabAllSearch);
        this.setMaxStackSize(1);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + ClayBucketMod.MODID + ":" + ClayBucketMod.UNFIRED_CLAYBUCKET;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName();
    }
}
