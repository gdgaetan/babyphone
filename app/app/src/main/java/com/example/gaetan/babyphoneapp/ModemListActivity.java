package com.example.gaetan.babyphoneapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;

import com.example.gaetan.babyphoneapp.api.ApiBluetoothDevice;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gaetan on 04/04/2018.
 */

public class ModemListActivity extends ListActivity {
    private PopupWindow mPopupWindow;

    private String m_text = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String modems = (String) getIntent().getSerializableExtra("modems");
        List<String> modemList = Arrays.asList(modems.split(";"));
        ArrayAdapter<String> modemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, modemList);
        setListAdapter(modemAdapter);

        ListView listView = getListView();
        listView.setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    String modemName = adapterView.getItemAtPosition(i).toString();
                    Log.e("rether", modemName);
                    showPasswordPrompt(modemName);
                }

        );


    }

    protected void showPasswordPrompt(String modemName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(modemName);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expe0cted; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_text = input.getText().toString();
                ApiBluetoothDevice.getdevice().sendAsciiMessage("c;"+modemName+";"+m_text);
1 !           }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

}
