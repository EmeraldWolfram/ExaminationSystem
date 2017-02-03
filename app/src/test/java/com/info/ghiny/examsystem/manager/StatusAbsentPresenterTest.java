package com.info.ghiny.examsystem.manager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.StatusFragmentMVP;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Copyright (C) 2016 - 2017 Steven Foong Ghin Yew <stevenfgy@yahoo.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class StatusAbsentPresenterTest {

    private StatusAbsentPresenter manager;
    private StatusFragmentMVP.AbsentMvpView taskView;
    private SubmissionMVP.MvpModel taskModel;

    private Bitmap image;
    private SharedPreferences preferences;
    private ArrayList<Candidate> absentList;


    @Before
    public void setUp() throws Exception {
        taskModel   = Mockito.mock(SubmissionMVP.MvpModel.class);
        taskView    = Mockito.mock(StatusFragmentMVP.AbsentMvpView.class);
        image       = Mockito.mock(Bitmap.class);
        preferences = Mockito.mock(SharedPreferences.class);


        manager = new StatusAbsentPresenter(image, preferences, taskView);
        manager.setTaskModel(taskModel);
        manager.setAbsentList(null);
    }

    @After
    public void tearDown() throws Exception {}

    //= OnResume ===================================================================================

    /**
     * onResume()
     *
     * This method load the setting and see is the preference for sorting have changes
     * It then restructure the sorting the AbsentList
     *
     * Tests:
     * 1. Initialize the absent list with Ascending, Grouped & ID sorting
     * 2. Initialize the absent list with Ascending, Grouped & Name sorting
     * 3. Initialize the absent list with Ascending, Grouped & Table sorting
     * 4. Initialize the absent list with Ascending & ID sorting
     * 5. Initialize the absent list with Ascending & Name sorting
     * 6. Attendance List is null, Throw FATAL_ERROR
     *
     */
    @Test
    public void testOnResume_1() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(true);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("1");
        assertNull(manager.getAbsentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.ABSENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_ID, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getAbsentList());
    }

    @Test
    public void testOnResume_2() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(true);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("2");
        assertNull(manager.getAbsentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.ABSENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_NAME, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getAbsentList());
    }

    @Test
    public void testOnResume_3() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(true);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("3");
        assertNull(manager.getAbsentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.ABSENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_TABLE, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getAbsentList());
    }

    @Test
    public void testOnResume_4() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(false);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("1");
        assertNull(manager.getAbsentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.ABSENT,
                SortManager.SortMethod.GROUP_PAPER_SORT_ID, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getAbsentList());
    }

    @Test
    public void testOnResume_5() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(false);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("2");
        assertNull(manager.getAbsentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.ABSENT,
                SortManager.SortMethod.GROUP_PAPER_SORT_NAME, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getAbsentList());
    }

    @Test
    public void testOnResume_6() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(true);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("1");

        TakeAttdModel.setAttdList(null);
        manager = new StatusAbsentPresenter(image, preferences, taskView);
        manager.setTaskModel(taskModel);
        ProcessException err = new ProcessException("ERROR", ProcessException.FATAL_MESSAGE,
                IconManager.WARNING);
        assertNull(manager.getAbsentList());
        when(taskModel.getCandidatesWith(any(Status.class), any(SortManager.SortMethod.class),
                anyBoolean())).thenThrow(err);

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.ABSENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_ID, true);
        verify(taskView).displayError(err);
        assertNull(manager.getAbsentList());
    }

    //= GetItemCount ===============================================================================

    /**
     * getItemCount()
     *
     * Return the number of item in the absent list
     *
     */

    @Test
    public void testGetItemCount() throws Exception {
        absentList  = new ArrayList<>();
        absentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU01", "BAME 0001", Status.ABSENT));
        absentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU02", "BAME 0001", Status.ABSENT));
        absentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU03", "BAME 0001", Status.ABSENT));
        manager.setAbsentList(absentList);
        assertEquals(3, manager.getItemCount());

        absentList.remove(0);
        absentList.remove(1);
        manager.setAbsentList(absentList);
        assertEquals(1, manager.getItemCount());
    }

    //= OnMove =====================================================================================

    /**
     * onMove()
     *
     * this method is here for extension but not used in this system
     **/

    //= OnClick ====================================================================================
     /**
     *
     * Tests:
     * 1. UNDO button pressed, set the Candidate as ABSENT and show in the Display
     * 2. Error thrown by model (not really possible to happen) when UNDO button pressed, display
     */

    @Test
    public void testOnClick_1() throws Exception {
        Candidate cdd1  = new Candidate(0, "RMB3", "NAME AAA", "15WAU01", "BAME 0001", Status.ABSENT);
        absentList  = new ArrayList<>();
        absentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU02", "BAME 0001", Status.ABSENT));
        absentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU03", "BAME 0001", Status.ABSENT));
        manager.setAbsentList(absentList);
        manager.setTempPosition(0);
        manager.setTempCandidate(cdd1);
        assertEquals(2, absentList.size());

        manager.onClick(Mockito.mock(View.class));

        verify(taskView).insertCandidate(0);
        verify(taskModel).unassignCandidate(0, cdd1);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertEquals(3, absentList.size());
    }

    @Test
    public void testOnClick_2() throws Exception {
        Candidate cdd1  = new Candidate(0, "RMB3", "NAME AAA", "15WAU01", "BAME 0001", Status.ABSENT);
        absentList  = new ArrayList<>();
        absentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU02", "BAME 0001", Status.ABSENT));
        absentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU03", "BAME 0001", Status.ABSENT));
        manager.setAbsentList(absentList);
        manager.setTempPosition(0);
        manager.setTempCandidate(cdd1);
        assertEquals(2, absentList.size());

        ProcessException err    = new ProcessException(ProcessException.FATAL_MESSAGE);
        doThrow(err).when(taskModel).unassignCandidate(0, cdd1);

        manager.onClick(Mockito.mock(View.class));

        verify(taskView, never()).insertCandidate(0);
        verify(taskModel).unassignCandidate(0, cdd1);
        verify(taskView).displayError(err);
        assertEquals(2, absentList.size());
    }

}