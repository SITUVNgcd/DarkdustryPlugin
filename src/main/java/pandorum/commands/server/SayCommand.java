package pandorum.commands.server;

import arc.util.Log;
import pandorum.discord.BotHandler;

import static pandorum.Misc.sendToChat;

public class SayCommand {
    public static void run(final String[] args) {
        sendToChat("commands.say.chat", args[0]);
        Log.info("Server: &ly@", args[0]);
        BotHandler.text("**Сервер**: @", args[0]);
    }
}
