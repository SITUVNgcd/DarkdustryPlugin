package pandorum.events;

import mindustry.gen.Groups;
import pandorum.PandorumPlugin;
import pandorum.effects.Effects;
import pandorum.ranks.Ranks;

public class TriggerUpdateListener {
    public static void call() {
        Groups.player.each(p -> p.unit().moving(), Effects::onMove);
        if (PandorumPlugin.interval.get(1, 30f)) Groups.player.each(p -> p.name(Ranks.getRank(p).tag + p.getInfo().lastName));
    }
}
