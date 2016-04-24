/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qiu.tool.windword.chart;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import qiu.tool.windword.Util;
import qiu.tool.windword.WordLibAdapter;



import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Sales demo bar chart.
 */
public class SalesStackedBarChart extends AbstractDemoChart {
    /**
     * Returns the chart name.
     *
     * @return the chart name
     */

    private final long oneDay = 24 * 60 * 60 * 1000;

    public String getName() {
        return "Sales stacked bar chart";
    }

    /**
     * Returns the chart description.
     *
     * @return the chart description
     */
    public String getDesc() {
        return "The monthly sales for the last 2 years (stacked bar chart)";
    }

    /**
     * Executes the chart demo.
     *
     * @param context the context
     * @return the built intent this is the original function
     */
    public Intent execute0(Context context) {
        String[] titles = new String[] {
                "2008", "2007"
        };
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[] {
                14230, 12300, 14240, 15244, 15900, 19200, 22030, 21200, 19500, 15500, 12600, 14000
        });
        values.add(new double[] {
                5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500, 11600, 13500
        });
        int[] colors = new int[] {
                Color.BLUE, Color.CYAN
        };
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "Word list to check", "Month", "Units sold", 0.5, 12.5, 0,
                24000, Color.GRAY, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
        renderer.setXLabels(12);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setPanEnabled(true, false);
        // renderer.setZoomEnabled(false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
                Type.STACKED);
    }

    public Intent execute3(Context context) {
        String[] titles = new String[] {
                "2008", "2007"
        };
        List<double[]> values = new ArrayList<double[]>();
        values.add(new double[] {
                14230, 12300, 14240, 15244, 15900, 19200, 22030, 12300, 14240, 15244, 15900, 19200,
                22030, 21200, 19500, 15500, 12600, 14000, 14230, 12300, 14240, 15244, 15900, 19200,
                22030, 21200, 19500, 15500, 12600, 14000
        });
        values.add(new double[] {
                5230, 7300, 9240, 10540, 7900, 9200, 12030, 11200, 9500, 10500, 11600, 13500, 5230,
                7300, 9240, 10540, 7900, 9200, 12030, 7300, 9240, 10540, 7900, 9200, 12030, 11200,
                9500, 10500, 11600, 13500
        });
        int[] colors = new int[] {
                Color.BLUE, Color.CYAN
        };
        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "Word list to check", "Month", "Units sold", 0.5, 10, 0, 24000,
                Color.GRAY, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
        renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
        renderer.setXLabels(12);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setPanEnabled(true, true);
        // renderer.setZoomEnabled(false);
        renderer.setZoomRate(1.1f);
        renderer.setBarSpacing(0.5f);
        return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
                Type.STACKED);
    }

    private int getDayth(long time, long baseTime) {
        if (time < baseTime) {
            return 0;
        }
        return (int)((time - baseTime) / oneDay);
    }

    private void outArray(int span, double[] ary) {

        String logstr = "Span " + span + " ";
        for (double vl : ary) {
            logstr += "\t" + vl;
        }
        Util.log(logstr);
    }

    private double getMaxValue(double vals[], double base){

        for (double vl : vals) {
            if(vl > base){
                base = vl;
            }
        }
        return base;
    }

    public void test(Context context){
        WordLibAdapter lib = WordLibAdapter.getInstance(context);
        if (lib == null) {
            return ;
        }
        Cursor csr = lib.getAllTime();

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        long baseTime = now.getTimeInMillis();
        int dayCount = getDayth(lib.getLongestTime(), baseTime);
        Util.log("baseTime :" + baseTime);
        Util.log("Longest Time :" + lib.getLongestTime());
        Util.log("Longest dayth :" + dayCount);
        dayCount++;

        csr.moveToFirst();

        long lastTime = 0;

        while (!csr.isAfterLast()) {
            int thisSpan = csr.getInt(0);
            if (thisSpan < 0) {
                thisSpan = 0;
            }
            long thisTime = csr.getLong(1);
            if(lastTime>thisTime){
                Util.log("%%");
            }
            // Util.log("Span Time:" + thisSpan +","+ thisTime);
            int dth = getDayth(thisTime, baseTime);
            Util.log("test VAL: day " + dth + " span " +thisSpan + " time " +thisTime);
            csr.moveToNext();
            lastTime = thisTime;
        }
    }

    public Intent execute(Context context) {
        List<double[]> values = new ArrayList<double[]>();

        WordLibAdapter lib = WordLibAdapter.getInstance(context);
        if (lib == null) {
            return null;
        }
        Cursor csr = lib.getAllTime();

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        long baseTime = now.getTimeInMillis();
        int dayCount = getDayth(lib.getLongestTime(), baseTime);
        Util.log("baseTime :" + baseTime);
        Util.log("Longest Time :" + lib.getLongestTime());
        Util.log("Longest dayth :" + dayCount);
        dayCount++;
        int maxSpan = 0;
        if(csr.moveToLast()){
            maxSpan = csr.getInt(0);
        }

        for(int i= 0;i<= maxSpan;i++){
            Util.log("Span" + i + "is added.");
            values.add(new double[(int)dayCount]);
        }

        if(!csr.moveToFirst()){
            Util.log("no any content.");
            return null;
        }

        double vals[] = null;

        double maxValue = 0;

        int lastSpan = -999;// this is a impossible value.
        int thisSpan = 0;
        while (!csr.isAfterLast()) {
            thisSpan = csr.getInt(0);
            if (thisSpan < 0) {
                thisSpan = 0;
            }
            long thisTime = csr.getLong(1);
            int dth = getDayth(thisTime, baseTime);

//Util.log("VAL: day " + dth + " span " +thisSpan + " time " +thisTime);
            if (thisSpan != lastSpan) {
                if (vals != null) {
                    maxValue = getMaxValue(vals, maxValue);
                    //==================print begin================//
                    String info = "";
                    for(int i = 0;i<vals.length;i++){
                        info += "\t"+vals[i];
                    }
                    Util.log("Span "+ lastSpan + info);
                   //==================print end==================//
                }
                vals = values.get(thisSpan);
                lastSpan = thisSpan;
            }
            vals[dth] += 1.0;
            csr.moveToNext();
        }
        //==================print begin================//
        String info = "";
        for(int i = 0;i<vals.length;i++){
            info += "\t"+vals[i];
        }
        Util.log("Span "+ lastSpan + info);
       //==================print end==================//
        maxValue = getMaxValue(vals, maxValue);

        int max = (int)maxValue;
        max = (max+50)/50;
        max *= 50;

//=======================test code==========//'
//       String[] titles = new String[] {
//                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13"
//        };
//        int[] colors = new int[] {
//                Color.BLUE, Color.GREEN, Color.RED, 0xff00ffff, 0xffffff00, 0xff3366aa,0xffff00ff, 0xff00ddaa,
//                0xffddaa00, 0xff8800dd,  0xff00aa00, 0xff8800dd,  0xff00aa00, 0xff00ffff
//        };
//        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		//======================test end==============//
        XYMultipleSeriesRenderer renderer = buildBarRenderer(Util.getColors(maxSpan));
        setChartSettings(renderer, "Word list to check", "Month", "Units sold", 0.5f, 1.5f, 0, max,
                Color.GRAY, Color.LTGRAY);
        for (int i = 0; i <= maxSpan; i++) {
            renderer.getSeriesRendererAt(i).setDisplayChartValues(true);
            renderer.getSeriesRendererAt(i).setChartValuesTextSize(20);
        }
        renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
        //renderer.setXLabels(12);
        renderer.setYLabels(10);
        renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Align.LEFT);
        renderer.setPanEnabled(true, true);

        // renderer.setZoomEnabled(false);
        //renderer.setZoomRate(1.0f);
        renderer.setBarSpacing(0.2f);
//        return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
        return ChartFactory.getBarChartIntent(context, buildBarDataset(getTitles(maxSpan), values), renderer,
                Type.DEFAULT);
    }

    private String[] getTitles(int maxSpan) {
        String[] titles = new String[maxSpan + 1];
        for (int i = 0; i <= maxSpan; i++) {
            titles[i] = "" + i;
        }
        return titles;
    }


}
