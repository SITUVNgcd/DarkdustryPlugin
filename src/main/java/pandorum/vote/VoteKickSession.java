package pandorum.vote;

import arc.util.Timer;
import arc.util.Timer.Task;
import mindustry.gen.Groups;
import mindustry.gen.Player;

import static pandorum.PluginVars.kickDuration;
import static pandorum.PluginVars.voteKickDuration;
import static pandorum.util.PlayerUtils.kick;
import static pandorum.util.PlayerUtils.sendToChat;
import static pandorum.util.Utils.millisecondsToMinutes;

public class VoteKickSession extends VoteSession {

    protected final Player started;
    protected final Player target;

    public VoteKickSession(VoteKickSession[] voteKickSession, Player started, Player target) {
        super(voteKickSession);
        this.started = started;
        this.target = target;
    }

    @Override
    public Task start() {
        return Timer.schedule(() -> {
            if (!checkPass()) {
                sendToChat("commands.votekick.failed", target.coloredName());
                stop();
            }
        }, voteKickDuration);
    }

    @Override
    public void vote(Player player, int sign) {
        votes += sign;
        voted.add(player.uuid());
        sendToChat("commands.votekick.vote", player.coloredName(), target.coloredName(), votes, votesRequired());
        checkPass();
    }

    @Override
    public boolean checkPass() {
        if (votes >= votesRequired()) {
            sendToChat("commands.votekick.passed", target.coloredName(), millisecondsToMinutes(kickDuration));
            stop();
            kick(target, kickDuration, true, "kick.votekicked", started.coloredName());
            return true;
        }
        return false;
    }

    @Override
    public int votesRequired() {
        return Groups.player.size() > 4 ? 3 : 2;
    }

    public Player target() {
        return target;
    }
}
