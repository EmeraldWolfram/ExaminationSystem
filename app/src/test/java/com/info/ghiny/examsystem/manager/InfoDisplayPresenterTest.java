package com.info.ghiny.examsystem.manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.info.ghiny.examsystem.PopUpLogin;
import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.Session;
import com.info.ghiny.examsystem.interfacer.InfoDisplayMVP;
import com.info.ghiny.examsystem.model.JavaHost;
import com.info.ghiny.examsystem.model.JsonHelper;
import com.info.ghiny.examsystem.model.ProcessException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Calendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 10/10/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class InfoDisplayPresenterTest {

    private InfoDisplayPresenter manager;
    private InfoDisplayMVP.ViewFace taskView;
    private InfoDisplayMVP.Model taskModel;
    private String MESSAGE_FROM_CHIEF;

    private JavaHost host;
    private ConfigManager configManager;
    private Typeface typeface;

    private View view;
    private ViewGroup parent;
    private TextView paperView;
    private TextView sessionView;
    private TextView venueView;
    private TextView dayView;

    private ExamSubject subject1= new ExamSubject("BAME 1", "SUBJECT 1", 10,
            Calendar.getInstance(), 20, "M4", Session.AM);

    @Before
    public void setUp() throws Exception {
        taskView    = Mockito.mock(InfoDisplayMVP.ViewFace.class);
        taskModel   = Mockito.mock(InfoDisplayMVP.Model.class);
        host        = Mockito.mock(JavaHost.class);

        paperView   = Mockito.mock(TextView.class);
        sessionView = Mockito.mock(TextView.class);
        venueView   = Mockito.mock(TextView.class);
        dayView     = Mockito.mock(TextView.class);

        configManager   = Mockito.mock(ConfigManager.class);
        typeface        = Mockito.mock(Typeface.class);

        manager = new InfoDisplayPresenter(taskView, configManager);
        manager.setTaskModel(taskModel);

    }

    //= OnCreate() =================================================================================
    /**
     * onCreate()
     *
     * This method prepare the exam subjects for display
     * by taking the string passed by the previous activity
     *
     * Tests:
     * 1. MvpModel did not complain about the message, update the list and notify changes
     * 2. MvpModel complain about the message, finish the activity
     *    (As this activity is use for display purposes)
     * 3. Fail to retrieve a message from previous activity, finish the activity
     *    (Not possible, pre-handle in previous activity)
     *
     */
    @Test
    public void testOnCreate1_PositiveTest() throws Exception {
        MESSAGE_FROM_CHIEF = "Exam Paper";
        Intent intent   = Mockito.mock(Intent.class);
        when(intent.getStringExtra(JsonHelper.MINOR_KEY_CANDIDATES)).thenReturn(MESSAGE_FROM_CHIEF);
        doNothing().when(taskModel).updateSubjects(MESSAGE_FROM_CHIEF);
        ExternalDbLoader.setJavaHost(host);

        manager.onCreate(intent);

        verify(taskModel).updateSubjects(MESSAGE_FROM_CHIEF);
        verify(taskView).notifyDataSetChanged();
        verify(taskView, never()).finishActivity();

    }

    @Test
    public void testOnCreate2_NegativeTest() throws Exception {
        MESSAGE_FROM_CHIEF = "Exam Paper";
        Intent intent   = Mockito.mock(Intent.class);
        ProcessException err    = new ProcessException(ProcessException.MESSAGE_TOAST);
        when(intent.getStringExtra(JsonHelper.MINOR_KEY_CANDIDATES)).thenReturn(MESSAGE_FROM_CHIEF);
        doThrow(err).when(taskModel).updateSubjects(MESSAGE_FROM_CHIEF);
        ExternalDbLoader.setJavaHost(host);

        manager.onCreate(intent);

        verify(taskModel).updateSubjects(anyString());
        verify(taskView, never()).notifyDataSetChanged();
        verify(taskView).displayError(err);
    }

    @Test
    public void testOnCreate3_NegativeTest() throws Exception {
        MESSAGE_FROM_CHIEF = "Exam Paper";
        Intent intent   = Mockito.mock(Intent.class);
        NullPointerException err    = new NullPointerException();
        doThrow(err).when(intent).getStringExtra(JsonHelper.MINOR_KEY_CANDIDATES);
        ExternalDbLoader.setJavaHost(host);

        manager.onCreate(intent);

        verify(taskModel, never()).updateSubjects(anyString());
        verify(taskView, never()).notifyDataSetChanged();
        verify(taskView).displayError(any(ProcessException.class));
    }

    //= OnRestart() ================================================================================
    /**
     * onRestart()
     *
     * verify if security prompt was called when the app go through restart
     *
     */
    @Test
    public void testOnRestart() throws Exception {
        manager.onRestart();

        verify(taskView).securityPrompt(false);
    }

    //= OnPasswordReceived() ========================================================================
    /**
     * onPasswordReceived()
     *
     * 1. Password receive is correct, do nothing
     * 2. Password receive is incorrect, display the error and call security prompt again
     *
     */
    @Test
    public void testOnPasswordReceived_1() throws Exception {
        Intent popUpLoginAct = Mockito.mock(Intent.class);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("CORRECT");
        doNothing().when(taskModel).matchPassword("CORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskModel).matchPassword("CORRECT");
        verify(taskView, never()).displayError(any(ProcessException.class));
        verify(taskView, never()).securityPrompt(false);
    }

    @Test
    public void testOnPasswordReceived_2() throws Exception {
        Intent popUpLoginAct = Mockito.mock(Intent.class);
        ProcessException err = new ProcessException(ProcessException.MESSAGE_TOAST);
        when(popUpLoginAct.getStringExtra("Password")).thenReturn("INCORRECT");
        doThrow(err).when(taskModel).matchPassword("INCORRECT");

        manager.onPasswordReceived(PopUpLogin.PASSWORD_REQ_CODE, Activity.RESULT_OK, popUpLoginAct);

        verify(taskModel).matchPassword("INCORRECT");
        verify(taskView).displayError(err);
        verify(taskView).securityPrompt(false);
    }


    //= GetCount() =================================================================================
    @Test
    public void testGetCount() throws Exception {
        manager.getCount();
        verify(taskModel).getNumberOfSubject();
    }

    //= GetItemId() ================================================================================
    @Test
    public void testGetItemId() throws Exception {
        assertEquals(0, manager.getItemId(0));
        assertEquals(1, manager.getItemId(1));
        assertEquals(2, manager.getItemId(2));
        assertEquals(3, manager.getItemId(3));
    }

    //= GetView() ==================================================================================

    /**
     * getView(...)
     *
     * This method is used to setup a exam subject item in the list view
     *
     * Test:
     * 1. When the day count for exam = 0
     * 2. When the day count for exam = -1 (getDaysLeft handle, make sure only -1)
     * 3. When the day count for exam = 1
     * 4. When the day count for exam > 1
     */

    @Test
    public void testGetView1_0Day() throws Exception {
        view        = Mockito.mock(View.class);
        parent      = Mockito.mock(ViewGroup.class);

        when(taskModel.getSubjectAt(0)).thenReturn(subject1);
        when(taskModel.getDaysLeft(any(Calendar.class))).thenReturn(0);

        when(view.findViewById(R.id.paperCodeNameText)).thenReturn(paperView);
        when(view.findViewById(R.id.paperDayText)).thenReturn(dayView);
        when(view.findViewById(R.id.paperVenueText)).thenReturn(venueView);
        when(view.findViewById(R.id.paperSessionText)).thenReturn(sessionView);

        when(configManager.getTypeface(anyString())).thenReturn(typeface);

        manager.getView(0, view, parent);

        verify(paperView).setText(subject1.toString());
        verify(dayView).setText("TODAY");
        verify(venueView).setText(subject1.getExamVenue());
        verify(sessionView).setText(subject1.getPaperSession());
    }

    @Test
    public void testGetView2_NegativeOneDay() throws Exception {
        view        = Mockito.mock(View.class);
        parent      = Mockito.mock(ViewGroup.class);

        when(taskModel.getSubjectAt(0)).thenReturn(subject1);
        when(taskModel.getDaysLeft(any(Calendar.class))).thenReturn(-1);
        when(configManager.getTypeface(anyString())).thenReturn(typeface);

        when(view.findViewById(R.id.paperCodeNameText)).thenReturn(paperView);
        when(view.findViewById(R.id.paperDayText)).thenReturn(dayView);
        when(view.findViewById(R.id.paperVenueText)).thenReturn(venueView);
        when(view.findViewById(R.id.paperSessionText)).thenReturn(sessionView);

        manager.getView(0, view, parent);

        verify(paperView).setText(subject1.toString());
        verify(dayView).setText("ENDED");
        verify(venueView).setText(subject1.getExamVenue());
        verify(sessionView).setText(subject1.getPaperSession());
    }

    @Test
    public void testGetView3_1Day() throws Exception {
        view        = Mockito.mock(View.class);
        parent      = Mockito.mock(ViewGroup.class);

        when(taskModel.getSubjectAt(0)).thenReturn(subject1);
        when(taskModel.getDaysLeft(any(Calendar.class))).thenReturn(1);
        when(configManager.getTypeface(anyString())).thenReturn(typeface);

        when(view.findViewById(R.id.paperCodeNameText)).thenReturn(paperView);
        when(view.findViewById(R.id.paperDayText)).thenReturn(dayView);
        when(view.findViewById(R.id.paperVenueText)).thenReturn(venueView);
        when(view.findViewById(R.id.paperSessionText)).thenReturn(sessionView);

        manager.getView(0, view, parent);

        verify(paperView).setText(subject1.toString());
        verify(dayView).setText("TOMORROW");
        verify(venueView).setText(subject1.getExamVenue());
        verify(sessionView).setText(subject1.getPaperSession());
    }

    @Test
    public void testGetView4_MoreThan1Day() throws Exception {
        view        = Mockito.mock(View.class);
        parent      = Mockito.mock(ViewGroup.class);

        when(taskModel.getSubjectAt(0)).thenReturn(subject1);
        when(taskModel.getDaysLeft(any(Calendar.class))).thenReturn(2);
        when(configManager.getTypeface(anyString())).thenReturn(typeface);

        when(view.findViewById(R.id.paperCodeNameText)).thenReturn(paperView);
        when(view.findViewById(R.id.paperDayText)).thenReturn(dayView);
        when(view.findViewById(R.id.paperVenueText)).thenReturn(venueView);
        when(view.findViewById(R.id.paperSessionText)).thenReturn(sessionView);

        manager.getView(0, view, parent);

        verify(paperView).setText(subject1.toString());
        verify(dayView).setText("2 days later");
        verify(venueView).setText(subject1.getExamVenue());
        verify(sessionView).setText(subject1.getPaperSession());
    }

    //= GetItem(...) ===============================================================================
    /**
     * getItem(...)
     *
     * This method use to return an ExamSubject used by the BaseAdapter in MvpView
     *
     * Test:
     * To call taskModel and find the examsubject with the given index
     *
     * @throws Exception
     */
    @Test
    public void testGetItem() throws Exception {
        ExamSubject subject = new ExamSubject("Code", "Desc", 0,
                Calendar.getInstance(), 10, "Venue", Session.AM);
        when(taskModel.getSubjectAt(0)).thenReturn(subject);

        assertEquals(subject, manager.getItem(0));
        verify(taskModel).getSubjectAt(0);
    }

}