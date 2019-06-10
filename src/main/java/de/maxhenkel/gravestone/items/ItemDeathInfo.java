package de.maxhenkel.gravestone.items;

import de.maxhenkel.gravestone.DeathInfo;
import de.maxhenkel.gravestone.gui.ContainerDeathItems;
import de.maxhenkel.gravestone.gui.GUIDeathInfo;
import de.maxhenkel.gravestone.gui.InventoryDeathItems;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ItemDeathInfo extends Item {

    public ItemDeathInfo() {
        super(new Item.Properties().maxStackSize(1));
        this.setRegistryName("death_info");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        DeathInfo info = DeathInfo.getDeathInfoFromPlayerHand(playerIn);

        if (playerIn.isSneaking() && playerIn.playerAbilities.isCreativeMode) {
            if (playerIn instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) playerIn, new INamedContainerProvider() {

                    @Nullable
                    @Override
                    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                        return new ContainerDeathItems(id, playerInventory, new InventoryDeathItems(info));
                    }

                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent(ItemDeathInfo.this.getTranslationKey());
                    }
                });
            }
        } else if (worldIn.isRemote) {
            openClientGui(info);
        }
        return ActionResult.newResult(ActionResultType.SUCCESS, playerIn.getHeldItem(hand));
    }

    @OnlyIn(Dist.CLIENT)
    private void openClientGui(DeathInfo info) {
        Minecraft.getInstance().displayGuiScreen(new GUIDeathInfo(info));
    }
}
