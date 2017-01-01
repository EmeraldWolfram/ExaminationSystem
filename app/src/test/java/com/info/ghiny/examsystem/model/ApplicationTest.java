package com.info.ghiny.examsystem.model;

import com.info.ghiny.examsystem.database.ExternalDbLoader;

import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.condition.And;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;

import java.net.Socket;
import java.net.SocketAddress;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
/**
 * Created by user09 on 1/1/2017.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class ApplicationTest {

    private AndroidClient client1;
    private JavaHost host1;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testConnectionOnTwo() throws Exception{


    }

}
