package com.scwang.refreshlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ListView;

import com.scwang.refreshlayout.dummy.DummyContent;
import com.scwang.refreshlayout.dummy.DummyContent.DummyItem;
import com.scwang.smartrefreshlayout.SmartRefreshLayout;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        View view = findViewById(R.id.list);
        if (view instanceof ListView) {
            ListView listView = (ListView) view;
            listView.setAdapter(new ItemRecyclerViewAdapter(DummyContent.ITEMS, new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(DummyItem item) {

                }
            }));
            if (view.getParent() instanceof SmartRefreshLayout) {
                ((SmartRefreshLayout) view.getParent()).setReboundInterpolator(new OvershootInterpolator());
            }
        }
//        MountanScenceView scenceView = (MountanScenceView) findViewById(R.id.flyrefresh);
//        SmartRefreshLayout refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshlayout);
//        refreshLayout.setRefreshHeader(scenceView);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
