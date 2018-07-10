package sgm.recevicecardanimation;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import sgm.recevicecardanimation.adapter.CardContactAdapter;
import sgm.recevicecardanimation.model.User;
import sgm.recevicecardanimation.socket.SocketListener;
import sgm.recevicecardanimation.utils.AppConstants;
import sgm.recevicecardanimation.utils.AppUtils;
import sgm.recevicecardanimation.utils.CenterRecycleViewSnapHelper;
import sgm.recevicecardanimation.utils.RequestDataUtils;
import sgm.recevicecardanimation.utils.SwipeController;

public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback {
    private static final long TIME_UPDATE_CURRENT_LOCATION = 10000;
    @BindView(R.id.rcy_card)
    RecyclerView mRcyCard;
    @BindView(R.id.imv_down)
    ImageView mImvDown;
    @BindView(R.id.ll_footer)
    LinearLayout mLlFooter;
    @BindView(R.id.imv_loading)
    ImageView mImvLoading;
    @BindView(R.id.ll_loading)
    LinearLayout mLlLoading;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    private CardContactAdapter mAdapter;
    private ArrayList<User> mListContact;
    private ArrayList<User> mListReject;
    private OkHttpClient mClient;
    private User mReciveUser = null;
    private boolean isSendRequest = false;
    private WebSocket mWs = null;
    private double mLatitude = 0;
    private double mLongitude = 0;
    private FusedLocationProviderClient mFusedLocationClient;
    private Timer mTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initView();
        setupRecycleView();
        if (mClient == null)
            mClient = new OkHttpClient();
        initTimer();
    }

    private void initTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // use runOnUiThread(Runnable action)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initReceiver(mLatitude, mLongitude);
                        }
                    });
                }
            }, TIME_UPDATE_CURRENT_LOCATION, TIME_UPDATE_CURRENT_LOCATION);
        }
    }

    @OnClick({R.id.imv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.imv_back:
                onBackPressed();
                break;
        }
    }

    private void initView() {
        Glide.with(MainActivity.this).asGif().load(R.drawable.delete_cr).into(mImvDown);
        Glide.with(MainActivity.this).asGif().load(R.drawable.waiting_cr).into(mImvLoading);
        mListContact = new ArrayList<>();
        mListReject = new ArrayList<>();
        int actionBarHeight = 1;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        int paddingTop = getResources().getDimensionPixelSize(R.dimen.value_25dp);
        int paddingBottom = getResources().getDimensionPixelSize(R.dimen.value_90dp);
        int heightOfRecycleView = AppUtils.getScreenHigh() - paddingTop - paddingBottom - actionBarHeight;
        mAdapter = new CardContactAdapter(MainActivity.this, mListContact, heightOfRecycleView, new CardContactAdapter.onActionSendRequest() {
            @Override
            public void onSend(User user, int position) {
                try {
                    mListContact.get(position).setStatus(AppConstants.STATUS_USER.REQUESTING);
                    mAdapter.notifyItemChanged(position);
//                mAdapter.notifyDataSetChanged();
                    sendRequestAddContact(user, position);
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                }

            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRcyCard.setLayoutManager(lm);
        mRcyCard.setHasFixedSize(true);
        mRcyCard.setAdapter(mAdapter);
    }

    private void initReceiver(double latitude, double longitude) {
        Request request = new Request.Builder().url(AppConstants.WebSocketLink
                .LINK_SEND).build();
        SocketListener listener = new SocketListener(latitude, longitude);
        mWs = mClient.newWebSocket(request, listener);
        listener.setActionRequest(new SocketListener.onActionRequest() {
            @Override
            public void onStart() {
                Log.e("Socket", "start");
            }

            @Override
            public void onDone(String result) {
                Log.e("Socket", "done");
                try {
                    JSONObject person = (new JSONObject(result)).getJSONObject(AppConstants
                            .KEY_PARAMS.DATA.toString());
                    mReciveUser = User.parse(person);
                    if (mReciveUser != null) {
                        if (checkCanAddItem(mReciveUser)) {
                            mListContact.add(mReciveUser);

                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                            changeUiView();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initListCard();
            }

            @Override
            public void onFail() {
                Log.e("Socket", "fail");
                initListCard();
            }

            @Override
            public void onClose() {
                Log.e("Socket", "close");
            }
        });
    }

    /**
     * Using to init list contact.
     */
    private void initListCard() {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setStatus(AppConstants.STATUS_USER.NORMAL);
            user.setId(i);
            user.setAvatar((i % 2 == 0) ? "https://anhdephd.com/wp-content/uploads/2017/11/anh-hot-girl-xinh-tuoi-mua-noel.jpg" : "https://images.kienthuc.net.vn/zoomh/500/uploaded/manhtu/2017_06_12/3/gai-xinh-dong-nai-khien-dan-mang-chao-dao-Hinh-6.jpg");
            user.setFullname("Full Name :" + i);
            user.setProfile("Profile : " + i);
            mListContact.add(user);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                changeUiView();
            }
        });
    }

    private boolean checkCanAddItem(User user) {
        if (mListContact.size() == 0)
            return true;
        for (int i = 0; i < mListContact.size(); i++) {
            User check = mListContact.get(i);
            if (check.getId() == user.getId()) {
                return false;
            }
        }
        for (int i = 0; i < mListReject.size(); i++) {
            User check = mListReject.get(i);
            if (check.getId() == user.getId()) {
                return false;
            }
        }
        return true;
    }

    private void setupRecycleView() {
        SwipeController swipeController = new SwipeController(new SwipeController.onActionSwipe() {
            @Override
            public void onDelete(final int position) {
                if (isSendRequest)
                    return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListReject.add(mListContact.get(position));
                        removeItemCard(position);
                    }
                });
            }

            @Override
            public void onReject() {

            }
        });
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(mRcyCard);
        CenterRecycleViewSnapHelper snap = new CenterRecycleViewSnapHelper();
        snap.attachToRecyclerView(mRcyCard);
    }

    private void sendRequestAddContact(User user, final int position) {
        mLlFooter.setVisibility(View.INVISIBLE);
        isSendRequest = true;
        Map<String, String> params = new HashMap<>();
        params.put("CONTACT_ID", String.valueOf(user.getId()));
        RequestDataUtils.requestData(com.android.volley.Request.Method.POST, MainActivity.this, "url_add.php",
                params, new RequestDataUtils.onResult() {
                    @Override
                    public void onSuccess(JSONObject object, String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mListContact.get(position).setStatus(AppConstants.STATUS_USER.DONE);
                                    mAdapter.notifyItemChanged(position);
//                                mListContact.get(position).setStatus(AppConstants.STATUS_USER.DONE);
//                                mAdapter.notifyItemChanged(position);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            removeItemCard(position);
                                        }
                                    }, 1200);
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFail(int error) {
                        Toast.makeText(MainActivity.this, getString(R.string.msg_request_error_try_again), Toast.LENGTH_SHORT).show();
                        mListContact.get(position).setStatus(AppConstants.STATUS_USER.NORMAL);
                        mAdapter.notifyItemChanged(position);
                        isSendRequest = false;
                    }
                });
    }

    private void removeItemCard(int index) {
        int size = mListContact.size();
        if (size > 0) {
            try {
                mListContact.remove(index);
                mAdapter.removeItemAtIndex(index);
                if (mListContact.size() == 0)
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2);
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2);
            }
            changeUiView();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2);
        }
    }

    private void changeUiView() {
        int totalContact = mListContact.size();
        mLlFooter.setVisibility(totalContact > 0 ? View.VISIBLE : View.INVISIBLE);
        mLlLoading.setVisibility(totalContact > 0 ? View.INVISIBLE : View.VISIBLE);
        mTvTitle.setText(totalContact > 0 ? String.valueOf(totalContact) + " requests for reception are coming." : "");
        isSendRequest = false;
    }

    @Override
    protected void onDestroy() {
        if (mClient != null)
            mClient.dispatcher().executorService().shutdown();
        if (mWs != null)
            mWs.cancel();
        if (mTimer != null)
            mTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isSendRequest)
            return;
        super.onBackPressed();
    }
}
