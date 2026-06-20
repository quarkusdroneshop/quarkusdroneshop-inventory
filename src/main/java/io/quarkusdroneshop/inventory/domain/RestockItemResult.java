package io.quarkusdroneshop.inventory.domain;

import io.debezium.outbox.quarkus.ExportedEvent;
import io.quarkusdroneshop.inventory.domain.events.RestockEvent;

import jakarta.enterprise.event.Event;
import java.util.List;

public class RestockItemResult {

    List<RestockInventoryCommand> restockInventoryCommands;

    List<ExportedEvent> restockEvents;

    public RestockItemResult(List<RestockInventoryCommand> restockInventoryCommandList, List<ExportedEvent> restockEventList) {
        this.restockInventoryCommands = restockInventoryCommandList;
        this.restockEvents = restockEventList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RestockItemResult{");
        sb.append("restockInventoryCommands=").append(restockInventoryCommands);
        sb.append(", restockEvents=").append(restockEvents);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestockItemResult that = (RestockItemResult) o;
        if (restockInventoryCommands != null ? !restockInventoryCommands.equals(that.restockInventoryCommands) : that.restockInventoryCommands != null)
            return false;
        int thisSize  = restockEvents != null ? restockEvents.size() : 0;
        int thatSize  = that.restockEvents != null ? that.restockEvents.size() : 0;
        return thisSize == thatSize;
    }

    @Override
    public int hashCode() {
        int result = restockInventoryCommands != null ? restockInventoryCommands.hashCode() : 0;
        result = 31 * result + (restockEvents != null ? restockEvents.size() : 0);
        return result;
    }

    public List<RestockInventoryCommand> getRestockInventoryCommands() {
        return restockInventoryCommands;
    }

    public void setRestockInventoryCommands(List<RestockInventoryCommand> restockInventoryCommands) {
        this.restockInventoryCommands = restockInventoryCommands;
    }

    public List<ExportedEvent> getRestockEvents() {
        return restockEvents;
    }

    public void setRestockEvents(List<ExportedEvent> restockEvents) {
        this.restockEvents = restockEvents;
    }
}
