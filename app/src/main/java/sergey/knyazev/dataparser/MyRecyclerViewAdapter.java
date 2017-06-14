package sergey.knyazev.dataparser;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Sergey on 14.06.2017.
 */


public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter.DataObjectHolder> {

    private ArrayList<DataObject> mDataset;
    private static MyClickListener myClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mTitleView;
        TextView mDateView;

        public DataObjectHolder(View itemView) {
            super(itemView);
            mTitleView = (TextView) itemView.findViewById(R.id.title);
            mDateView = (TextView) itemView.findViewById(R.id.date);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            myClickListener.onItemLongClick(getPosition(), v);
            return true;
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
    }

    public void updateData(ArrayList<DataObject> myDataset) {
        mDataset = myDataset;
        this.notifyDataSetChanged();
    }

    public void addItem(DataObject item) {
        mDataset.add(item);
        this.notifyDataSetChanged();
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        holder.mTitleView.setText(mDataset.get(position).getTitle());
        //holder.description.setText(mDataset.get(position).getLink());
        holder.mDateView.setText(mDataset.get(position).getDate());
    }

    public void addItem(DataObject dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * Возвращает количество элементов
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
        public void onItemLongClick(int position, View v);
    }
}