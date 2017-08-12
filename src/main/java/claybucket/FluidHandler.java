/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package claybucket;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FluidHandlerItemStackSimple is a template capability provider for ItemStacks.
 * Data is stored directly in the vanilla NBT, in the same way as the old ItemFluidContainer.
 *
 * This implementation only allows item containers to be fully filled or emptied, similar to vanilla buckets.
 */
public class FluidHandler implements IFluidHandlerItem, ICapabilityProvider
{
    @Nonnull
    protected ItemStack container;
    protected int capacity;

    /**
     * @param container  The container itemStack, data is stored on it directly as NBT.
     * @param capacity   The maximum capacity of this fluid tank.
     */
    public FluidHandler(@Nonnull ItemStack container, int capacity)
    {
        this.container = container;
        this.capacity = capacity;
    }

    @Nonnull
    @Override
    public ItemStack getContainer()
    {
        return container;
    }

    @Nullable
    public FluidStack getFluid()
    {
        if (container.getItemDamage() == 0) return null;
        if (container.getItemDamage() > Items.FLUIDS.length + 1) return null;
        return new FluidStack(Items.FLUIDS[container.getItemDamage() - 1], capacity);
    }

    protected void setFluid(FluidStack fluid)
    {
        if (fluid.amount == capacity)
        {
            for (int i = 0; i < Items.FLUIDS.length; i++)
            {
                if (Items.FLUIDS[i].equals(fluid.getFluid()))
                {
                    container.setItemDamage(i+1);
                    return;
                }
            }
        }
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
                return capacity;
            }
        }
        return 0;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain)
    {
        if (container.getCount() != 1 || resource == null || resource.amount <= 0 || !resource.isFluidEqual(getFluid()))
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
                container.setCount(0);
            }
            else
            {
                container.setItemDamage(0);
            }
        }
        return new FluidStack(Items.FLUIDS[dmg - 1], capacity);
    }

    public boolean canFillFluidType(FluidStack fluid)
    {
        for (Fluid f : Items.FLUIDS)
        {
            if (f.equals(fluid.getFluid()))
                return true;
        }
        return false;
    }

    public boolean canDrainFluidType(FluidStack fluid)
    {
        return true;
    }

    /**
     * Override this method for special handling.
     * Can be used to swap out the container's item for a different one with "container.setItem".
     * Can be used to destroy the container with "container.stackSize--"
     */
    protected void setContainerToEmpty()
    {
        container.setItemDamage(0);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
    }
}