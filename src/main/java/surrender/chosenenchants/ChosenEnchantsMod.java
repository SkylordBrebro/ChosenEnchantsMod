package surrender.chosenenchants;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ChosenEnchantsMod implements ModInitializer {
	public static final String MOD_ID = "chosenenchants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		// Register the event callback for item usage
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack heldItem = player.getStackInHand(hand);

			// Check if the item is a book and has a custom name
			if (heldItem.getItem() == Items.BOOK && heldItem.hasCustomName()) {
				String bookName = heldItem.getName().getString();

				// Check if the custom name matches a known enchantment
				Enchantment enchantment = getEnchantment(bookName);
				if (enchantment != null) {
					int bookCount = heldItem.getCount();

					// Create enchanted books and drop them
					for (int i = 0; i < bookCount; i++) {
						ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
						EnchantmentLevelEntry enchantmentEntry = new EnchantmentLevelEntry(enchantment, 1);
						EnchantedBookItem.addEnchantment(enchantedBook, enchantmentEntry);

						// Drop the enchanted book
						world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), enchantedBook));
					}

					// Remove the original books from the player's hand
					player.setStackInHand(hand, ItemStack.EMPTY);
					world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0F, 1.0F);

					// Return a TypedActionResult with SUCCESS
					return TypedActionResult.success(player.getStackInHand(hand));
				}
			}

			// Return a TypedActionResult with PASS for other cases
			return TypedActionResult.pass(player.getStackInHand(hand));
		});
	}

	private Enchantment getEnchantment(String name) {
			for (Map.Entry<Identifier, Enchantment> entry : Registries.ENCHANTMENT.) {
				Enchantment enchantment = entry.getValue();
				if (enchantment.getName().equals(name)) {
					return enchantment;
				}
			}
		return null; // Return null if no matching enchantment is found
	}
}
