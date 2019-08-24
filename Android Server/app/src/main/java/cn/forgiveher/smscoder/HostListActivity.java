package cn.forgiveher.smscoder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import cn.forgiveher.model.Host;


public class HostListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private List<Host> list = new ArrayList<>();
    private Context context;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            // Handle the camera action
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initData() {


        // Database
        LitePal.initialize(this);

        /*
        for (int i = 0; i < 5; i++) {
            Host host = new Host();
            host.setName("host" + i);
            host.setIP("127.0.0." + i);
            host.save();
        }
*/
        list = LitePal.findAll(Host.class);

    }


}
