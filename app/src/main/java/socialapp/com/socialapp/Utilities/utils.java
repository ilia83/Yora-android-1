package socialapp.com.socialapp.Utilities;

import android.app.Activity;
import android.widget.Toast;


public class utils {

    public static void tmsg(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

}
