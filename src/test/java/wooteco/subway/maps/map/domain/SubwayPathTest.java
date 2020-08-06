package wooteco.subway.maps.map.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;

public class SubwayPathTest {
	private Line line1 = new Line(200);
	private Line line2 = new Line(300);

	private LineStationEdge 목동역 = new LineStationEdge(new LineStation(1L, null, 0, 0), 1L);
	private LineStationEdge 오목교역 = new LineStationEdge(new LineStation(2L, 1L, 10, 5), 1L);
	private LineStationEdge 신길역 = new LineStationEdge(new LineStation(3L, 2L, 50, 20), 1L);
	private LineStationEdge 덕정역 = new LineStationEdge(new LineStation(4L, null, 10, 20), 2L);
	private LineStationEdge 의정부역 = new LineStationEdge(new LineStation(5L, 4L, 20, 20), 2L);

	private List<LineStationEdge> lineStationEdges = new ArrayList<>();

	@BeforeEach
	public void setUp() {
		lineStationEdges.add(목동역);
		lineStationEdges.add(오목교역);
		lineStationEdges.add(신길역);
		lineStationEdges.add(덕정역);
		lineStationEdges.add(의정부역);
	}

	@DisplayName("지하철 경로의 요금을 계산한다.")
	@Test
	void calculateFareByDistanceTest() {
		SubwayPath subwayPath = new SubwayPath(lineStationEdges);
		int moreExpensiveLineFare = Math.max(line1.getLineFare(), line2.getLineFare());

		assertThat(subwayPath.calculateFare(moreExpensiveLineFare)).isEqualTo(2850);
	}
}
