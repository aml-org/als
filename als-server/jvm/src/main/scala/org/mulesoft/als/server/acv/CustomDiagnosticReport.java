package org.mulesoft.als.server.acv;

import java.util.List;

public class CustomDiagnosticReport {
    String profileName;
    List<CustomDiagnosticEntry> entries;

    public CustomDiagnosticReport(String profileName, List<CustomDiagnosticEntry> entries) {
        this.profileName = profileName;
        this.entries = entries;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public List<CustomDiagnosticEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CustomDiagnosticEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "CustomDiagnosticReport{" +
                "profileName='" + profileName + '\'' +
                ", entries=" + entries +
                '}';
    }
}
