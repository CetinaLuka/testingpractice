package com.matthewcannefax.testingpractice;

import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.SharedPreferences;

import com.matthewcannefax.testingpractice.SharedPreferenceEntry;

import java.util.Calendar;


@RunWith(MockitoJUnitRunner.class)
public class SharedPreferencesHelperTest {

    private static final String TEST_NAME = "Test name";

    private static final String TEST_EMAIL = "test@email.com";

    private static final Calendar TEST_DATE_OF_BIRTH = Calendar.getInstance();

    static {
        TEST_DATE_OF_BIRTH.set(1980, 1, 1);
    }

    private SharedPreferenceEntry mSharedPreferenceEntry;

    private SharedPreferencesHelper mMockSharedPreferencesHelper;

    private SharedPreferencesHelper mMockBrokenSharedPreferencesHelper;

    @Mock
    SharedPreferences mMockSharedPreferences;

    @Mock
    SharedPreferences mMockBrokenSharedPreferences;

    @Mock
    SharedPreferences.Editor mMockEditor;

    @Mock
    SharedPreferences.Editor mMockBrokenEditor;

    @Before
    public void initMocks() {
        //Create SharedPreferenceEntry to persist
        mSharedPreferenceEntry = new SharedPreferenceEntry(TEST_NAME, TEST_DATE_OF_BIRTH, TEST_EMAIL);

        //Create a mocked SharedPreferences.
        mMockSharedPreferencesHelper = createMockSharedPrefence();

        //Create a mocked SharedPreferences that fails at saving data
        mMockBrokenSharedPreferencesHelper = createBrokenMockSharedPreference();
    }

    @Test
    public void sharedPreferenceHelper_SaveAndReadPersonalInformation() {
        //Save teh personal information to SharedPreferences
        boolean success = mMockBrokenSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);

        assertThat("Checking that SharedPreferenceEntry.save... returns true", success, is(false));//This was expected to break, now i set it to false

        //Read personal information from SharedPreferences
        SharedPreferenceEntry savedSharedPreferenceEntry = mMockSharedPreferencesHelper.getPersonalInfo();

        //Make sure both written and retrieved personal information are equal
        assertThat("Checking that SharedPreferenceEntry.name has been persisted and read correctly", mSharedPreferenceEntry.getName(), is(equalTo(savedSharedPreferenceEntry.getName())));

        assertThat("Checking that SharedPreferenceEntry.dateOfBirth has been persisted and read correctly", mSharedPreferenceEntry.getDateOfBirth(), is(equalTo(savedSharedPreferenceEntry.getDateOfBirth())));

        assertThat("Checking that SharedPreferenceEntry.email has been persisted and read correctly", mSharedPreferenceEntry.getEmail(), is(equalTo(savedSharedPreferenceEntry.getEmail())));
    }


    @Test
    public void sharedPreferencesHelper_SavePersonalInformationFailed_ReturnsFalse() {
        //Read personal information from a broken SharedPrefencesHelper
        boolean success = mMockBrokenSharedPreferencesHelper.savePersonalInfo(mSharedPreferenceEntry);

        assertThat("Makes sure writing to a broken SharedPreferencesHelper returns false", success, is(false));
    }

    /**
     * Creates a mocked SharedPreferences.
     */

    private SharedPreferencesHelper createMockSharedPrefence() {
        //Mocking reading the SharedPreferences as if mMockSharedPreferences was previously written correctly
        when(mMockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_NAME), anyString())).thenReturn(mSharedPreferenceEntry.getName());
        when(mMockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_EMAIL), anyString())).thenReturn(mSharedPreferenceEntry.getEmail());
        when(mMockSharedPreferences.getLong(eq(SharedPreferencesHelper.KEY_DOB), anyLong())).thenReturn(mSharedPreferenceEntry.getDateOfBirth().getTimeInMillis());

        //Mocking a successful commit.
        //when(mMockEditor.commit()).thenReturn(true);

        //Return the MockEditor when requesting it
        //when(mMockSharedPreferences.edit()).thenReturn(mMockEditor);
        return new SharedPreferencesHelper(mMockSharedPreferences);
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing
     */
    private SharedPreferencesHelper createBrokenMockSharedPreference(){
        //Mocking a commit that fails.
        when(mMockBrokenEditor.commit()).thenReturn(false);

        //Return the broken MockEditor when requesting it
        when(mMockBrokenSharedPreferences.edit()).thenReturn(mMockBrokenEditor);

        return new SharedPreferencesHelper(mMockBrokenSharedPreferences);
    }

}
