# 성능 점검 이슈

전체 레이어 readback을 작은 샘플 레이어 readback으로 바꾸었다. 코드 경로·샘플 크기 검증과 실제 프레임/메모리 성능 계측은 구분한다.

| ID | 우선순위 | 이슈 이름 | 위치 | 해결 방향 | 상태 |
| --- | --- | --- | --- | --- | --- |
| PERF-001 | P2 | 자동 대비 분석이 정지 화면에서도 전체 레이어 비트맵을 반복 생성 | [hig/src/commonMain/kotlin/zone/ien/hig/CupertinoLiquidButton.kt:163](../../hig/src/commonMain/kotlin/zone/ien/hig/CupertinoLiquidButton.kt#L163)<br>[hig/src/commonMain/kotlin/zone/ien/hig/CupertinoTopBars.kt:523](../../hig/src/commonMain/kotlin/zone/ien/hig/CupertinoTopBars.kt#L523)<br>[hig/src/commonMain/kotlin/zone/ien/hig/Luminance.kt:20](../../hig/src/commonMain/kotlin/zone/ien/hig/Luminance.kt#L20) | 수정: 버튼·상단 바의 대비 루프가 전체 레이어를 CPU로 읽기 전에 관심 영역을 별도 5×5 GraphicsLayer에 렌더링한다. remembered 샘플 레이어를 재사용하고 작은 레이어에만 toImageBitmap을 호출한다. 1000×500 원본의 영역별 명도 및 실제 샘플 크기 5×5를 Desktop·Wasm 테스트로 검증했다. 300ms 갱신 주기는 유지한다. 프레임 시간·GPU 비용·메모리 감소량은 계측하지 않았으므로 수치 개선은 주장하지 않는다. | 해결 완료 |
