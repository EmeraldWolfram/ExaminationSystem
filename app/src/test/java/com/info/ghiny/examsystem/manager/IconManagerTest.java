package com.info.ghiny.examsystem.manager;

import com.info.ghiny.examsystem.R;

import org.junit.Test;

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

public class IconManagerTest {

    @Test
    public void testGetIcon_Warning_icon_type() throws Exception {
        IconManager iconManager = new IconManager();
        int test = iconManager.getIcon(IconManager.WARNING);
        assertEquals(R.drawable.other_warn_icon, test);
    }

    @Test
    public void testGetIcon_Message_icon_type() throws Exception {
        IconManager iconManager = new IconManager();
        int test = iconManager.getIcon(IconManager.MESSAGE);
        assertEquals(R.drawable.other_msg_icon, test);
    }

    @Test
    public void testGetIcon_Assigned_icon_type() throws Exception {
        IconManager iconManager = new IconManager();
        int test = iconManager.getIcon(IconManager.ASSIGNED);
        assertEquals(R.drawable.other_entry_icon_2, test);
    }
}