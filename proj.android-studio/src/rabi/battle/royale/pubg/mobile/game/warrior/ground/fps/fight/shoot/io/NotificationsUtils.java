package rabi.battle.royale.pubg.mobile.game.warrior.ground.fps.fight.shoot.io;
import android.content.Context;
import androidx.core.app.NotificationManagerCompat;
public class NotificationsUtils {


    public static boolean isNotificationEnabled(Context context) {

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        boolean isOpened = manager.areNotificationsEnabled();

        if (isOpened) {
            return true;
        }
        else{
            return false;
        }

    }
}
