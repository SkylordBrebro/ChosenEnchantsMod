package surrender.chosenenchants;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChosenEnchantsMod implements ModInitializer {
	public static final String MOD_ID = "chosenenchants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Check the player's inventory for books with custom names and perform actions
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkPlayerInventory(player);
            }
        });
		// Register the event callback for item usage
		//UseItemCallback.EVENT.register((player, world, hand) -> {
		//	// Check if the item is a book and has a custom name
		//	ItemStack heldItem = player.getStackInHand(hand);
		//	if (heldItem.getItem() == Items.BOOK && heldItem.hasCustomName()) {
		//		// Process the book and enchantments
		//		processEnchantmentBook(player, world, heldItem);
    	//
		//		// Return a TypedActionResult with SUCCESS
		//		return TypedActionResult.success(player.getStackInHand(hand));
		//	}
		//
		//	// Return a TypedActionResult with PASS for other cases
		//	return TypedActionResult.pass(player.getStackInHand(hand));
		//});
	}
	private void checkPlayerInventory(ServerPlayerEntity player) {
		player.getInventory().main.forEach(itemStack -> {
			if (itemStack.getItem() == Items.BOOK && itemStack.hasCustomName()) {
				String bookName = itemStack.getName().getString().toLowerCase().replace(' ', '_');

				// Process the book and enchantments
				processEnchantmentBook(player, player.getWorld(), itemStack, bookName);
				//player.getInventory().removeOne(itemStack);
			}
		});
	}
	private void processEnchantmentBook(PlayerEntity player, World world, ItemStack book, String bookName) {
		// Check if the custom name matches a known enchantment
		Enchantment enchantment = getEnchantment(bookName);
		if (enchantment != null) {
			int bookCount = book.getCount();

			// Create enchanted books and drop them
			for (int i = 0; i < bookCount; i++) {
				ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
				EnchantmentLevelEntry enchantmentEntry = new EnchantmentLevelEntry(enchantment, 1);
				EnchantedBookItem.addEnchantment(enchantedBook, enchantmentEntry);

				// Drop the enchanted book
				world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), enchantedBook));
			}

			// Remove only books with the same custom name from the player's inventory
			book.setCount(0);
			//player.getInventory().removeOne(itemStack ->
			//		book.getItem().equals(Items.BOOK) && book.hasCustomName() && book.getName().getString().equals(bookName));

			// Optionally, you can play a sound or display a particle effect here
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS, 1.0F, 1.0F);
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
		}
	}


	//public void onInitialize() {
	//
	//	// Register the event callback for item usage
	//	UseItemCallback.EVENT.register((player, world, hand) -> {
	//		ItemStack heldItem = player.getStackInHand(hand);
	//
	//		// Check if the item is a book and has a custom name
	//		if (heldItem.getItem() == Items.BOOK && heldItem.hasCustomName()) {
	//			String bookName = heldItem.getName().getString().toLowerCase().replace(' ', '_');
	//
	//			// Check if the custom name matches a known enchantment
	//			Enchantment enchantment = getEnchantment(bookName);
	//			if (enchantment != null) {
	//				int bookCount = heldItem.getCount();
	//
	//				// Create enchanted books and drop them
	//				for (int i = 0; i < bookCount; i++) {
	//					ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
	//					EnchantmentLevelEntry enchantmentEntry = new EnchantmentLevelEntry(enchantment, 1);
	//					EnchantedBookItem.addEnchantment(enchantedBook, enchantmentEntry);
	//
	//					// Drop the enchanted book
	//					world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), enchantedBook));
	//				}
	//
	//				// Remove the original books from the player's hand
	//				player.setStackInHand(hand, ItemStack.EMPTY);
	//
	//
	//				// Return a TypedActionResult with SUCCESS
	//				return TypedActionResult.success(player.getStackInHand(hand));
	//			}
	//		}
	//
	//		// Return a TypedActionResult with PASS for other cases
	//		return TypedActionResult.pass(player.getStackInHand(hand));
	//	});
	//}

	private Enchantment getEnchantment(String name) {
		if (Registries.ENCHANTMENT.get(Identifier.tryParse(name)) != null) {
			LOGGER.info(String.valueOf(Registries.ENCHANTMENT.get(Identifier.tryParse(name))));
			return Registries.ENCHANTMENT.get(Identifier.tryParse(name));
		}
		return null; // Return null if no matching enchantment is found
	}
}