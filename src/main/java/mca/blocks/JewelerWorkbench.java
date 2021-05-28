package mca.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;
import java.util.Random;

public class JewelerWorkbench extends ContainerBlock {
    private static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    //private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

    private static final VoxelShape SHAPE_PILLAR = Block.box(5, 2, 5, 11, 14, 11);
    private static final VoxelShape SHAPE_BOTTOM1 = Block.box(1, 0, 1, 15, 1, 15);
    private static final VoxelShape SHAPE_BOTTOM2 = Block.box(3, 1, 3, 13, 2, 13);
    private static final VoxelShape SHAPE_TOP1 = Block.box(1, 15, 1, 15, 16, 15);
    private static final VoxelShape SHAPE_TOP2 = Block.box(3, 14, 3, 13, 15, 13);
    private static final VoxelShape SHAPE = createShape(SHAPE_PILLAR, SHAPE_BOTTOM1, SHAPE_BOTTOM2, SHAPE_TOP1, SHAPE_TOP2);
    private Properties registerDefaultState;


    private static VoxelShape createShape(VoxelShape... shapes) {
        VoxelShape shape = shapes[0];
        for (int i = 1; i < shapes.length; ++i) {
            shape = VoxelShapes.or(shape, shapes[i]);
        }
        return shape;
    }

    public JewelerWorkbench(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(OPEN, Boolean.valueOf(false)));

    }

    @Nullable
    @Override
    public TileEntity newBlockEntity(IBlockReader p_196283_1_) {


        return null; // TODO Jeweler Workbench BlockReader

    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        // TODO animate
    }

    public BlockState rotate(BlockState blockParameter, Rotation posRotation) {
        return blockParameter.setValue(FACING, posRotation.rotate(blockParameter.getValue(FACING)));
    }

    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack item, @Nullable IBlockReader iBlock, List<ITextComponent> tooltip, ITooltipFlag iTooltipFlag) {
        tooltip.add(new StringTextComponent("Workbench allows you to buy rings from Jeweler"));
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
