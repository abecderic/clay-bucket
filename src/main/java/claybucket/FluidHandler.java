package claybucket;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ItemFluidContainer;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * FluidHandlerItemStackSimple is a template capability provider for ItemStacks.
 * Data is stored directly in the vanilla NBT, in the same way as the old deprecated {@link ItemFluidContainer}.
 *
 * This implementation only allows item containers to be fully filled or emptied, similar to vanilla buckets.
 */
public class FluidHandler implements IFluidHandler, ICapabilityProvider
{
    protected final ItemStack container;
    protected final int capacity;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public FluidHandler(ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    @Nullable
    public FluidStack getFluid()
    {
        if (container.getItemDamage() == 0) return null;
        if (container.getItemDamage() > Items.FLUIDS.length + 1) return null;
        return new FluidStack(Items.FLUIDS[container.getItemDamage() - 1], capacity);
    }

    @Override
    public IFluidTankProperties[] getTankProperties()
    {
        return new IFluidTankProperties[] { new FluidTankProperties(getFluid(), capacity) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill)
    {
        if (container.getItemDamage() != 0) return 0;
        if (resource.amount < capacity) return 0;
        for (int i = 0; i < Items.FLUIDS.length; i++)
        {
            Fluid fluid = Items.FLUIDS[i];
            if (resource.getFluid().equals(fluid))
            {
                if (doFill)
                {
                    container.setItemDamage(i + 1);
                }
                return Math.min(capacity, resource.amount);
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.stackSize != 1 || resource == null || resource.amount <= 0 || !resource.isFluidEqual(getFluid()))
        {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain)
    {
        if (container.getItemDamage() <= 0) return null;
        if (maxDrain < 1000) return null;
        int dmg = container.getItemDamage();
        if (doDrain)
        {
            if (Items.DESTROY_BUCKET[dmg - 1])
            {
                container.stackSize = 0;
            }
            else
            {
                container.setItemDamage(0);
            }
        }
        return new FluidStack(Items.FLUIDS[dmg - 1], capacity);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? (T) this : null;
    }
}