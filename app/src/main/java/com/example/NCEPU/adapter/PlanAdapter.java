/**
 * Date   : 2021/2/1 13:06
 * Author : KI
 * File   : PlanAdapter
 * Desc   : TeachingPlan's adapter
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.NCEPU.R;
import com.example.NCEPU.Utils.Plan;
import com.example.NCEPU.Utils.MyGridView;

import java.util.ArrayList;
import java.util.List;




public class PlanAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Plan> list;

    public PlanAdapter(Context context, List<Plan> list) {
        this.context = context;
        this.list = list;

    }


    @Override
    //获取分组个数
    public int getGroupCount() {
        return list.size();
    }

    @Override
    //分组中子选项个数为1
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    //获取指定分组数据
    public Object getGroup(int i) {
        return list.get(i);
    }

    @Override
    //获取指定子选项数据
    public Object getChild(int i, int j) {
        return null;
    }

    @Override
    //获取指定分组的id
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        GroupHolder groupHolder;
        if (convertView == null) {
            //父布局
            view = View.inflate(context, R.layout.group_plan, null);
            groupHolder = new GroupHolder();
            groupHolder.tv_nature_plan = view.findViewById(R.id.tv_nature_plan);
            view.setTag(groupHolder);
        } else {
            view = convertView;
            groupHolder = (GroupHolder) view.getTag();
        }
        String text = list.get(i).getTag();
        text += "  最低要求学分:" + list.get(i).getMinCredit();
        text += "  当前学分:" + list.get(i).getCurrentCredit();
        groupHolder.tv_nature_plan.setText(text);
        return view;
    }


    //取得显示给定分组给定子位置的数据用的视图
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        View view;
        ChildHolder childHolder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.child_plan, null);

            //指定Item的宽高
//            DisplayMetrics dm = new DisplayMetrics();
//            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
//            int height = dm.heightPixels ;//高度
//            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2500));

            childHolder = new ChildHolder();
            childHolder.childGridView = view.findViewById(R.id.gv_vision_plan);
            view.setTag(childHolder);

        } else {
            view = convertView;
            childHolder = (ChildHolder) view.getTag();
        }
//        final QuestionSetInfo item = itemList.get(position);
//        //界面刷新与设置监听器......
//        fb.display(holder.imageView, item.getCoverMapUrl(), bitmap, bitmap);
//        holder.imageView.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(context,ExamEnterActivity.class);
//                intent.putExtra("bean", item);
//                context.startActivity(intent);
//            }
//        });

        // 初始化gridView
        ArrayList<String> data = new ArrayList<>();
//        data.add("课程号");
//        data.add("课程名称");
//        data.add("课程性质");
//        data.add("学分");
//        data.add("学年");
//        data.add("学期");
        for(int k = 0; k < list.get(i).getPlans().size(); k++) {
       //     data.add(list.get(i).getPlans().get(k).getCourse_num());
            data.add(list.get(i).getPlans().get(k).getCourse_name());
//            data.add(list.get(i).getPlans().get(k).getCourse_nature());
            data.add(list.get(i).getPlans().get(k).getCredit());
            data.add(list.get(i).getPlans().get(k).getYear());
            data.add(list.get(i).getPlans().get(k).getSemester());
        }
        System.out.println("哈哈");
        System.out.println(list.get(i).getPlans().size()); //23
        System.out.println(data.size());  //24*6=144
        String[] array = data.toArray(new String[data.size()]);
        GridViewPlanAdapter gridViewPlanAdapter = new GridViewPlanAdapter(context, array);
        childHolder.childGridView.setAdapter(gridViewPlanAdapter);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    static class GroupHolder {
        TextView tv_nature_plan;
    }

    static class ChildHolder {
        MyGridView childGridView;
    }
}


