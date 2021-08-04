package com.example.baojing;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {
    Context mContext;
    private List<String> mData;
    private List<EditText> editTexts;
    private LayoutInflater mLayoutInflater;
    private EditText lastSelect = null;
    private String TAG = "ContactAdapter";
    int lastPos = -1;


    public ContactAdapter(Context context, List<String> data){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mData = data;
        editTexts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.contact_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mText = view.findViewById(R.id.contact_text);
        return viewHolder;
    }

    void setData(List<String> data){
        mData = data;
        this.notifyDataSetChanged();
    }

    void addAndSelect(){
        mData = getData();
        mData.add("happy~");
        this.notifyDataSetChanged();
    }

    void setEdit(){
        if(lastSelect != null){
            lastSelect.setInputType(InputType.TYPE_CLASS_PHONE);
            lastSelect.setSelection(lastSelect.getText().length());
//            lastSelect.setText("edit");
        }
    }

    void setFocusOnLast(){
        editTexts.get(mData.size()-1).callOnClick();
    }

    void setDelete(){
        if(lastPos != -1){
            mData = getData();
            mData.remove(lastPos);
            notifyDataSetChanged();
            lastPos = -1;
            lastSelect = null;
        }
    }

    List<String> getData(){
        for(int i= 0 ; i < mData.size(); i++){
            mData.set(i, editTexts.get(i).getText().toString());
        }
        return mData;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        holder.mText.setText(mData.get(position));
        holder.mText.setInputType(InputType.TYPE_NULL);
        holder.mText.setClickable(true);

        holder.mText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
//                    Log.d(TAG, "Clicked");
                    view.requestFocus();
                    return false;
                }
                if(lastSelect != null && lastSelect != holder.mText){
                    lastSelect.setInputType(InputType.TYPE_NULL);
                }
                lastSelect = holder.mText;
                lastPos = position;
                return true;
            }
        });

//        holder.mText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(lastSelect != null && lastSelect != holder.mText){
//                    lastSelect.setInputType(InputType.TYPE_NULL);
//                }
//                lastSelect = holder.mText;
//                lastPos = position;
//            }
//        });

        if(editTexts.size() <= position){
            editTexts.add(holder.mText);
        }
        else{
            editTexts.set(position, holder.mText);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        EditText mText;
    }
}
