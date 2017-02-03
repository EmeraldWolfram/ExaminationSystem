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
