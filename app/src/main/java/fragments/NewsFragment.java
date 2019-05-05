package fragments;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.teleg.programm.R;

import com.example.teleg.programm.serverSide.Api.ClientXml;
import com.example.teleg.programm.serverSide.Api.RsreuApi;
import com.example.teleg.programm.serverSide.News.ItemNews;
import com.example.teleg.programm.serverSide.News.RVAdapter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class NewsFragment extends Fragment {


    private Button button;
    private RecyclerView rv;
    private RsreuApi api = ClientXml.getInstance().getApi();

    public NewsFragment() {
    }

    public static NewsFragment newInstance() {
        return new NewsFragment();
    }

    static final String BASE_URL = "http://www.rsreu.ru/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_news_list, container, false);
        rv = (RecyclerView)view.findViewById(R.id.recyclerView);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            loadNews();
        }

        return view;
    }

    public void loadNews() {
        api.getItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feed -> {
                    List<ItemNews> news = feed.getChannel().getNewsList();
                    RVAdapter adapter = new RVAdapter(news);
                    LinearLayoutManager manager = new LinearLayoutManager(getContext());
                    rv.setLayoutManager(manager);
                    rv.setAdapter(adapter);
                }, Throwable::printStackTrace);
    }
}