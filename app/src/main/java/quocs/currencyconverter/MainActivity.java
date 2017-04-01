package quocs.currencyconverter;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    public String[] lstLabels = {
            "US Dollar ($) ", "Euro (\u20AC)", "British Pound (\u00a3)", "Japanese Yen (\u00A5)",
            "Chinese  Yuan (CNY)", "Lao Kip (Lao Kip)", "Cambodia Riel (KHR)", "Vietnamese dong (\u0111)"};

    public String[] lstValue = {
            "USD", "EUR", "GBP", "JPY",
            "CNY", "LAK", "KHR", "VND"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.activity_main);


        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerTo);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lstLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(7);
    }

    public void onClick(View view) {
        if (!isNetworkConnected()) {
            DialogFragment newFragment = new NoInternetDialog();
            newFragment.show(getSupportFragmentManager(), "missiles");

//            Context context = newFragment.getContext();
//
//            Button buttonOK  = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
//            buttonOK.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        } else {

            String str = ((EditText) findViewById(R.id.editTextValue)).getText().toString();
            float num = Float.parseFloat(str);

            Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFrom);
            Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerTo);


            new connectWebService(lstValue[spinnerFrom.getSelectedItemPosition()], lstValue[spinnerTo.getSelectedItemPosition()],
                    num, this).execute();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
