package cn.forgiveher.smscoder;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.forgiveher.model.Host;

public class HostListActivity extends AppCompatActivity {
    private List<Host> list = new ArrayList<>();
    private Context context;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        // Database
        /*
        LitePal.initialize(this);


        Host myhost = new Host();
        myhost.setName("test1");
        myhost.setIP("127.0.0.1");
        myhost.save();

        List<Host> allhosts = LitePal.findAll(Host.class);
        */
        context = this;

        initData();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        HostAdapter adapter = new HostAdapter(list, new HostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Host host) {
                Toast.makeText(getApplicationContext(), host.getName()+" "+" host is Clicked", Toast.LENGTH_LONG).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        list.clear();
        for (int i = 50; i < 100; i++) {
            Host host = new Host();
            host.setName("host" + i);
            host.setIP(Integer.toString(i));
            list.add(host);
        }
    }


}
