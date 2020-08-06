package wooteco.subway.maps.map.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wooteco.subway.maps.line.application.LineService;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.line.dto.LineStationResponse;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.domain.SubwayPath;
import wooteco.subway.maps.map.dto.MapResponse;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.dto.PathResponseAssembler;
import wooteco.subway.maps.station.application.StationService;
import wooteco.subway.maps.station.domain.Station;
import wooteco.subway.maps.station.dto.StationResponse;
import wooteco.subway.members.member.domain.LoginMember;

@Service
@Transactional
public class MapService {
    private LineService lineService;
    private StationService stationService;
    private PathService pathService;

    public MapService(LineService lineService, StationService stationService, PathService pathService) {
        this.lineService = lineService;
        this.stationService = stationService;
        this.pathService = pathService;
    }

    public MapResponse findMap() {
        List<Line> lines = lineService.findLines();
        Map<Long, Station> stations = findStations(lines);

        List<LineResponse> lineResponses = lines.stream()
                .map(it -> LineResponse.of(it, extractLineStationResponses(it, stations)))
            .collect(Collectors.toList());

        return new MapResponse(lineResponses);
    }

    public PathResponse findPath(LoginMember loginMember, Long source, Long target, PathType type) {
        List<Line> lines = lineService.findLines();
        SubwayPath subwayPath = pathService.findPath(lines, source, target, type);

        List<Long> ids = subwayPath.extractStationId();
        Map<Long, Station> stations = stationService.findStationsByIdsToMap(ids);

        int lineFare = calculateLineFare(lines, ids);

        return PathResponseAssembler.assemble(subwayPath, stations, loginMember, lineFare);
    }

    private int calculateLineFare(List<Line> lines, List<Long> ids) {
        List<Station> stations = stationService.findStationsByIdsToList(ids);
        return lines.stream()
            .filter(line -> line.isContain(stations))
            .mapToInt(line -> line.getLineFare())
            .max()
            .orElseThrow(IllegalAccessError::new);
    }

    private Map<Long, Station> findStations(List<Line> lines) {
        List<Long> stationIds = lines.stream()
            .flatMap(it -> it.getStationInOrder().stream())
            .map(it -> it.getStationId())
            .collect(Collectors.toList());

        return stationService.findStationsByIdsToMap(stationIds);
    }

    private List<LineStationResponse> extractLineStationResponses(Line line, Map<Long, Station> stations) {
        return line.getStationInOrder().stream()
                .map(it -> LineStationResponse.of(line.getId(), it, StationResponse.of(stations.get(it.getStationId()))))
                .collect(Collectors.toList());
    }
}
