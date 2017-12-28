package com.android.al3arrab.universalapp.Youtube;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.android.al3arrab.universalapp.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class YouTubeActivity extends AppCompatActivity implements OnFullscreenListener {

    private String REQUEST_URL;
    private static final int ANIMATION_DURATION_MILLIS = 300;
    private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;
    private VideoFragment videoFragment;
    private View videoBox;
    private View closeButton;
    private View listLayout;

    private boolean isFullscreen;

    private static final int VIDEO_LOADER_ID = 1;

    private YTAdapter mAdapter;

    private TextView mEmptyStateTextView;
    EditText search;
    ListView videoListView;
    ProgressBar loadingIndicator;
    LoaderManager.LoaderCallbacks<List<YTVideo>> myCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_main);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        videoFragment = (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
        videoBox = findViewById(R.id.video_box);
        closeButton = findViewById(R.id.close_button);
        videoListView = (ListView) findViewById(R.id.videoList);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listLayout = findViewById(R.id.list_lay);


        videoBox.setVisibility(View.INVISIBLE);
        layout();

        myCallbacks = new LoaderManager.LoaderCallbacks<List<YTVideo>>() {
            @Override
            public Loader<List<YTVideo>> onCreateLoader(int id, Bundle args) {
                loadingIndicator.setVisibility(View.VISIBLE);
                return new YTLoader(getBaseContext(), REQUEST_URL);
            }

            @Override
            public void onLoadFinished(Loader<List<YTVideo>> loader, List<YTVideo> books) {
                loadingIndicator.setVisibility(View.GONE);

                mAdapter.clear();

                if (books != null && !books.isEmpty()) {
                    mEmptyStateTextView.setVisibility(View.GONE);
                    mAdapter.addAll(books);
                }else {
                    mEmptyStateTextView.setText(R.string.no_videos);
                    videoListView.setEmptyView(mEmptyStateTextView);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<YTVideo>> loader) {
                loadingIndicator.setVisibility(View.GONE);
                mAdapter.clear();
            }
        };

        loadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        search = (EditText)findViewById(R.id.etSearch);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            private Timer timer = new Timer();
            private final long DELAY = 1000;

            @Override
            public void afterTextChanged(final Editable s) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (s.toString().isEmpty()){
                                            mAdapter.clear();
                                            mEmptyStateTextView.setText(R.string.yt_searching);
                                            videoListView.setEmptyView(mEmptyStateTextView);
                                        }else {
                                            String searchQuery = wordsCounter(s.toString());
                                            REQUEST_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=25&type=video&order=viewCount&key=AIzaSyD9ifawjJ9kor2K67gO2gHbwhH-XnxvyAs&q=" + searchQuery;
                                            mAdapter = new YTAdapter(getBaseContext(), new ArrayList<YTVideo>());

                                            videoListView.setAdapter(mAdapter);

                                            videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    YTVideo video = mAdapter.getItem(position);

                                                    VideoFragment videoFragment =
                                                            (VideoFragment) getFragmentManager().findFragmentById(R.id.video_fragment_container);
                                                    videoFragment.setVideoId(video.getmID());

                                                    if (videoBox.getVisibility() != View.VISIBLE) {
                                                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                                            videoBox.setTranslationY(videoBox.getHeight());
                                                        }
                                                        videoBox.setVisibility(View.VISIBLE);
                                                    }

                                                    if (videoBox.getTranslationY() > 0) {
                                                        videoBox.animate().translationY(0).setDuration(ANIMATION_DURATION_MILLIS);
                                                    }
                                                }
                                            });

                                            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                                            NetworkInfo networkInfo = null;
                                            if (connMgr != null) {
                                                networkInfo = connMgr.getActiveNetworkInfo();
                                            }

                                            if (networkInfo != null && networkInfo.isConnected()) {
                                                mEmptyStateTextView.setVisibility(View.GONE);
                                                LoaderManager loaderManager = getLoaderManager();
                                                loaderManager.restartLoader(VIDEO_LOADER_ID, null, myCallbacks);
                                            } else {
                                                loadingIndicator.setVisibility(View.GONE);

                                                mEmptyStateTextView.setText(R.string.no_internet_connection);
                                                videoListView.setEmptyView(mEmptyStateTextView);
                                            }
                                        }
                                    }
                                });
                            }
                        },
                        DELAY
                );
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        layout();
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;

        layout();
    }

    private int portState;
    private boolean oneTime = false;

    private void layout() {
        boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        listLayout.setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
        //videoListView.setLabelVisibility(isPortrait);
        closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        if (isFullscreen) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            videoBox.setTranslationY(0);
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.START);
        } else if (isPortrait) {
            View decorView = getWindow().getDecorView();
            if (!oneTime){
                portState = decorView.getSystemUiVisibility();
                oneTime = true;
            }
            decorView.setSystemUiVisibility(portState);

            setLayoutSize(listLayout, MATCH_PARENT, MATCH_PARENT);
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        } else {
            videoBox.setTranslationY(0);
            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
            setLayoutSize(listLayout, screenWidth / 4, MATCH_PARENT);
            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
            setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
                    Gravity.END | Gravity.CENTER_VERTICAL);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private static void setLayoutSize(View view, int width, int height) {
        LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }

    public void onClickClose(@SuppressWarnings("unused") View view) {
        videoFragment.pause();
        ViewPropertyAnimator animator = videoBox.animate()
                .translationYBy(videoBox.getHeight())
                .setDuration(ANIMATION_DURATION_MILLIS);
        runOnAnimationEnd(animator, new Runnable() {
            @Override
            public void run() {
                videoBox.setVisibility(View.INVISIBLE);
            }
        });
    }

    @TargetApi(16)
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }

    public String wordsCounter(String input)
    {
        String word = "";
        ArrayList<String> words = new ArrayList<>();
        int i = 0;

        for (char item : input.toCharArray()) {
            i++;
            if (!Character.isWhitespace(item)) {
                word += item;
                if (i == input.length())
                {
                    words.add(word);
                }
            }
            else {
                if (!word.isEmpty())
                {
                    words.add(word);
                }
                word = "";
            }
        }
        return TextUtils.join("+", words);
    }

    public static final class VideoFragment extends YouTubePlayerFragment
            implements OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;

        public static VideoFragment newInstance() {
            return new VideoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            initialize("AIzaSyD9ifawjJ9kor2K67gO2gHbwhH-XnxvyAs", this);
        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
        }

        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    player.cueVideo(videoId);
                }
            }
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
            player.setOnFullscreenListener((YouTubeActivity) getActivity());
            if (!restored && videoId != null) {
                player.cueVideo(videoId);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            this.player = null;
        }

    }
}