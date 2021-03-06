package de.melanx.aiotbotania.items.base;

import de.melanx.aiotbotania.AIOTBotania;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IForgeShearable;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.List;
import java.util.Random;

public class ItemShearsBase extends ShearsItem implements IManaUsingItem {

    private final int MANA_PER_DAMAGE;

    public ItemShearsBase(int MANA_PER_DAMAGE, int MAX_DMG) {
        super(new Item.Properties().group(AIOTBotania.instance.getTab()).maxStackSize(1).defaultMaxDamage(MAX_DMG));

        this.MANA_PER_DAMAGE = MANA_PER_DAMAGE;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        ToolCommons.damageItem(stack, 1, entityLiving, MANA_PER_DAMAGE);
        return true;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        if (entity.world.isRemote) return ActionResultType.PASS;

        if (entity instanceof IForgeShearable) {
            IForgeShearable target = (IForgeShearable) entity;
            BlockPos pos = new BlockPos(entity.getPosX(), entity.getPosY(), entity.getPosZ());
            if (target.isShearable(stack, entity.world, pos)) {
                List<ItemStack> drops = target.onSheared(player, stack, entity.world, pos,
                        EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
                Random rand = new Random();
                drops.forEach(d -> {
                    ItemEntity entity1 = entity.entityDropItem(d, 1.0F);
                    entity1.setMotion(entity1.getMotion().add((rand.nextFloat() - rand.nextFloat() * 0.1F), rand.nextFloat() * 0.05F, rand.nextFloat() - rand.nextFloat() * 0.1F));
                });
                ToolCommons.damageItem(stack, 1, player, MANA_PER_DAMAGE);
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity player, int invSlot, boolean isCurrentItem) {
        if (!world.isRemote && player instanceof PlayerEntity && stack.getDamage() > 0 && ManaItemHandler.instance().requestManaExactForTool(stack, (PlayerEntity) player, MANA_PER_DAMAGE * 2, true))
            stack.setDamage(stack.getDamage() - 1);
    }

    @Override
    public boolean usesMana(ItemStack itemStack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (enchantment == Enchantments.SILK_TOUCH) return true;
        return super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
