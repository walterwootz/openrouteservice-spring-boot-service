package com.walterwootz.openrouteserviceexample.services;

import org.springframework.stereotype.Service;

@Service
public interface RouteService {
    /**
     * Calculate route duration between two points using matrix API
     * @param source latitude,longitude format e.g. -71.602844,-34.517101
     * @param destination latitude,longitude format e.g. -71.602844,-34.517101
     * @return duration in seconds
     */
    public Double calculateDurationMatrix(String source, String destination);

    /**
     * Calculate route duration between two points using directions API
     * @param source latitude,longitude format e.g. -71.602844,-34.517101
     * @param destination latitude,longitude format e.g. -71.602844,-34.517101
     * @return duration in seconds
     */
    public Double calculateDurationDirections(String source, String destination);
}
