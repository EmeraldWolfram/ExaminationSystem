package com.info.ghiny.examsystem.model;

import android.util.Base64;

import com.info.ghiny.examsystem.database.CheckListLoader;
import com.info.ghiny.examsystem.database.ExamSubject;
import com.info.ghiny.examsystem.database.ExternalDbLoader;
import com.info.ghiny.examsystem.database.StaffIdentity;
import com.info.ghiny.examsystem.interfacer.LoginMVP;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by GhinY on 15/06/2016.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class LoginModelTest {
    private LoginModel helper;
    private StaffIdentity staffId;
    private LoginMVP.MPresenter taskPresenter;
    private CheckListLoader dbLoader;
    private TCPClient tcpClient;
    private String MESSAGE_FROM_CHIEF;

    @Before
    public void setUp() throws Exception{
        TCPClient.setConnector(null);
        staffId = new StaffIdentity("12WW", true, "MR. TEST", "H3");

        dbLoader        = Mockito.mock(CheckListLoader.class);
        taskPresenter   = Mockito.mock(LoginMVP.MPresenter.class);
        LoginModel.setStaff(null);

        ConnectionTask connectionTask   = Mockito.mock(ConnectionTask.class);
        tcpClient   = Mockito.mock(TCPClient.class);

        ExternalDbLoader.setConnectionTask(connectionTask);
        ExternalDbLoader.setTcpClient(tcpClient);
        helper  = new LoginModel(taskPresenter, dbLoader);
    }

    //= checkQrId() ================================================================================
    /**
     * checkQrId(scanStr)
     *
     * This method used to check an input string (scanned-in) whether it was possible to
     * be an ID
     *
     * 1. throw MESSAGE_TOAST if the QR scanned is not possible to be an ID
     * 2. do nothing if the scanned string could be an ID
     */
    @Test
    public void testCheckQrId_throw_MESSAGE_TOAST() throws Exception{
        try{
            assertNull(helper.getQrStaffID());

            helper.checkQrId("My name");
            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err) {
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Invalid staff ID Number", err.getErrorMsg());
            assertNull(helper.getQrStaffID());
        }
    }

    @Test
    public void testCheckQrId_Nothing_Happen() throws Exception {
        try{
            assertNull(helper.getQrStaffID());
            helper.checkQrId("012345");
            assertEquals("012345", helper.getQrStaffID());
        } catch (ProcessException err) {
            fail("Expected nothing but thrown " + err.getErrorMsg());
        }
    }

    //= MatchStaffPw() =============================================================================
    /**
     * matchStaffPw(String inputPw)
     *
     * thrown FATAL Message if the staff is not declare yet.
     * In fact, this will never happen.
     * Without Staff declaration, will not prompt for password
     * Make sure the login count does not reduce as the password input by user might be correct
     */
    @Test
    public void testMatchStaffPw_Staff_is_null_should_throw_FATAL_MESSAGE() throws Exception{
        try{
            helper.setQrStaffID(null);

            helper.matchStaffPw(null);

            fail("Expected FATAL_MESSAGE but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Input ID is null", err.getErrorMsg());

            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     * Login count does not reduce as it might be accidentally pressed by user
     */
    @Test
    public void testMatchStaffPw_NULL_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            helper.setQrStaffID(staffId.getIdNo());

            helper.matchStaffPw(null);

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());

            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was empty
     * MESSAGE_TOAST will be thrown
     * Login count does not reduce as it might be accidentally pressed by user
     */
    @Test
    public void testMatchStaffPw_EMPTY_PW_should_throw_MESSAGE_TOAST() throws Exception{
        try{
            helper.setQrStaffID(staffId.getIdNo());

            helper.matchStaffPw("");

            fail("Expected MESSAGE_TOAST but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Please enter a password to proceed", err.getErrorMsg());

            verify(tcpClient, never()).sendMessage(anyString());
        }
    }

    /**
     * matchStaffPw(String inputPw)
     *
     * when the inputPw was valid
     * verification message should be sent to the Chief
     */
    @Test
    public void testMatchStaffPw_Valid_PW_should_send_message() throws Exception{
        try{
            helper.setQrStaffID(staffId.getIdNo());
            when(dbLoader.queryDuelMessage()).thenReturn("DUEL");

            helper.matchStaffPw("ABCD");

            verify(tcpClient).sendMessage(anyString());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    //= CheckLoginResult(...) ======================================================================
    /**
     * checkLoginResult(...)
     *
     * When the chief respond to the verification message sent
     * this method will be called to see the result from the chief
     * whether the password is CORRECT or INCORRECT
     *
     * Tests:
     * 1. Input password was CORRECT when login count > 1, parse the message to get staff object
     * 2. Input password was CORRECT when login count = 1, parse the message to get staff object
     * 3. Input password was INCORRECT when login count > 1, reduce count and throw MESSAGE_TOAST
     * 4. Input password was INCORRECT when login count = 1, reduce count and throw FATAL_MESSAGE
     */
    @Test
    public void testCheckLoginResult1_CorrectPassword() throws Exception {
        try{
            MESSAGE_FROM_CHIEF = "{\"Status\":\"Invigilator\",\"Venue\":\"M4\"," +
                            "\"IdNo\":\"012345\",\"Result\":true,\"Name\":\"STAFF_NAME\"}";

            helper.setLoginCount(3);
            helper.setQrStaffID("012345");
            helper.setInputPW("Password");

            helper.checkLoginResult(MESSAGE_FROM_CHIEF);

            assertEquals(3, helper.getLoginCount());
            assertEquals("STAFF_NAME",  LoginModel.getStaff().getName());
            assertEquals("012345",      LoginModel.getStaff().getIdNo());
            assertEquals("Invigilator", LoginModel.getStaff().getRole().get(0));
            assertEquals("M4",          LoginModel.getStaff().getVenueHandling());
            assertEquals("Password",    LoginModel.getStaff().getPassword());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testCheckLoginResult2_CorrectPassword() throws Exception {
        try{
            MESSAGE_FROM_CHIEF = "{\"Status\":\"Invigilator\",\"Venue\":\"M4\"," +
                    "\"IdNo\":\"012345\",\"Result\":true,\"Name\":\"STAFF_NAME\"}";

            helper.setLoginCount(1);
            helper.setQrStaffID("012345");
            helper.setInputPW("Password");

            helper.checkLoginResult(MESSAGE_FROM_CHIEF);

            assertEquals(3, helper.getLoginCount());
            assertEquals("STAFF_NAME",  LoginModel.getStaff().getName());
            assertEquals("012345",      LoginModel.getStaff().getIdNo());
            assertEquals("Invigilator", LoginModel.getStaff().getRole().get(0));
            assertEquals("M4",          LoginModel.getStaff().getVenueHandling());
            assertEquals("Password",    LoginModel.getStaff().getPassword());
        } catch (ProcessException err){
            fail("No Exception expected but thrown " + err.getErrorMsg());
        }
    }

    @Test
    public void testCheckLoginResult3_IncorrectPassword() throws Exception {
        try{
            MESSAGE_FROM_CHIEF = "{\"Result\":false}";

            helper.setLoginCount(3);
            helper.setQrStaffID("012345");
            helper.setInputPW("Password");

            helper.checkLoginResult(MESSAGE_FROM_CHIEF);

            fail("Expected MESSAGE_TOAST Exception but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.MESSAGE_TOAST, err.getErrorType());
            assertEquals("Incorrect Login Id or Password\n2 attempt left", err.getErrorMsg());
            assertNull(LoginModel.getStaff());
        }
    }

    @Test
    public void testCheckLoginResult4_IncorrectPassword() throws Exception {
        try{
            MESSAGE_FROM_CHIEF = "{\"Result\":false}";

            helper.setLoginCount(1);
            helper.setQrStaffID("012345");
            helper.setInputPW("Password");

            helper.checkLoginResult(MESSAGE_FROM_CHIEF);

            fail("Expected FATAL_MESSAGE Exception but none thrown");
        } catch (ProcessException err){
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("You have failed to login!", err.getErrorMsg());
            assertNull(LoginModel.getStaff());
        }
    }

    @Test
    public void test() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonHelper.KEY_TYPE_RETURN, true);
        //jsonObject.put(StaffIdentity.STAFF_NAME, "STAFF_NAME");
        //jsonObject.put(StaffIdentity.STAFF_ID_NO, "012345");
        //jsonObject.put(StaffIdentity.STAFF_VENUE, "M4");
        //jsonObject.put(StaffIdentity.STAFF_ROLE, "Invigilator");
        JSONArray arr = new JSONArray();
        JSONObject subject1 = new JSONObject();
        subject1.put(ExamSubject.PAPER_CODE, "BAME 0001");
        subject1.put(ExamSubject.PAPER_DESC, "SUBJECT 1");
        subject1.put(ExamSubject.PAPER_VENUE, "M4");
        subject1.put(ExamSubject.PAPER_SESSION, "AM");
        subject1.put(ExamSubject.PAPER_DATE, "10:10:2016");

        JSONObject subject2 = new JSONObject();
        subject2.put(ExamSubject.PAPER_CODE, "BAME 0002");
        subject2.put(ExamSubject.PAPER_DESC, "SUBJECT 2");
        subject2.put(ExamSubject.PAPER_VENUE, "M4");
        subject2.put(ExamSubject.PAPER_SESSION, "AM");
        subject2.put(ExamSubject.PAPER_DATE, "9:10:2016");
        arr.put(subject1);
        arr.put(subject2);

        jsonObject.put(JsonHelper.PAPER_LIST, arr);

        System.out.printf("%s", jsonObject.toString());

    }

    //= Run() ======================================================================================
    /**
     * run()
     *
     * 1. When ConnectionTask is complete, do nothing
     * 2. When ConnectionTask is not complete, throw an error and the presenter shall handle
     */
    @Test
    public void testRun_ChiefDoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(true);
        helper.run();
        verify(taskPresenter, never()).onTimesOut(any(ProcessException.class));
    }

    @Test
    public void testRun_ChiefNoRespond() throws Exception {
        ConnectionTask.setCompleteFlag(false);
        helper.run();
        verify(taskPresenter).onTimesOut(any(ProcessException.class));
    }

    //= HmacSha(...) ============================================================================

    /**
     * hmacSha(...)
     *
     * This method use take two input String and encrypt the 2nd String using the 1st String
     * and return a encrypted String
     *
     * Tests:
     * 1. When the two input string is totally same, should provide the same HashCode
     * 2. When null password detected during the process of encryption, throw FATAL_MESSAGE
     * 3. When null message detected during the process of encryption, throw FATAL_MESSAGE
     */

    @Test
    public void testHmacSha1_PositiveTest() throws Exception {
        String hash         = helper.hmacSha("MyPassword", "7ABB8");
        String sameHash     = helper.hmacSha("MyPassword", "7ABB8");
        String diffHash1    = helper.hmacSha("MyPassword", "7aBB8"); //A -> a
        String diffHash2    = helper.hmacSha("myPassword", "7ABB8"); //M -> m

        assertTrue(hash.equals(sameHash));
        assertFalse(hash.equals(diffHash1));
        assertFalse(hash.equals(diffHash2));
    }

    @Test
    public void testHmacSha3_NegativeTest() throws Exception {
        try{
            String hash         = helper.hmacSha("MyPassword", null);

            fail("Expected FATAL_MESSAGE but none thrown!");
        } catch (ProcessException err) {
            assertEquals(ProcessException.FATAL_MESSAGE, err.getErrorType());
            assertEquals("Encryption library not found\n" +
                    "Please contact developer!", err.getErrorMsg());
        }
    }
}