package wooteco.subway.maps.map.domain;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public class SubwayPath {
    public static final int EXTRA_FARE = 100;
    public static final int DISTANCE_LIMIT_10KM = 10;
    public static final int DISTANCE_LIMIT_50KM = 50;
    public static final int DIVIDE_UNIT_10KM = 5;
    public static final int DIVIDE_UNIT_50KM = 8;
    private static final int BASIC_FARE = 1250;
    private static final int MAXIMUM_FARE_UNDER_50KM = 2050;

    private List<LineStationEdge> lineStationEdges;

    public SubwayPath(List<LineStationEdge> lineStationEdges) {
        this.lineStationEdges = lineStationEdges;
    }

    public List<LineStationEdge> getLineStationEdges() {
        return lineStationEdges;
    }

    public List<Long> extractStationId() {
        List<Long> stationIds = Lists.newArrayList(lineStationEdges.get(0).getLineStation().getPreStationId());
        stationIds.addAll(lineStationEdges.stream()
                .map(it -> it.getLineStation().getStationId())
                .collect(Collectors.toList()));

        return stationIds;
    }

    public int calculateDuration() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDuration()).sum();
    }

    public int calculateDistance() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDistance()).sum();
    }

    public int calculateFare(int lineFare) {
        int distanceFare = calculateFareByDistance();
        return distanceFare + lineFare;
    }

    private int calculateFareByDistance() {
        int totalDistance = calculateDistance();
        if (totalDistance < DISTANCE_LIMIT_10KM) {
            return BASIC_FARE;
        } else if (totalDistance < DISTANCE_LIMIT_50KM) {
            return calculateOverFare(totalDistance - DISTANCE_LIMIT_10KM, DIVIDE_UNIT_10KM) * EXTRA_FARE + BASIC_FARE;
        }
        return MAXIMUM_FARE_UNDER_50KM
            + calculateOverFare(totalDistance - DISTANCE_LIMIT_50KM, DIVIDE_UNIT_50KM) * EXTRA_FARE;
    }

    private int calculateOverFare(int overDistance, int divideUnit) {
        return (overDistance - 1) / divideUnit + 1;
    }

    public List<Long> extractLineIds() {
        return lineStationEdges.stream()
            .map(LineStationEdge::getLineId)
            .distinct()
            .collect(Collectors.toList());
    }
}
