package com.info.ghiny.examsystem.manager;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;

import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.StatusFragmentMVP;
import com.info.ghiny.examsystem.interfacer.SubmissionMVP;
import com.info.ghiny.examsystem.model.ProcessException;
import com.info.ghiny.examsystem.model.TakeAttdModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

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

public class StatusPresentPresenterTest {

    private StatusPresentPresenter manager;
    private StatusFragmentMVP.PresentMvpView taskView;
    private SubmissionMVP.MvpModel taskModel;

    private Bitmap image;
    private SharedPreferences preferences;
    private ArrayList<Candidate> presentList;
    
    @Before
    public void setUp() throws Exception {
        taskModel   = Mockito.mock(SubmissionMVP.MvpModel.class);
        taskView    = Mockito.mock(StatusFragmentMVP.PresentMvpView.class);
        image       = Mockito.mock(Bitmap.class);
        preferences = Mockito.mock(SharedPreferences.class);


        manager = new StatusPresentPresenter(image, preferences, taskView);
        manager.setTaskModel(taskModel);
        manager.setPresentList(null);
    }
    
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
        assertNull(manager.getPresentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.PRESENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_ID, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getPresentList());
    }

    @Test
    public void testOnResume_2() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(true);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("2");
        assertNull(manager.getPresentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.PRESENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_NAME, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getPresentList());
    }

    @Test
    public void testOnResume_3() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(true);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("3");
        assertNull(manager.getPresentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.PRESENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_TABLE, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getPresentList());
    }

    @Test
    public void testOnResume_4() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(false);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("1");
        assertNull(manager.getPresentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.PRESENT,
                SortManager.SortMethod.GROUP_PAPER_SORT_ID, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getPresentList());
    }

    @Test
    public void testOnResume_5() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(false);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("2");
        assertNull(manager.getPresentList());

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.PRESENT,
                SortManager.SortMethod.GROUP_PAPER_SORT_NAME, true);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertNotNull(manager.getPresentList());
    }

    @Test
    public void testOnResume_6() throws Exception {
        when(preferences.getBoolean("ProgrammeGrouping", true)).thenReturn(true);
        when(preferences.getBoolean("AscendingSort", true)).thenReturn(true);
        when(preferences.getString("CandidatesSorting", "3")).thenReturn("1");

        TakeAttdModel.setAttdList(null);
        manager = new StatusPresentPresenter(image, preferences, taskView);
        manager.setTaskModel(taskModel);
        ProcessException err = new ProcessException("ERROR", ProcessException.FATAL_MESSAGE,
                IconManager.WARNING);
        assertNull(manager.getPresentList());
        when(taskModel.getCandidatesWith(any(Status.class), any(SortManager.SortMethod.class),
                anyBoolean())).thenThrow(err);

        manager.onResume();

        verify(taskModel).getCandidatesWith(Status.PRESENT,
                SortManager.SortMethod.GROUP_PAPER_GROUP_PROGRAM_SORT_ID, true);
        verify(taskView).displayError(err);
        assertNull(manager.getPresentList());
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
        presentList  = new ArrayList<>();
        presentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU01", "BAME 0001", Status.ABSENT));
        presentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU02", "BAME 0001", Status.ABSENT));
        presentList.add(new Candidate(0, "RMB3", "NAME AAA", "15WAU03", "BAME 0001", Status.ABSENT));
        manager.setPresentList(presentList);
        assertEquals(3, manager.getItemCount());

        presentList.remove(0);
        presentList.remove(1);
        manager.setPresentList(presentList);
        assertEquals(1, manager.getItemCount());
    }

    //= OnLongPress() ==============================================================================

    /**
     * onLongPress()
     *
     * This method set the selected candidate to Late or vice versa depending on the current state
     * of the Candidate Late Field.
     */

    @Test
    public void onLongPressed() throws Exception {
        View view       = Mockito.mock(View.class);
        presentList     = new ArrayList<>();
        Candidate cdd1  = new Candidate(3, "RMB3", "NAME AAA", "15WAU02", "BAME 0001", Status.PRESENT);
        presentList.add(cdd1);
        presentList.add(new Candidate(4, "RMB3", "NAME AAA", "15WAU03", "BAME 0001", Status.PRESENT));
        manager.setPresentList(presentList);
        assertFalse(presentList.get(0).isLate());

        manager.onLongPressed(0, view, true);
        assertTrue(presentList.get(0).isLate());

        manager.onLongPressed(0, view, false);
        assertFalse(presentList.get(0).isLate());
    }

    //= OnClick ====================================================================================
    /**
     *
     * Tests:
     * 1. UNDO button pressed, set the Candidate as ABSENT and show in the Display
     * 2. Error thrown by model (not really possible to happen) when UNDO button pressed, display
     */

    @Test
    public void testOnClick_1() throws Exception {
        Candidate cdd1  = new Candidate(1, "RMB3", "NAME AAA", "15WAU01", "BAME 0001", Status.PRESENT);
        presentList  = new ArrayList<>();
        presentList.add(new Candidate(3, "RMB3", "NAME AAA", "15WAU02", "BAME 0001", Status.PRESENT));
        presentList.add(new Candidate(4, "RMB3", "NAME AAA", "15WAU03", "BAME 0001", Status.PRESENT));
        manager.setPresentList(presentList);
        manager.setTempPosition(1);
        manager.setTempCandidate(cdd1);
        assertEquals(2, presentList.size());

        manager.onClick(Mockito.mock(View.class));

        verify(taskView).insertCandidate(1);
        verify(taskModel).assignCandidate(cdd1);
        verify(taskView, never()).displayError(any(ProcessException.class));
        assertEquals(3, presentList.size());
    }

    @Test
    public void testOnClick_2() throws Exception {
        Candidate cdd1  = new Candidate(1, "RMB3", "NAME AAA", "15WAU01", "BAME 0001", Status.PRESENT);
        presentList  = new ArrayList<>();
        presentList.add(new Candidate(3, "RMB3", "NAME AAA", "15WAU02", "BAME 0001", Status.PRESENT));
        presentList.add(new Candidate(4, "RMB3", "NAME AAA", "15WAU03", "BAME 0001", Status.PRESENT));
        manager.setPresentList(presentList);
        manager.setTempPosition(1);
        manager.setTempCandidate(cdd1);
        assertEquals(2, presentList.size());

        ProcessException err    = new ProcessException(ProcessException.FATAL_MESSAGE);
        doThrow(err).when(taskModel).assignCandidate(cdd1);

        manager.onClick(Mockito.mock(View.class));

        verify(taskView, never()).insertCandidate(1);
        verify(taskModel).assignCandidate(cdd1);
        verify(taskView).displayError(err);
        assertEquals(2, presentList.size());
    }
}