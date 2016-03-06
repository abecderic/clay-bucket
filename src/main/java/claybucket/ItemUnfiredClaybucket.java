package claybucket;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		itemIcon = register.registerIcon(ClayBucketMod.MODID + ":" + ClayBucketMod.UNFIRED_CLAYBUCKET);
	}
}
