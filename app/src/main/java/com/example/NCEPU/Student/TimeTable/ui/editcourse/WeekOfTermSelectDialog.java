package com.example.NCEPU.Student.TimeTable.ui.editcourse;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.NCEPU.R;
import com.example.NCEPU.Student.TimeTable.util.Config;

import java.util.ArrayList;
import java.util.List;


public class WeekOfTermSelectDialog extends Dialog {
    private List<Boolean> list = new ArrayList<>(Config.getMaxWeekNum());
    private int mWeekOfTerm;
    private Context mContext;
    private DialogAdapter dialogAdapter;
    private CheckBox selectAll;
    private CheckBox singleWeek;
    private CheckBox doubleWeek;
    private View.OnClickListener[] listeners = new View.OnClickListener[2];


    public WeekOfTermSelectDialog(@NonNull Context context, int weekOfTerm) {
        super(context,R.style.CustomDialog);
        this.mWeekOfTerm = weekOfTerm;
        this.mContext = context;
    }

    /**
     * 设置dialog居下占满屏幕
     */
    private void changeDialogStyle() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;
                window.setAttributes(attr);
            }
        }
    }

    public int getWeekOfTerm() {
        int weekOfTerm = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i)) {
                //Log.d("weekofterm",String.valueOf(i));
                weekOfTerm++;
            }
            if(i!=len-1){//最后不移动
                weekOfTerm=weekOfTerm<<1;
            }
        }
        return weekOfTerm;
    }

    public static int getWeekOfTerm_(List<Boolean> list) {
        int weekOfTerm = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i)) {
                //Log.d("weekofterm",String.valueOf(i));
                weekOfTerm++;
            }
            if(i!=len-1){//最后不移动
                weekOfTerm=weekOfTerm<<1;
            }
        }
        return weekOfTerm;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_week_of_term);
        init();
        RecyclerView recyclerView = findViewById(R.id.rv_week_of_term);
        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(mContext, 5);
        recyclerView.setLayoutManager(gridLayoutManager);

        dialogAdapter = new DialogAdapter();
        recyclerView.setAdapter(dialogAdapter);
        selectAll = findViewById(R.id.check_box_select_all);
        //全选
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = selectAll.isChecked();
                if (b) {
                    singleWeek.setChecked(false);
                    doubleWeek.setChecked(false);
                }
                for (int i = 0, len = list.size(); i < len; i++) {
                    dialogAdapter.checkBoxList.get(i).setChecked(b);
                }
            }
        });
        singleWeek = findViewById(R.id.check_box_single_week);
        singleWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = singleWeek.isChecked();
                if (b) {
                    selectAll.setChecked(false);
                    doubleWeek.setChecked(false);
                    for (int i = 0, len = list.size(); i < len; i++) {
                        dialogAdapter.checkBoxList.get(i).setChecked((i + 1) % 2 == 1);
                    }
                }

            }
        });
        doubleWeek = findViewById(R.id.check_box_double_week);
        doubleWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = doubleWeek.isChecked();
                if (b) {
                    singleWeek.setChecked(false);
                    selectAll.setChecked(false);
                    for (int i = 0, len = list.size(); i < len; i++) {
                        dialogAdapter.checkBoxList.get(i).setChecked((i + 1) % 2 == 0);
                    }
                }

            }
        });
        Button cancelBtn = findViewById(R.id.btn_cancel);
        Button yesBtn = findViewById(R.id.btn_yes);
        cancelBtn.setOnClickListener(listeners[1]);
        yesBtn.setOnClickListener(listeners[0]);

        changeDialogStyle();
    }

    public void setPositiveBtn(View.OnClickListener listener) {
        listeners[0] = listener;
    }

    public void setNativeBtn(View.OnClickListener listener) {
        listeners[1] = listener;
    }

    private void init() {
        if (mWeekOfTerm == -1) {
            for (int i = 0, len = Config.getMaxWeekNum(); i < len; i++) {
                list.add(false);
            }
        } else {
            for (int i = Config.getMaxWeekNum() - 1; i >= 0; i--) {
                list.add(((mWeekOfTerm >> i) & 0x01) == 1);
            }
        }

    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {
        public List<CheckBox> checkBoxList = new ArrayList<>();

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_of_term_checkbox, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            checkBoxList.add(holder.checkBox);
            holder.checkBox.setChecked(list.get(position));
            holder.checkBox.setText(String.valueOf(position + 1));
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.d("checkbox", "发生改变");
                    list.set(position, b);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private CheckBox checkBox;

            public ViewHolder(View view) {
                super(view);
                checkBox = view.findViewById(R.id.checkBox);
            }

        }
    }
}
