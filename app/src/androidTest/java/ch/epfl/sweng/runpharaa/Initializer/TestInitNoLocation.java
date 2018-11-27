package ch.epfl.sweng.runpharaa.Initializer;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import ch.epfl.sweng.runpharaa.firebase.Database;
import ch.epfl.sweng.runpharaa.Firebase.Storage;
import ch.epfl.sweng.runpharaa.login.firebase.FirebaseAuthentication;
import ch.epfl.sweng.runpharaa.login.google.GoogleAuthentication;

public class TestInitNoLocation {

    @BeforeClass
    public static void setTestModeOn() {
        Database.isTest = true;
        Storage.isTest = true;
        GoogleAuthentication.isTest = true;
        FirebaseAuthentication.isTest = true;
    }

    @AfterClass
    public static void setTestModeOff() {
        Database.isTest = false;
        Database.isTest = false;
        GoogleAuthentication.isTest = false;
        FirebaseAuthentication.isTest = false;
    }
}
