package com.example.springroll.database;

/**
 * Created by SpringRoll on 4/23/2016.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Calendar;

import library.calendarAPI.DateTimePickerDialog;
import library.calendarAPI.WeekViewEvent;

/**
 * Created by SpringRoll on 3/30/2016.
 */
public class EventFragment extends Fragment implements DateTimePickerDialog.DateTimeListener{

    public static final String EXTRA_EVENT_ID = "com.example.springroll.database.event_id";
    public WeekViewEvent getmEvent() {
        return mEvent;
    }

    public void setmEvent(WeekViewEvent mEvent) {
        this.mEvent = mEvent;
    }

    private boolean mSave = false, mAllDay = false;
    private WeekViewEvent mEvent;
    private Button mStartDateButton, mEndDateButton;
    private EditText mTitle, mLocation;
    private Switch mAllDaySwitch;
    private Calendar calendar, mFromDate, mToDate;
    private int year, month, day, hour,minute,am_pm = -2;

    /**
     public static EventFragment newInstance(UUID eventId){
     Bundle args = new Bundle();
     args.putSerializable(EXTRA_EVENT_ID,eventId);

     EventFragment fragment = new EventFragment();
     fragment.setArguments(args);
     return fragment;
     }*/

    public static EventFragment newInstance(long eventId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_EVENT_ID, eventId);

        EventFragment fragment = new EventFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mEvent = new WeekViewEvent();
        // UUID eventId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_EVENT_ID);
        // UUID eventId = (UUID)getArguments().getSerializable(EXTRA_EVENT_ID);
        long eventId = (long)getArguments().getSerializable(EXTRA_EVENT_ID);
        mEvent = CalEventManager.get(getActivity()).getSingleEvent(eventId); //Need to fix this one

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        //showDate(year, month+1, day);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_fragment_event,container,false);


            if (NavUtils.getParentActivityName(getActivity()) != null) {
               // getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }


        mTitle = (EditText)v.findViewById(R.id.event_title);
        mTitle.setText(mEvent.getName());
        mTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEvent.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This space intentionally left blank
            }
        });

        mLocation = (EditText)v.findViewById(R.id.event_location);
        mLocation.setText(mEvent.getLocation());
        mLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEvent.setLocation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //This space intentionally left blank
            }
        });

        mStartDateButton = (Button)v.findViewById(R.id.start_date_button);
        mStartDateButton.setText(setString(am_pm, mStartDateButton, mAllDay));
        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(mStartDateButton, am_pm);
                mFromDate = Calendar.getInstance();
                mFromDate.set(year, month, day, hour, minute);
                mEvent.setStartTime(mFromDate);
                if(mFromDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                    mSave = false;
                    mStartDateButton.setPaintFlags(mStartDateButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else{
                    mSave = true;
                    mStartDateButton.setPaintFlags(0);
                }
            }
        });

        mEndDateButton = (Button)v.findViewById(R.id.end_date_button);
        mEndDateButton.setText(setString(am_pm, mEndDateButton, mAllDay));
        mEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(mEndDateButton, am_pm);
                mToDate = Calendar.getInstance();
                mToDate.set(year, month, day, hour, minute);
                mEvent.setStartTime(mToDate);
                if(mToDate.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()){
                    mSave = false;
                    mEndDateButton.setPaintFlags(mEndDateButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }else{
                    mSave = true;
                    mEndDateButton.setPaintFlags(0);
                }
            }
        });

        mAllDaySwitch = (Switch)v.findViewById(R.id.switch1);
        mAllDaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAllDay = true;
                    mEvent.setAllDay(true);
                    mStartDateButton.setText(setString(am_pm, mStartDateButton, mAllDay));
                    mEndDateButton.setText(setString(am_pm, mEndDateButton, mAllDay));
                } else {
                    mAllDay = false;
                    mEvent.setAllDay(false);
                    mStartDateButton.setText(setString(am_pm, mStartDateButton, mAllDay));
                    mEndDateButton.setText(setString(am_pm, mEndDateButton, mAllDay));
                }
            }
        });
        return v;
    }

    public StringBuilder setString(int am_pm,Button b,Boolean mAllDay){
        StringBuilder text;
        if(mAllDay){
            text = new StringBuilder().append(month).append("/").append(day).append("/").append(year);
        }
        else if(am_pm != -1 && b.getId() == R.id.start_date_button){
            text = new StringBuilder().append(month).append("/").append(day).append("/").append(year).append("  -  ").append(hour).append(":").append(minute).append(" ").append(am_pm == Calendar.AM ? "AM" : "PM");
        }else{
            text = new StringBuilder().append(month).append("/").append(day).append("/").append(year).append("  -  ").append(hour+1).append(":").append(minute).append(" ").append(am_pm == Calendar.AM ? "AM" : "PM");
        }
        return text;
    }

    private void showDateTimeDialog(Button b, int am_pm) {
        DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getActivity(), false, this);
        pickerDialog.show();
        b.setText(setString(am_pm,b,mAllDay));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_frag, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mSave)
            menu.findItem(R.id.delete_event).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityIntent(getActivity()) != null){
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;

            case R.id.save_event:
                mSave = true;
                return true;

            case R.id.cancel_event:
                return true;

            case R.id.delete_event:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK,null);
    }

    @Override
    public void onDateTimeSelected(int year, int month, int day, int hour, int min, int am_pm) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = min;
        this.am_pm = am_pm;
        //String text = day + "/" + month + "/" + year + " - " + hour + ":" + min;
        // if (am_pm != -1)
        //text = text + (am_pm == Calendar.AM ? "AM" : "PM");
    }

    public static class DateTimePickerFragment extends DialogFragment implements DateTimePickerDialog.DateTimeListener {
        private int year,month,day,am_pm;
        private int hour,minute;
        DateTimePickerDialog.DateTimeListener onDateSet;

        public DateTimePickerFragment(){}

        public void setCallBack(DateTimePickerDialog.DateTimeListener ondate){
            onDateSet = ondate;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DateTimePickerDialog(getActivity(),false,this);
        }

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            year = args.getInt("year");
            month = args.getInt("month");
            day = args.getInt("day");
            hour = args.getInt("hour");
            minute = args.getInt("minute");
        }

        @Override
        public void onDateTimeSelected(int year, int month, int day, int hour, int min, int am_pm) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.minute = min;
            this.am_pm = am_pm;
        }
    }

}



