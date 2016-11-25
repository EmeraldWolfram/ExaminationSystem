package com.info.ghiny.examsystem.model;


import com.info.ghiny.examsystem.R;
import com.info.ghiny.examsystem.manager.IconManager;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by GhinY on 17/06/2016.
 */
public class IconManagerTest {

    @Test
    public void testGetIcon_Warning_icon_type() throws Exception {
        IconManager iconManager = new IconManager();
        int test = iconManager.getIcon(IconManager.WARNING);
        assertEquals(R.drawable.warn_icon, test);
    }

    @Test
    public void testGetIcon_Message_icon_type() throws Exception {
        IconManager iconManager = new IconManager();
        int test = iconManager.getIcon(IconManager.MESSAGE);
        assertEquals(R.drawable.msg_icon, test);
    }

    @Test
    public void testGetIcon_Assigned_icon_type() throws Exception {
        IconManager iconManager = new IconManager();
        int test = iconManager.getIcon(IconManager.ASSIGNED);
        assertEquals(R.drawable.entry_icon_2, test);
    }

}