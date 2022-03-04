package pandorum.commands.discord;

import arc.files.Fi;
import mindustry.io.SaveIO;
import net.dv8tion.jda.api.entities.Message.Attachment;
import pandorum.discord.Context;

import static mindustry.Vars.*;

public class AddMapCommand {
    public static void run(final String[] args, final Context context) {
        if (context.attachments.size() != 1 || !context.attachments.get(0).getFileName().endsWith(mapExtension)) {
            context.err(":link: Неверное вложение.", "Тебе нужно прикрепить один файл с расширением **.msav!**");
            return;
        }

        Attachment attachment = context.attachments.get(0);

        attachment.downloadToFile(customMapDirectory.child(attachment.getFileName()).file()).thenAccept(file -> {
            Fi mapFile = new Fi(file);
            if (!SaveIO.isSaveValid(mapFile)) {
                context.err(":no_entry_sign: Файл поврежден или не является картой!");
                mapFile.delete();
                return;
            }

            maps.reload();
            context.success(":map: Карта добавлена на сервер.");
        }).exceptionally(e -> {
            context.err(":no_entry_sign: Файл поврежден или не является картой!");
            return null;
        });
    }
}
