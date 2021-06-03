package mca.blocks;

import mca.core.MCA;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class JewelerWorkbench extends HorizontalBlock{
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.1D, 1.0D, 15.0D, 24.0D, 15.0D);
    public static final DirectionProperty JFACING = HorizontalBlock.FACING;

    public JewelerWorkbench(Properties builder) {
        super(builder);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(JFACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack item, @Nullable IBlockReader iBlock, List<ITextComponent> tooltip, ITooltipFlag iTooltipFlag) {
        tooltip.add(new StringTextComponent("Workbench allows you to buy rings from Jeweler").withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent(String.format("tooltip.%s.block.statue.line1", MCA.MOD_ID)).withStyle(TextFormatting.GRAY));
        tooltip.add(new TranslationTextComponent(String.format("tooltip.%s.block.statue.line2", MCA.MOD_ID)).withStyle(TextFormatting.GRAY));
    }

    /*@Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    //private static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 6.0D, 11.0D);

    private static final VoxelShape SHAPE_PILLAR = Block.box(5, 2, 5, 11, 14, 11);
    private static final VoxelShape SHAPE_BOTTOM1 = Block.box(1, 0, 1, 15, 1, 15);
    private static final VoxelShape SHAPE_BOTTOM2 = Block.box(3, 1, 3, 13, 2, 13);
    private static final VoxelShape SHAPE_TOP1 = Block.box(1, 15, 1, 15, 16, 15);
    private static final VoxelShape SHAPE_TOP2 = Block.box(3, 14, 3, 13, 15, 13);
    private static final VoxelShape SHAPE = createShape(SHAPE_PILLAR, SHAPE_BOTTOM1, SHAPE_BOTTOM2, SHAPE_TOP1, SHAPE_TOP2);


    private static VoxelShape createShape(VoxelShape... shapes) {
        VoxelShape shape = shapes[0];
        for (int i = 1; i < shapes.length; ++i) {
            shape = VoxelShapes.or(shape, shapes[i]);
        }
        return shape;
    }
*/
}
