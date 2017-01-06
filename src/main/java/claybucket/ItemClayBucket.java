package claybucket;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemClayBucket extends ItemFluidContainer
{
    private static final int AMOUNT = 1000;
    private static final int NETHER_LINES = 6;

    public ItemClayBucket()
    {
        super(AMOUNT);
        this.setCreativeTab(CreativeTabs.MISC);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        MinecraftForge.EVENT_BUS.register(this);
        this.setRegistryName(Items.CLAYBUCKET);
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
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
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
                FluidStack stack = tank.drain(AMOUNT, false);
                if (stack != null && stack.amount == AMOUNT)
                {
                    for (int i = 0; i < Items.FLUIDS.length; i++)
                    {
                        if (stack.getFluid() == Items.FLUIDS[i])
                        {
                            ItemStack item = event.getEntityPlayer().getHeldItem(hand);
                            item.setItemDamage(i + 1);
                            tank.drain(AMOUNT, true);
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
                if (tank.fill(fluid, false) == AMOUNT)
                {
                    ItemStack item = event.getEntityPlayer().getHeldItem(hand);
                    if (Items.DESTROY_BUCKET[item.getItemDamage() - 1])
                    {
                        item.func_190918_g(item.func_190916_E()); /* set stackSize to 0 */
                    }
                    else
                    {
                        item.setItemDamage(0);
                    }
                    tank.fill(fluid, true);
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!worldIn.isRemote && stack != null)
        {
            RayTraceResult rtr = this.rayTrace(worldIn, playerIn, stack.getItemDamage() == 0);

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
                        if (block == Blocks.WATER)
                        {
                            block = Blocks.FLOWING_WATER;
                        }
                        else if (block == Blocks.LAVA)
                        {
                            block = Blocks.FLOWING_LAVA;
                        }

                        if (worldIn.getBiome(pos).equals(Biomes.HELL) && block == Blocks.FLOWING_WATER)
                        {
                            int no = playerIn.getRNG().nextInt(NETHER_LINES);
                            playerIn.addChatComponentMessage(new TextComponentTranslation("chat.claybucketnether." + no), false); /* false = regular chat */
                        }
                        else
                        {
                            worldIn.setBlockState(pos, block.getDefaultState(), 3);

                            if (Items.DESTROY_BUCKET[stack.getItemDamage() - 1])
                            {
                                stack.func_190918_g(stack.func_190916_E()); /* set stackSize to 0 */
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
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt)
    {
        return new FluidHandler(stack, AMOUNT);
    }
}
