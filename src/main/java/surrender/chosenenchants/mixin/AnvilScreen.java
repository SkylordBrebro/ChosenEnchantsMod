package surrender.chosenenchants.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreen {

	@Shadow
	@Nullable
	private String newItemName;

	@Inject(at = @At("TAIL"), method = "updateResult")
	private void init(CallbackInfo info) {
		AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler) (Object) this;
		// Access the result slot of the anvil screen handler
		ItemStack resultStack = anvilScreenHandler.getSlot(2).getStack();

		// Check if the resultStack is not null and has a display name
		if (resultStack != null && resultStack.hasCustomName()) {
			// Get the display name as a string

			// Print or use the itemName string as needed
			System.out.println("Item renamed to: " + newItemName + resultStack.getItem());
		}
	}
}