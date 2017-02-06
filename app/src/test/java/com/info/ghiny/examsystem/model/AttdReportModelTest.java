package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.AttendanceList;
import com.info.ghiny.examsystem.database.Candidate;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.database.Status;
import com.info.ghiny.examsystem.interfacer.AttdReportMVP;
import com.info.ghiny.examsystem.view_holder.ProgrammeDisplayHolder;
import com.info.ghiny.examsystem.view_holder.StatusDisplayHolder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

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
public class AttdReportModelTest {

    private AttendanceList attdList;
    private AttdReportModel model;
    private AttdReportMVP.MvpMPresenter taskPresenter;

    @Before
    public void setUp() throws Exception {
        attdList = new AttendanceList();
        attdList.addCandidate(new Candidate(12, "RMB3", "Candidate A", "15WAR00001", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(15, "RMA3", "Candidate B", "15WAR00002", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(14, "RMB3", "Candidate C", "15WAR00003", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(13, "RMA3", "Candidate D", "15WAR00004", "BAME 0001", Status.PRESENT));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate E", "15WAR00005", "BAME 0001", Status.ABSENT));
        attdList.addCandidate(new Candidate(0, "RMA3", "Candidate F", "15WAR00006", "BAME 0001", Status.ABSENT));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate G", "15WAR00007", "BAME 0001", Status.BARRED));
        attdList.addCandidate(new Candidate(0, "RMA3", "Candidate H", "15WAR00008", "BAME 0001", Status.EXEMPTED));
        attdList.addCandidate(new Candidate(0, "RMB3", "Candidate I", "15WAR00009", "BAME 0001", Status.QUARANTINED));
        TakeAttdModel.setAttdList(attdList);
        LoginModel.setStaff(new StaffIdentity("012", true, "STAFF_1", "M4"));


        taskPresenter   = Mockito.mock(AttdReportMVP.MvpMPresenter.class);
        model           = new AttdReportModel(taskPresenter);

    }

    @Test
    public void getDisplayMap() throws Exception {
        HashMap<ProgrammeDisplayHolder, List<StatusDisplayHolder>> map = model.getDisplayMap(model.getDisplayHeader());

        assertEquals(2, map.size());
        for(ProgrammeDisplayHolder key : map.keySet()){
            List<StatusDisplayHolder> child    = map.get(key);
            for(int i = 0; i < child.size(); i++){
                StatusDisplayHolder item    = child.get(i);
                System.out.printf("%s %d\n", item.getStatus().toString(), item.getQuantity());
            }
        }

    }

    @Test
    public void getDisplayHeader() throws Exception {

        List<ProgrammeDisplayHolder> list  = model.getDisplayHeader();

        assertEquals(2, list.size());
        assertEquals("RMB3", list.get(0).getProgramme());
        assertEquals(Integer.valueOf(5), list.get(0).getTotal());
        assertEquals("RMA3", list.get(1).getProgramme());
        assertEquals(Integer.valueOf(4), list.get(1).getTotal());

    }

}