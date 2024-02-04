package surrender.chosenenchants.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreen {
	@Shadow @Nullable private String newItemName;

	@Shadow @Final private Property levelCost;

	@Inject(at = @At("HEAD"), method = "updateResult", cancellable = true)
	private void init(CallbackInfo info) {
		// Access the result slot of the anvil screen handler
		AnvilScreenHandler anvilScreenHandler = (AnvilScreenHandler) (Object) this;
		//checks if all slots have the correct items
		if (anvilScreenHandler.getSlot(0).getStack().getItem() == Items.BOOK && anvilScreenHandler.getSlot(1).getStack().getItem() == Items.LAPIS_LAZULI) {
			ItemStack book = anvilScreenHandler.getSlot(0).getStack();
			ItemStack lapis = anvilScreenHandler.getSlot(1).getStack();
			if (book.getCount() == 1 && lapis.getCount() == 1) {
				// Check if the input name is not null and the book actually got renamed
				if (this.newItemName != null && !this.newItemName.equals(book.getName().getString())) {
					String bookName = this.newItemName.toLowerCase().replace(' ', '_');
					// Check if the custom name matches a known enchantment
					Enchantment enchantment = getEnchantment(bookName);
					if (enchantment != null) {
						//sets item specifics
						ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
						EnchantmentLevelEntry enchantmentEntry = new EnchantmentLevelEntry(enchantment, 1);
						EnchantedBookItem.addEnchantment(enchantedBook, enchantmentEntry);
						// Replaces output slot with enchanted book
						anvilScreenHandler.setStackInSlot(2, 0,enchantedBook);
						this.levelCost.set(5);
						info.cancel(); // cancels any other actions the anvil might do.
					}
				}

			}
		}
	}
	@Unique
	private Enchantment getEnchantment(String name) {
		if (Registries.ENCHANTMENT.get(Identifier.tryParse(name)) != null) {
			return Registries.ENCHANTMENT.get(Identifier.tryParse(name));
		}
		return null; // Return null if no matching enchantment is found
	}
}