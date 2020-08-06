package wooteco.subway.maps.map.acceptance;

import static wooteco.subway.maps.line.acceptance.step.LineStationAcceptanceStep.*;
import static wooteco.subway.maps.map.acceptance.step.PathAcceptanceStep.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.common.acceptance.AcceptanceTest;
import wooteco.subway.maps.line.acceptance.step.LineAcceptanceStep;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.station.acceptance.step.StationAcceptanceStep;
import wooteco.subway.maps.station.dto.StationResponse;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 목동역;
    private Long 오목교역;
    private Long 신정역;

    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        교대역 = 지하철역_등록되어_있음("교대역");
        강남역 = 지하철역_등록되어_있음("강남역");
        양재역 = 지하철역_등록되어_있음("양재역");
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역");
        목동역 = 지하철역_등록되어_있음("목동역");
        오목교역 = 지하철역_등록되어_있음("오목교역");
        신정역 = 지하철역_등록되어_있음("신정역");

        이호선 = 지하철_노선_등록되어_있음("2호선", "GREEN", "200");
        신분당선 = 지하철_노선_등록되어_있음("신분당선", "RED", "100");
        삼호선 = 지하철_노선_등록되어_있음("3호선", "ORANGE", "300");

        지하철_노선에_지하철역_등록되어_있음(이호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(이호선, 교대역, 강남역, 2, 2);
        지하철_노선에_지하철역_등록되어_있음(이호선, 강남역, 오목교역, 10, 5);

        지하철_노선에_지하철역_등록되어_있음(신분당선, null, 강남역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 강남역, 양재역, 2, 1);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 양재역, 목동역, 50, 10);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 목동역, 신정역, 10, 7);

        지하철_노선에_지하철역_등록되어_있음(삼호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 1, 2);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 남부터미널역, 양재역, 2, 2);
    }

    @DisplayName("거리별 요금을 계산한다.")
    @Test
    void distanceFareTest() {
        //when
        ExtractableResponse<Response> response1 = 거리_경로_조회_요청("DISTANCE", 2L, 6L);
        ExtractableResponse<Response> response2 = 거리_경로_조회_요청("DISTANCE", 1L, 6L);
        ExtractableResponse<Response> response3 = 거리_경로_조회_요청("DISTANCE", 3L, 5L);
        ExtractableResponse<Response> response4 = 거리_경로_조회_요청("DISTANCE", 2L, 5L);
        ExtractableResponse<Response> response5 = 거리_경로_조회_요청("DISTANCE", 2L, 7L);

        //then
        적절한_경로를_응답(response1, Lists.newArrayList(강남역, 오목교역));
        총_거리와_소요_시간을_함께_응답함(response1, 10, 5);
        validateFare(response1, 1450);

        적절한_경로를_응답(response2, Lists.newArrayList(교대역, 강남역, 오목교역));
        총_거리와_소요_시간을_함께_응답함(response2, 12, 7);
        validateFare(response2, 1650);

        적절한_경로를_응답(response3, Lists.newArrayList(양재역, 목동역));
        총_거리와_소요_시간을_함께_응답함(response3, 50, 10);
        validateFare(response3, 2350);

        적절한_경로를_응답(response4, Lists.newArrayList(강남역, 양재역, 목동역));
        총_거리와_소요_시간을_함께_응답함(response4, 52, 11);
        validateFare(response4, 2450);

        적절한_경로를_응답(response5, Lists.newArrayList(강남역, 양재역, 목동역, 신정역));
        총_거리와_소요_시간을_함께_응답함(response5, 62, 18);
        validateFare(response5, 2550);
    }

    @DisplayName("두 역의 최단 거리 경로를 조회한다.")
    @Test
    void findPathByDistance() {
        //when
        ExtractableResponse<Response> response = 거리_경로_조회_요청("DISTANCE", 1L, 3L);

        //then
        적절한_경로를_응답(response, Lists.newArrayList(교대역, 남부터미널역, 양재역));
        총_거리와_소요_시간을_함께_응답함(response, 3, 4);
    }

    @DisplayName("두 역의 최소 시간 경로를 조회한다.")
    @Test
    void findPathByDuration() {
        //when
        ExtractableResponse<Response> response = 거리_경로_조회_요청("DURATION", 1L, 3L);
        //then
        적절한_경로를_응답(response, Lists.newArrayList(교대역, 강남역, 양재역));
        총_거리와_소요_시간을_함께_응답함(response, 4, 3);
    }

    private Long 지하철_노선_등록되어_있음(String name, String color, String fare) {
        ExtractableResponse<Response> createLineResponse1 = LineAcceptanceStep.지하철_노선_등록되어_있음(name, color, fare);
        return createLineResponse1.as(LineResponse.class).getId();
    }

    private Long 지하철역_등록되어_있음(String name) {
        ExtractableResponse<Response> createdStationResponse1 = StationAcceptanceStep.지하철역_등록되어_있음(name);
        return createdStationResponse1.as(StationResponse.class).getId();
    }
}
