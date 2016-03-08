package claybucket;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class ItemClayBucket extends Item
{
	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	private static final int AMOUNT = 1000;
	private static final int NETHER_LINES = 6;

	public ItemClayBucket()
	{
		this.setCreativeTab(CreativeTabs.tabAllSearch);
		this.setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public String getUnlocalizedName()
	{
		return "item." + ClayBucketMod.MODID + ":" + ClayBucketMod.CLAYBUCKET;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if (stack.getItemDamage() > 0 && stack.getItemDamage() <= ClayBucketMod.NAMES.length)
		{
			return getUnlocalizedName() + "_" + ClayBucketMod.NAMES[stack.getItemDamage() - 1];
		}
		else
		{
			return getUnlocalizedName();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register)
	{
		itemIcon = register.registerIcon(ClayBucketMod.MODID + ":" + ClayBucketMod.CLAYBUCKET);

		icons = new IIcon[ClayBucketMod.NAMES.length];
		for (int i = 0; i < ClayBucketMod.NAMES.length; i++)
		{
			icons[i] = register.registerIcon(ClayBucketMod.MODID + ":" + ClayBucketMod.CLAYBUCKET + "_" + ClayBucketMod.NAMES[i]);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg)
	{
		if (dmg > 0 && dmg <= icons.length)
		{
			return icons[dmg - 1];
		}
		else
		{
			return itemIcon;
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!event.world.isRemote && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == ClayBucketMod.claybucket)
		{
			TileEntity te = event.world.getTileEntity(event.x, event.y, event.z);
			if (te != null && te instanceof IFluidHandler)
			{
				if (event.entityPlayer.getCurrentEquippedItem().getItemDamage() == 0) /* Empty */
				{
					IFluidHandler tank = (IFluidHandler) te;
					FluidStack stack = tank.drain(ForgeDirection.getOrientation(event.face), AMOUNT, false);
					if (stack != null && stack.amount == AMOUNT)
					{
						for (int i = 0; i < ClayBucketMod.FLUIDS.length; i++)
						{
							if (stack.getFluid() == ClayBucketMod.FLUIDS[i])
							{
								ItemStack item = event.entityPlayer.getCurrentEquippedItem();
								item.setItemDamage(i + 1);
								tank.drain(ForgeDirection.getOrientation(event.face), AMOUNT, true);
								event.entityPlayer.setCurrentItemOrArmor(0, item);
								if (event.isCancelable())
								{
									event.setCanceled(true);
								}
								return;
							}
						}
					}
				}
				else /* Filled */
				{
					IFluidHandler tank = (IFluidHandler) te;
					FluidStack fluid = new FluidStack(ClayBucketMod.FLUIDS[event.entityPlayer.getCurrentEquippedItem().getItemDamage() - 1], AMOUNT);
					if (tank.fill(ForgeDirection.getOrientation(event.face), fluid, false) == AMOUNT)
					{
						ItemStack item = event.entityPlayer.getCurrentEquippedItem();
						if (ClayBucketMod.DESTROY_BUCKET[item.getItemDamage() - 1])
						{
							item.stackSize = 0;
						}
						else
						{
							item.setItemDamage(0);
						}
						tank.fill(ForgeDirection.getOrientation(event.face), fluid, true);
						event.entityPlayer.setCurrentItemOrArmor(0, item);
						if (event.isCancelable())
						{
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (!world.isRemote && stack != null)
		{
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, stack.getItemDamage() == 0);

			if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			{
				int x = mop.blockX, y = mop.blockY, z = mop.blockZ;

				TileEntity te = world.getTileEntity(x, y, z);
				if (te != null && te instanceof IFluidHandler)
				{
					/* gets handled by the SubscribeEvent */
					return stack;
				}

				if (stack.getItemDamage() == 0) /* Empty */
				{
					Block block = world.getBlock(x, y, z);
					for (int i = 0; i < ClayBucketMod.BLOCKS.length; i++)
					{
						if (block.equals(ClayBucketMod.BLOCKS[i]))
						{
							stack.setItemDamage(i + 1);
							world.setBlockToAir(x, y, z);
							break;
						}
					}
				}
				else /* Filled */
				{
					Block block = ClayBucketMod.BLOCKS[stack.getItemDamage() - 1];
					switch (mop.sideHit)
					{
						case 0:
							--y;
							break;
						case 1:
							++y;
							break;
						case 2:
							--z;
							break;
						case 3:
							++z;
							break;
						case 4:
							--x;
							break;
						case 5:
							++x;
							break;
					}
					if (block.isReplaceable(world, x, y, z))
					{
						if (block == Blocks.water)
						{
							block = Blocks.flowing_water;
						}
						else if (block == Blocks.lava)
						{
							block = Blocks.flowing_lava;
						}

						if (world.getBiomeGenForCoords(x, z).isEqualTo(BiomeGenBase.hell))
						{
							int no = player.getRNG().nextInt(NETHER_LINES);
							player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("chat.claybucketnether." + no)));
						}
						else
						{
							world.setBlock(x, y, z, block, 0, 3);

							if (ClayBucketMod.DESTROY_BUCKET[stack.getItemDamage() - 1])
							{
								stack.stackSize = 0;
							}
							else
							{
								stack.setItemDamage(0);
							}
						}
					}
				}
			}
		}
		return stack;
	}
}
