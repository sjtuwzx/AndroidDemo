package ctrip.android.hotel;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wzx.android.demo.pinnedHeader.ItemsAdapter;
import com.wzx.android.demo.v2.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang_zx on 2015/3/5.
 */
public class HotelInvoicePostDialog extends HotelCustomDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hotel_dialog_invoice_post, container, false);

        ListView listView = (ListView)view.findViewById(R.id.list);
        ItemsAdapter adapter = new ItemsAdapter(getActivity());
        listView.setAdapter(adapter);
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add("item[" + (i + 1) + "]");
        }
        adapter.setData(items);

        return view;
    }


    @Override
    public void setWindowContainerPadding(ViewGroup container) {
        container.setPadding(30, 100, 30, 100);
    }
}
