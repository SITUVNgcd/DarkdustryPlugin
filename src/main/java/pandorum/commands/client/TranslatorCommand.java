package pandorum.commands.client;

import arc.util.CommandHandler.CommandRunner;
import mindustry.gen.Player;
import pandorum.data.PlayerData;

import static pandorum.PluginVars.translatorLocales;
import static pandorum.data.Database.getPlayerData;
import static pandorum.data.Database.setPlayerData;
import static pandorum.util.PlayerUtils.bundled;
import static pandorum.util.Search.findTranslatorLocale;

public class TranslatorCommand implements CommandRunner<Player> {
    public void accept(String[] args, Player player) {
        PlayerData data = getPlayerData(player.uuid());

        if (args.length == 0) {
            bundled(player, "commands.tr.current", data.locale);
            return;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> {
                StringBuilder locales = new StringBuilder();
                translatorLocales.keys().toSeq().each(locale -> locales.append(locale).append(" "));
                bundled(player, "commands.tr.list", locales.toString());
            }
            case "off" -> {
                data.locale = "off";
                setPlayerData(player.uuid(), data);
                bundled(player, "commands.tr.disabled");
            }
            case "auto" -> {
                data.locale = "auto";
                setPlayerData(player.uuid(), data);
                bundled(player, "commands.tr.auto");
            }
            default -> {
                String locale = findTranslatorLocale(args[0]);
                if (locale == null) {
                    bundled(player, "commands.tr.incorrect");
                    return;
                }

                data.locale = locale;
                setPlayerData(player.uuid(), data);
                bundled(player, "commands.tr.changed", locale, translatorLocales.get(locale));
            }
        }
    }
}
