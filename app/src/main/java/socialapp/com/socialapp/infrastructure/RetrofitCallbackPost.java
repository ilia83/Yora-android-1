package socialapp.com.socialapp.infrastructure;

import android.util.Log;

import com.squareup.otto.Bus;

/**
 * Created by SAMAR on 5/3/2016.
 */
public class RetrofitCallbackPost<T extends ServiceResponse> extends RetrofitCallback<T> {
    private static final String TAG = "RetrofitCallbackPost";

    private final Bus bus;


    public RetrofitCallbackPost(Class<T> resultType, Bus bus) {
        super(resultType);
        this.bus = bus;
    }



    @Override
    protected void onResponse(T t) {
        if (t == null) {
            try {
                t = resultType.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "Could not create black result obhect", e);
            }
        }

        bus.post(t);
    }
}
