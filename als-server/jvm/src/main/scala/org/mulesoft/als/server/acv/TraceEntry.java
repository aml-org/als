package org.mulesoft.als.server.acv;

import java.util.List;
import org.eclipse.lsp4j.Location;


public class TraceEntry {
    private String message;
    private List<TraceEntry> traces;
    private Location location;

    public TraceEntry(String message, List<TraceEntry> traces, Location location) {
        this.message = message;
        this.traces = traces;
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TraceEntry> getTraces() {
        return traces;
    }

    public void setTraces(List<TraceEntry> traces) {
        this.traces = traces;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "TraceEntry{" +
                "message='" + message + '\'' +
                ", traces=" + traces +
                ", location=" + location +
                '}';
    }
}
