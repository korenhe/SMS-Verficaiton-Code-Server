package cn.forgiveher.smscoder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.forgiveher.model.Host;

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.MyViewHolder> {

    private List<Host> hostList;
    private OnItemClickListener listener;

    public HostAdapter(List<Host> hostList, OnItemClickListener listener) {
        this.hostList = hostList;
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, ip;

        public MyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.name);
            ip = (TextView) view.findViewById(R.id.ip);
        }

        public void bind(final Host item, final OnItemClickListener listener) {

            name.setText(item.getName());
            ip.setText(item.getIp());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listener.onItemClick(item);

                }

            });
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_list_row, parent, false);
        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(hostList.get(position), listener);


    }

    @Override
    public int getItemCount() {
        return hostList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Host book);

    }


}