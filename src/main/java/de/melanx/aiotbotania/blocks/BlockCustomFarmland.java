package de.melanx.aiotbotania.blocks;

import de.melanx.aiotbotania.AIOTBotania;
import de.melanx.aiotbotania.core.config.ConfigHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import vazkii.botania.client.fx.SparkleParticleData;

import java.util.Random;

public class BlockCustomFarmland extends FarmlandBlock {
    public BlockCustomFarmland() {
        super(Block.Properties.create(Material.EARTH).hardnessAndResistance(0.6F).sound(SoundType.GROUND));
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(MOISTURE, 7)
        );
        setRegistryName(AIOTBotania.MODID, "super_farmland");
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        if (ConfigHandler.CLIENT.PARTICLES.get()) {
            int r = 1;
            float g = 0.078F;
            float b = 0.576F;
            SparkleParticleData data = SparkleParticleData.sparkle((float) Math.random(), r, g, b, 3);
            world.addParticle(data, pos.getX() + Math.random(), pos.getY() + Math.random() * 1.5, pos.getZ() + Math.random(), 0, 0, 0);
        }
    }

    @Override
    public void onFallenUpon(World world, BlockPos pos, Entity entity, float fallDistance) {
        entity.fall(fallDistance, 1.0F);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(MOISTURE);
    }

    @Override
    public boolean canSustainPlant(BlockState state, IBlockReader world, BlockPos pos, Direction facing, IPlantable plantable) {
        PlantType plantType = plantable.getPlantType(world, pos.up());
        if (plantType == PlantType.Crop) {
            return true;
        }
        return super.canSustainPlant(state, world, pos, facing, plantable);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return new ItemStack(Blocks.FARMLAND.asItem());
    }
}
