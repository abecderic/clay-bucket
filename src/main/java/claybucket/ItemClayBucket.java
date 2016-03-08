package claybucket;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ItemClayBucket extends Item
{
    private static final int AMOUNT = 1000;
    private static final int NETHER_LINES = 6;

    public ItemClayBucket()
    {
        this.setCreativeTab(CreativeTabs.tabAllSearch);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getUnlocalizedName()
    {
        return "item." + ClayBucketMod.MODID + ":" + Items.CLAYBUCKET;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (stack.getItemDamage() > 0 && stack.getItemDamage() <= Items.NAMES.length)
        {
            return getUnlocalizedName() + "_" + Items.NAMES[stack.getItemDamage() - 1];
        }
        else
        {
            return getUnlocalizedName();
        }
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List subItems)
    {
        for (int i = 0; i <= Items.NAMES.length; i++)
        {
            subItems.add(new ItemStack(Items.claybucket, 1, i));
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (!event.world.isRemote && event.entityPlayer.getCurrentEquippedItem() != null && event.entityPlayer.getCurrentEquippedItem().getItem() == Items.claybucket)
        {
            TileEntity te = event.world.getTileEntity(event.pos);
            if (te != null && te instanceof IFluidHandler)
            {
                if (event.entityPlayer.getCurrentEquippedItem().getItemDamage() == 0) /* Empty */
                {
                    IFluidHandler tank = (IFluidHandler) te;
                    FluidStack stack = tank.drain(event.face, AMOUNT, false);
                    if (stack.amount == AMOUNT)
                    {
                        for (int i = 0; i < Items.FLUIDS.length; i++)
                        {
                            if (stack.getFluid() == Items.FLUIDS[i])
                            {
                                ItemStack item = event.entityPlayer.getCurrentEquippedItem();
                                item.setItemDamage(i + 1);
                                tank.drain(event.face, AMOUNT, true);
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
                    FluidStack fluid = new FluidStack(Items.FLUIDS[event.entityPlayer.getCurrentEquippedItem().getItemDamage() - 1], AMOUNT);
                    if (tank.fill(event.face, fluid, false) == AMOUNT)
                    {
                        ItemStack item = event.entityPlayer.getCurrentEquippedItem();
                        if (Items.DESTROY_BUCKET[item.getItemDamage() - 1])
                        {
                            item.stackSize = 0;
                        }
                        else
                        {
                            item.setItemDamage(0);
                        }
                        tank.fill(event.face, fluid, true);
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
                BlockPos pos = mop.getBlockPos();
                TileEntity te = world.getTileEntity(pos);
                if (te != null && te instanceof IFluidHandler)
                {
                    /* gets handled by the SubscribeEvent */
                    return stack;
                }

                if (stack.getItemDamage() == 0) /* Empty */
                {
                    Block block = world.getBlockState(pos).getBlock();
                    for (int i = 0; i < Items.BLOCKS.length; i++)
                    {
                        if (block.equals(Items.BLOCKS[i]))
                        {
                            stack.setItemDamage(i + 1);
                            world.setBlockToAir(pos);
                            break;
                        }
                    }
                }
                else /* Filled */
                {
                    Block block = Items.BLOCKS[stack.getItemDamage() - 1];
                    pos = pos.offset(mop.sideHit);
                    if (block.isReplaceable(world, pos))
                    {
                        if (block == Blocks.water)
                        {
                            block = Blocks.flowing_water;
                        }
                        else if (block == Blocks.lava)
                        {
                            block = Blocks.flowing_lava;
                        }

                        if (world.getBiomeGenForCoords(pos).isEqualTo(BiomeGenBase.hell))
                        {
                            int no = player.getRNG().nextInt(NETHER_LINES);
                            player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal("chat.claybucketnether." + no)));
                        }
                        else
                        {
                            world.setBlockState(pos, block.getDefaultState(), 3);

                            if (Items.DESTROY_BUCKET[stack.getItemDamage() - 1])
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
