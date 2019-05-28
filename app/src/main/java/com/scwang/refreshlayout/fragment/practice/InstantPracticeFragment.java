package com.scwang.refreshlayout.fragment.practice;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;
import com.scwang.refreshlayout.util.DynamicTimeFormat;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * 即时聊天页面
 * A simple {@link Fragment} subclass.
 */
public class InstantPracticeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_practice_instant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        final Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });


        final MessageAdapter adapter = new MessageAdapter(initData());

        ClassicsFooter footer = root.findViewById(R.id.footer);
        View arrow = footer.findViewById(ClassicsFooter.ID_IMAGE_ARROW);
        arrow.setScaleY(-1);//必须设置

        RecyclerView listView = root.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setScaleY(-1);//必须设置

        final RefreshLayout refreshLayout = root.findViewById(R.id.refreshLayout);
        refreshLayout.setEnableRefresh(false);//必须关闭
        refreshLayout.setEnableAutoLoadMore(true);//必须关闭
        refreshLayout.setEnableNestedScroll(false);//必须关闭
        refreshLayout.setEnableScrollContentWhenLoaded(true);//必须关闭
        refreshLayout.getLayout().setScaleY(-1);//必须设置
        refreshLayout.setScrollBoundaryDecider(new ScrollBoundaryDeciderAdapter() {
            @Override
            public boolean canLoadMore(View content) {
                return super.canRefresh(content);//必须替换
            }
        });

        //监听加载，而不是监听 刷新
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.insert(initData());
                        refreshLayout.finishLoadMore();
                    }
                }, 2000);
            }
        });

        /*
         * 触发测试
         */
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoLoadMore();
            }
        });
    }

    private Collection<Message> initData() {
        final User user = new User(){{
            avatarId = R.mipmap.image_avatar_1;
        }};
        final User mine = new User(){{
            isMe = true;
            avatarId = R.mipmap.image_avatar_4;
        }};
        return Arrays.asList(
                new Message(){{
                    this.Time = new Date(System.currentTimeMillis() - 3600 * 1000 * 3);
                    this.Message = "对方测回了一条消息";
                }},
                new Message(){{
                    this.User = user;
                    this.Time = new Date(System.currentTimeMillis() - 3600 * 1000 * 3 + 5000);
                    this.Message = "刚刚发错了，不好意思，下面这个才是";
                }},
                new Message(){{
                    this.User = user;
                    this.Time = new Date(System.currentTimeMillis() - 3600 * 1000 * 3 + 10000);
                    this.Image = R.mipmap.image_avatar_3;
                }},
                new Message(){{
                    this.User = mine;
                    this.Time = new Date(System.currentTimeMillis() - 3600 * 1000 * 3 + 15000);
                    this.Message = "好的，收到了";
                }},
                new Message(){{
                    this.User = mine;
                    this.Time = new Date(System.currentTimeMillis() - 3600 * 1000 * 3 + 15000 * 50);
                    this.Message = "一会你来我办公室一趟";
                }},
                new Message(){{
                    this.User = user;
                    this.Time = new Date(System.currentTimeMillis() - 3600 * 1000 * 3 + 15000 * 50 + 5000);
                    this.Message = "好的，马上到。";
                }}
        );
    }

    static class User {
        int avatarId;
        boolean isMe;
    }

    static class Message {
        Date Time;
        User User;
        int Image;
        String Message;

    }

    static class MessageAdapter extends BaseRecyclerAdapter<Message> {

        DynamicTimeFormat format = new DynamicTimeFormat();

        MessageAdapter(Collection<Message> collection) {
            super(collection, R.layout.item_practice_instant);
        }

        @Override
        protected void onBindViewHolder(SmartViewHolder holder, Message message, int index) {
            onItemBindingTime(holder, message, index);
            if (message.User == null) {
                onItemBindingSystem(holder, message);
            } else if (message.User.isMe) {
                onItemBindingMine(holder, message);
            } else {
                onItemBindingOther(holder, message);
            }
        }

        /**
         * 展示对方的消息
         */
        private void onItemBindingOther(SmartViewHolder holder, Message message) {
            holder.gone(R.id.chatting_right);
            holder.gone(R.id.chatting_tv_sysmsg);
            holder.visible(R.id.chatting_left);

            holder.image(R.id.chatting_liv_avatar, message.User.avatarId);

            if (message.Image == 0) {
                holder.gone(R.id.chatting_liv_img);
                holder.text(R.id.chatting_ltv_txt, message.Message).visible(R.id.chatting_ltv_txt);
            } else {
                holder.gone(R.id.chatting_ltv_txt);
                holder.image(R.id.chatting_liv_img, message.Image).visible(R.id.chatting_liv_img);
            }
        }


        /**
         * 展示自己的消息
         */
        private void onItemBindingMine(SmartViewHolder holder, Message message) {
            holder.gone(R.id.chatting_left);
            holder.gone(R.id.chatting_tv_sysmsg);
            holder.visible(R.id.chatting_right);

            holder.image(R.id.chatting_riv_avatar, message.User.avatarId);

            if (message.Image == 0) {
                holder.gone(R.id.chatting_riv_img);
                holder.text(R.id.chatting_rtv_txt, message.Message).visible(R.id.chatting_rtv_txt);
            } else {
                holder.gone(R.id.chatting_rtv_txt);
                holder.image(R.id.chatting_riv_img, message.Image).visible(R.id.chatting_riv_img);
            }
        }

        /**
         * 展示系统消息
         */
        private void onItemBindingSystem(SmartViewHolder holder, Message message) {
            holder.gone(R.id.chatting_left);
            holder.gone(R.id.chatting_right);
            holder.gone(R.id.chatting_tv_sendtime);
            holder.visible(R.id.chatting_tv_sysmsg).text(R.id.chatting_tv_sysmsg, message.Message);
        }


        /**
         * 展示时间
         */
        private void onItemBindingTime(SmartViewHolder holder, Message model, int index) {
            Message prev = null;
            if (index > 0) {
                prev = get(index - 1);
                if (prev.User == null) {
                    if (index > 1) {
                        prev = get(index - 2);
                    } else {
                        prev = null;
                    }
                }
            }
            if (prev != null && (model.Time.getTime() - prev.Time.getTime() < 5 * 60 * 1000)) {
                holder.gone(R.id.chatting_tv_sendtime);
            } else {
                holder.visible(R.id.chatting_tv_sendtime);
                holder.text(R.id.chatting_tv_sendtime, format.format(model.Time));
            }
        }

    }
}
