package surrender.chosenenchants;

import net.fabricmc.api.ModInitializer;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

public class ChosenEnchantsMod implements ModInitializer {
	public static final String MOD_ID = "chosenenchants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Chosen Enchants Initialized.");

	}
}