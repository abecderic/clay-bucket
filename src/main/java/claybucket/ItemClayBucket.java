package claybucket;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
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
        if (event.getWorld().isRemote) return;

        EnumHand hand = null;
        if (event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND) != null && event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.claybucket)
        {
            hand = EnumHand.MAIN_HAND;
        }
        else if (event.getEntityPlayer().getHeldItem(EnumHand.OFF_HAND) != null && event.getEntityPlayer().getHeldItem(EnumHand.OFF_HAND).getItem() == Items.claybucket)
        {
            hand = EnumHand.OFF_HAND;
        }
        if (hand == null) return;

        TileEntity te = event.getWorld().getTileEntity(event.getPos());
        if (te != null && te instanceof IFluidHandler)
        {
            if (event.getEntityPlayer().getHeldItem(hand).getItemDamage() == 0) /* Empty */
            {
                IFluidHandler tank = (IFluidHandler) te;
                FluidStack stack = tank.drain(event.getFace(), AMOUNT, false);
                if (stack != null && stack.amount == AMOUNT)
                {
                    for (int i = 0; i < Items.FLUIDS.length; i++)
                    {
                        if (stack.getFluid() == Items.FLUIDS[i])
                        {
                            ItemStack item = event.getEntityPlayer().getHeldItem(hand);
                            item.setItemDamage(i + 1);
                            tank.drain(event.getFace(), AMOUNT, true);
                            event.getEntityPlayer().setHeldItem(hand, item);
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
                FluidStack fluid = new FluidStack(Items.FLUIDS[event.getEntityPlayer().getHeldItem(hand).getItemDamage() - 1], AMOUNT);
                if (tank.fill(event.getFace(), fluid, false) == AMOUNT)
                {
                    ItemStack item = event.getEntityPlayer().getHeldItem(hand);
                    if (Items.DESTROY_BUCKET[item.getItemDamage() - 1])
                    {
                        item.stackSize = 0;
                    }
                    else
                    {
                        item.setItemDamage(0);
                    }
                    tank.fill(event.getFace(), fluid, true);
                    event.getEntityPlayer().setHeldItem(hand, item);
                    if (event.isCancelable())
                    {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!worldIn.isRemote && stack != null)
        {
            RayTraceResult rtr = this.getMovingObjectPositionFromPlayer(worldIn, playerIn, stack.getItemDamage() == 0);

            if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK)
            {
                BlockPos pos = rtr.getBlockPos();
                TileEntity te = worldIn.getTileEntity(pos);
                if (te != null && te instanceof IFluidHandler)
                {
                    /* gets handled by the SubscribeEvent */
                    return ActionResult.newResult(EnumActionResult.FAIL, stack);
                }

                if (stack.getItemDamage() == 0) /* Empty */
                {
                    Block block = worldIn.getBlockState(pos).getBlock();
                    for (int i = 0; i < Items.BLOCKS.length; i++)
                    {
                        if (block.equals(Items.BLOCKS[i]))
                        {
                            stack.setItemDamage(i + 1);
                            worldIn.setBlockToAir(pos);
                            break;
                        }
                    }
                }
                else /* Filled */
                {
                    Block block = Items.BLOCKS[stack.getItemDamage() - 1];
                    pos = pos.offset(rtr.sideHit);
                    if (block.isReplaceable(worldIn, pos))
                    {
                        if (block == Blocks.water)
                        {
                            block = Blocks.flowing_water;
                        }
                        else if (block == Blocks.lava)
                        {
                            block = Blocks.flowing_lava;
                        }

                        if (worldIn.getBiomeGenForCoords(pos).equals(Biomes.hell))
                        {
                            int no = playerIn.getRNG().nextInt(NETHER_LINES);
                            playerIn.addChatComponentMessage(new TextComponentTranslation("chat.claybucketnether." + no));
                        }
                        else
                        {
                            worldIn.setBlockState(pos, block.getDefaultState(), 3);

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
                return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
            }
        }
        return ActionResult.newResult(EnumActionResult.PASS, stack);
    }
}
