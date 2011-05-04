/**
 * Copyright (C) 2011 by Jeffrey Sambells / We-Crate Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.intent.tests;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the demo layout.
        setContentView(R.layout.main);

        LinearLayout root = (LinearLayout) this.findViewById(R.id.root);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);

        final Intent i = new Intent("com.lozzal.intent.action.SUGGESTIONS");
        i.putExtra("LOCATION", "CN Tower");
        i.putExtra("THEME", "I am hungry");

        // Check if the intent has a receiver.
        PackageManager pm = getPackageManager();
        List<ResolveInfo> a = pm.queryIntentActivities(i, 0);
        final boolean receiverExists = a.size() > 0;

        TextView hello = (TextView) findViewById(R.id.message);

        if (receiverExists) {
            // Generic intents.
            hello.setText(R.string.haslozzal);
            createLocalActivitySuggestionIntent(root, lp);
            createWhatNextIntent(root, lp);

            // Lozzal targeted intents.
            createLozzalSuggestionsIntent(root, lp);
            createLozzalDeckIntent(root, lp);
            createLozzalItineraryIntent(root, lp);
        } else {

            // Add the button
            hello.setText(R.string.nolozzal);
            Button iButton = new Button(this);
            iButton.setLayoutParams(lp);
            iButton.setText("Download Latests Dev Preview");
            iButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("http://lozzal.com/files/android/Lozzal-latest.apk")));
                }
            });
            root.addView(iButton);
        }
    }
    
    
    // Generic intents

    static int SUGGESTIONS_RESULT = 1;

    void createLocalActivitySuggestionIntent(LinearLayout root, LinearLayout.LayoutParams lp) {

        // Create the intent.
        final Intent i = new Intent("com.android_intent.intent.action.LOCAL_ACTIVITY_SUGGESTIONS");

        // Set the appropriate data uri.
        i.setData(Uri.parse("suggestions://43496052,-80548260/I+am+hungry/"));

        // Check if the intent has a receiver.
        PackageManager pm = getPackageManager();
        List<ResolveInfo> a = pm.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        boolean receiverExists = a.size() > 0;

        // Add the button
        Button iButton = new Button(this);
        iButton.setLayoutParams(lp);
        iButton.setEnabled(receiverExists);
        iButton.setText("Generic Suggestions Intent");
        iButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(i, SUGGESTIONS_RESULT);
            }
        });
        root.addView(iButton);

    }

    static int WHAT_NEXT_RESULT = 2;

    void createWhatNextIntent(LinearLayout root, LinearLayout.LayoutParams lp) {

        // Create the intent.
        final Intent i = new Intent("com.android_intent.intent.action.WHAT_NEXT");

        // Set the appropriate data uri.
        i.setData(Uri.parse("next://43496052,-80548260/"));

        // Check if the intent has a receiver.
        PackageManager pm = getPackageManager();
        List<ResolveInfo> a = pm.queryIntentActivities(i, 0);
        boolean receiverExists = a.size() > 0;

        // Add the button
        Button iButton = new Button(this);
        iButton.setLayoutParams(lp);
        iButton.setEnabled(receiverExists);
        iButton.setText("Generic What Next Intent");
        iButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(i, WHAT_NEXT_RESULT);
            }
        });
        root.addView(iButton);

    }

    // Dealing with intent response
    protected void onActivityResult(int result, int code, Intent i) {

        Log.d("Intents", "result: " + result + " code: " + code);

        // Suggestion result.
        if (code == Activity.RESULT_OK && result == SUGGESTIONS_RESULT) {
            Bundle extras = i.getExtras();
            ArrayList<String> l = extras.getStringArrayList("SUGGESTIONS");
            Log.d("Intents", l.toString());

            String[] items = new String[l.size()];
            for (int c = 0; c < l.size(); c++) {
                items[c] = l.get(c);
            }

            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle("Suggestion")
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }

        // What Next result.
        if (code == Activity.RESULT_OK && result == WHAT_NEXT_RESULT) {
            Bundle extras = i.getExtras();
            String suggestion = extras.getString("SUGGESTION");

            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle("Suggestion")
                    .setMessage(suggestion).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    }).create().show();

            // Do something with the suggestion...

        }
    }

    // Targeted intents.

    void createLozzalSuggestionsIntent(LinearLayout root, LinearLayout.LayoutParams lp) {

        final Intent i = new Intent("com.lozzal.intent.action.SUGGESTIONS");
        i.putExtra("LOCATION", "CN Tower");
        i.putExtra("THEME", "I am hungry");

        // Check if the intent has a receiver.
        PackageManager pm = getPackageManager();
        List<ResolveInfo> a = pm.queryIntentActivities(i, 0);
        final boolean receiverExists = a.size() > 0;

        // Add the button.
        Button iButton = new Button(this);
        iButton.setLayoutParams(lp);
        iButton.setText("Lozzal Suggestions (CN Tower, I'm hungry)");
        iButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiverExists) {
                    // Start the activity using the Lozzal app.
                    startActivity(i);
                } else {
                    // Start the activity using the browser app.
                    try {
                        String theme = URLEncoder.encode(i.getStringExtra("THEME"), "UTF-8");
                        String location = URLEncoder.encode(i.getStringExtra("LOCATION"), "UTF-8");
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
                                .parse("http://lozzal.com/suggestions/" + location + "/" + theme));
                        startActivity(browserIntent);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        root.addView(iButton);

    }

    void createLozzalDeckIntent(LinearLayout root, LinearLayout.LayoutParams lp) {

        final Intent i = new Intent("com.lozzal.intent.action.DECK");
        i.putExtra("DECK_ID", "6bM29");
        i.putExtra("CARD", "2");

        // Check if the intent has a receiver.
        PackageManager pm = getPackageManager();
        List<ResolveInfo> a = pm.queryIntentActivities(i, 0);
        final boolean receiverExists = a.size() > 0;

        // Add the button.
        Button iButton = new Button(this);
        iButton.setLayoutParams(lp);
        iButton.setText("Deck (6bM29, 2)");
        iButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiverExists) {
                    // Start the activity using the Lozzal app.
                    startActivity(i);
                } else {
                    // Start the activity using the browser app.
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://lozzal.com/"
                            + i.getStringExtra("DECK_ID") + "/" + i.getStringExtra("CARD")));
                    startActivity(browserIntent);
                }
            }
        });
        root.addView(iButton);

    }

    void createLozzalItineraryIntent(LinearLayout root, LinearLayout.LayoutParams lp) {

        final Intent i = new Intent("com.lozzal.intent.action.ITINERARY");
        i.putExtra("DECK_ID", "6bM29");
        i.putExtra("CARD", "2");

        // Check if the intent has a receiver.
        PackageManager pm = getPackageManager();
        List<ResolveInfo> a = pm.queryIntentActivities(i, 0);
        final boolean receiverExists = a.size() > 0;

        // Add the button.
        Button iButton = new Button(this);
        iButton.setLayoutParams(lp);
        iButton.setText("Itinerary (6bM29, 2)");
        iButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiverExists) {
                    // Start the activity using the Lozzal app.
                    startActivity(i);
                } else {
                    // Start the activity using the browser app.
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
                            .parse("http://lozzal.com/details/itinerary/" + i.getStringExtra("DECK_ID") + "/"
                                    + i.getStringExtra("CARD")));
                    startActivity(browserIntent);
                }
            }
        });
        root.addView(iButton);

    }

}
