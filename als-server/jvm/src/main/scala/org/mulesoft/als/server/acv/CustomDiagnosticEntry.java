package org.mulesoft.als.server.acv;

import org.eclipse.lsp4j.Location;

import java.util.List;

public class CustomDiagnosticEntry {
    private String level;
    private String id;
    private String name;
    private String message;
    private Location location;
    private List<TraceEntry> trace;

    public CustomDiagnosticEntry(String level, String id, String name, String message, Location location, List<TraceEntry> trace) {
        this.level = level;
        this.id = id;
        this.name = name;
        this.message = message;
        this.location = location;
        this.trace = trace;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<TraceEntry> getTrace() {
        return trace;
    }

    public void setTrace(List<TraceEntry> trace) {
        this.trace = trace;
    }

    @Override
    public String toString() {
        return "CustomDiagnosticEntry{" +
                "level='" + level + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", location=" + location +
                ", trace=" + trace +
                '}';
    }
}
