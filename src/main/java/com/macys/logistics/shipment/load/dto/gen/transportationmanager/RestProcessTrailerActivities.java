package com.macys.logistics.shipment.load.dto.gen.transportationmanager;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestProcessTrailerActivities {

    protected ApiHeaderType apiHeader;
    protected List<RestTrailerActivityType> trailerActivity;
    public List<RestTrailerActivityType> getTrailerActivity() {
        if (trailerActivity == null) {
            trailerActivity = new ArrayList<>();
        }
        return this.trailerActivity;
    }
}
