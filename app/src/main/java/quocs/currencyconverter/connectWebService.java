package quocs.currencyconverter;

/**
 * Created by tmquoc on 01/04/2017.
 */

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by tmquoc on 25/03/2017.
 */

public class connectWebService extends AsyncTask<Void, Void, String> {
    private String fromUnit, toUnit;
    private float value;
    MainActivity activity;
    boolean reversed;

    connectWebService(String fromUnit, String toUnit, float value, MainActivity activity) {
        this(fromUnit, toUnit, value, activity, false);
    }

    connectWebService(String fromUnit, String toUnit, float value, MainActivity activity, boolean reversed) {
        this.fromUnit = fromUnit;
        this.toUnit = toUnit;
        this.value = value;
        this.activity = activity;
        this.reversed = reversed;

        ((Button) activity.findViewById(R.id.buttonCheck)).setEnabled(false);

    }

    protected String doInBackground (Void ...Params) {
        try {
            return downloadUrl("http://www.webservicex.net/CurrencyConvertor.asmx/ConversionRate?FromCurrency="
                    + fromUnit
                    + "&ToCurrency="
                    + toUnit);
        }
        catch (IOException e) {
            Log.d("", "Caught IOException: " + e.getMessage());
        }
        return "";
    }

    protected void onPostExecute(String result) {
        Document document = getDomElement(result);
        String strResult = document.getDocumentElement().getChildNodes().item(0).getNodeValue();
        float  fltResult = Float.parseFloat(strResult);
        if (fltResult == 0) {
            new connectWebService(toUnit, fromUnit,value, activity, true).execute();
            return;
        }
        if (!reversed) {
            fltResult = fltResult * value;
        }
        else {
            fltResult = value / fltResult;
        }

        ((TextView) activity.findViewById(R.id.textViewResult)).setText(String.format("%.2f", fltResult));
        ((Button) activity.findViewById(R.id.buttonCheck)).setEnabled(true);

    }



    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private String downloadUrl(String input) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            URL url = new URL (input);
            connection = (HttpURLConnection) url.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
            br.close();


        }
        catch (Exception e) {
            Log.d("TMQ" ,"Caught IOException: " + e.getMessage());
        }
        finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        Log.d("Ket qua", result);
        return result;

    }

    public Document getDomElement(String xml) {
        Document document = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();

            is.setCharacterStream(new StringReader(xml));
            document = db.parse(is);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

}
