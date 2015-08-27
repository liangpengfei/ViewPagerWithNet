package xhome.uestcfei.com.library;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ViewPagerWithNet可以很方便的定制要显示的内容
 * 自己的学习笔记
 * Email : luckyliangfei@gmail.com
 * Created by fei on 15/8/27.
 */
public class ViewPagerWithNet extends FrameLayout {

    private static final String TAG = "ViewPagerWithNet";
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private Context context;

    //可以自己加入你想用这个viewpager展示的任何的数据，都可以传递进来，并且在view中添加相应的罗杰处理就行了
    private String[] imageUrls;
    private String[] contents;

    private ViewPager viewPager;
    private List<View> dotViewsList;


    //实现轮播的功能，类似Timer
    private ScheduledExecutorService scheduledExecutorService;
    private int currentItem  = 0;

    public ViewPagerWithNet(Context context) {
        this(context, null);
    }

    public ViewPagerWithNet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerWithNet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        //初始化图片加载组件
        initImageLoader(context);
    }

    /**
     * 初始化相关Data，前提data可以通过重写下面的这个方法，也可以直接添加set方法
     */
    public void initData(String [] imageUrls,String [] contents) {
        this.imageUrls = imageUrls;
        this.contents = contents;
        dotViewsList = new ArrayList<>();
        initUI(context);
    }

    //开启轮播
    public void startPlay() {
        if (contents == null || imageUrls == null) {
            return;
        }
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 5, TimeUnit.SECONDS);
    }

    public void stopPlay() {
        scheduledExecutorService.shutdown();
    }

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }

    };

    /**
     *执行轮播图切换任务
     *
     */
    private class SlideShowTask implements Runnable{

        @Override
        public void run() {
            synchronized (viewPager) {
                currentItem = (currentItem+1)%imageUrls.length;
                handler.obtainMessage().sendToTarget();
            }
        }

    }

    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     *
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {
            currentItem = pos;
            for(int i=0;i < dotViewsList.size();i++){
                if(i == pos){
                    ((View)dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_focus);
                }else {
                    ((View)dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_blur);
                }
            }
        }

    }

    private void initUI(Context context) {
        //这个是这里的视图的逐步局
        View view = LayoutInflater.from(context).inflate(R.layout.viewpager_with_net, this,true);

        LinearLayout dotLayout = (LinearLayout)findViewById(R.id.dotLayout);

        //添加有下角的小红点
        for (int i = 0; i < imageUrls.length; i++) {
            ImageView dotView =  new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
            params.leftMargin = 4;
            params.rightMargin = 4;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }

        viewPager = (ViewPager) view.findViewById(R.id.view);

        viewPager.setAdapter(new MyPagerAdapter());

        viewPager.setOnPageChangeListener(new MyPageChangeListener());

    }


    /**
     * ImageLoader 图片组件初始化，做一些超时处理之类的设定
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove
                .build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * viewadapter的使用要注意，这个是有一点没有弄明白的
     * 一定要把这个新生成的view加入到container中去
     */

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view ==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.viewpager_item, null);
            TextView content = (TextView) view.findViewById(R.id.content);
            ImageView image = (ImageView) view.findViewById(R.id.imageview);
            content.setText(contents[position]);
            imageLoader.displayImage(imageUrls[position],image);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }


    }
}
